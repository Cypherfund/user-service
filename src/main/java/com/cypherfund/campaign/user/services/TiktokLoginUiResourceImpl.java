package com.cypherfund.campaign.user.services;

import com.cypherfund.campaign.user.controller.TiktokLoginUiResource;
import com.cypherfund.campaign.user.dal.entity.TProfile;
import com.cypherfund.campaign.user.dal.entity.TUser;
import com.cypherfund.campaign.user.dal.repository.TProfileRepository;
import com.cypherfund.campaign.user.dal.repository.TUserRepository;
import com.cypherfund.campaign.user.dto.Enumerations;
import com.cypherfund.campaign.user.model.*;
import com.cypherfund.campaign.user.security.JwtTokenProvider;
import com.cypherfund.campaign.user.security.UserPrincipal;
import com.google.gson.Gson;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.cypherfund.campaign.user.utils.Enumerations.SOCIAL_MEDIA_PLATFORM.tiktok;

@Slf4j
@Service
@RequiredArgsConstructor
public class TiktokLoginUiResourceImpl implements TiktokLoginUiResource {
    @Value("${spring.security.oauth2.client.registration.tiktok.client-id}")
    private String clientId;
    @Value("${spring.security.oauth2.client.registration.tiktok.client-secret}")
    private String clientSecret;
    @Value("${spring.security.oauth2.client.registration.tiktok.redirect-uri}")
    private String redirectUri;

    @Value("${spring.security.oauth2.client.provider.tiktok.authorization-uri}")
    private String authorizationUri;
    @Value("${spring.security.oauth2.client.provider.tiktok.token-uri}")
    private String tokenUri;
    @Value("${spring.security.oauth2.client.provider.tiktok.user-info-uri}")
    private String userInfoUri;

    private final LoginUiResourceImpl loginUiResourceImpl;
    private final TProfileRepository profileRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final JwtTokenProvider jwtTokenProvider;
    private final TUserRepository userRepository;

    @Override
    public ResponseEntity<JwtAuthenticationResponse> initiateTiktokLogin(HttpServletResponse response,
                                                                         String username,
                                                                         String email,
                                                                         String redirectUrl) {
        // Generate CSRF state token
        String csrfState = UUID.randomUUID().toString();
        Cookie csrfCookie = new Cookie("csrfState", csrfState);
        csrfCookie.setMaxAge(600); // 10 minutes
        csrfCookie.setHttpOnly(true); // Prevents JavaScript access
        csrfCookie.setPath("/"); // Ensures it's available across your application
        csrfCookie.setSecure(false); // Use if your app is running over HTTPS
        response.addCookie(csrfCookie);

        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setEmail(username);
        signUpRequest.setUsername(email);
        signUpRequest.setRedirectUrl(redirectUrl);

        redisTemplate.opsForValue().set("TEMP:LOGIN:TIKTOK:"+csrfState, signUpRequest);
        redisTemplate.expire("TEMP:LOGIN:TIKTOK:"+csrfState, 600, TimeUnit.SECONDS);

        // Build TikTok authorization URL
        String url = UriComponentsBuilder.fromUriString(authorizationUri)
                .queryParam("client_key", clientId)
                .queryParam("scope", "user.info.basic")
                .queryParam("response_type", "code")
                .queryParam("redirect_uri", redirectUri.replaceAll("\"", ""))
                .queryParam("state", csrfState)
                .build()
                .toUriString();

        // Redirect to TikTok authorization URL
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(url));
        return ResponseEntity.status(302).headers(headers).build();
    }

    @Override
    @SneakyThrows
    public ResponseEntity<JwtAuthenticationResponse> completeTiktokLogin(String code, String state, String scopes, String error, String errorDescription, String csrfState) {
        if (error != null) {
            log.error("Error: " + error + ", Description: " + errorDescription);
        }

        SignUpRequest signUpRequest = (SignUpRequest) redisTemplate.opsForValue().get("TEMP:LOGIN:TIKTOK:"+state);
        log.info("Sign up request: " + signUpRequest);

        if (code == null) {
            log.error("Authorization code not found");
            return null;
        }

        if (signUpRequest == null) {
            log.error("Sign up request not found");
            return null;
        }

        TiktokTokenResponse response = getToken(code);

        if (response == null || response.getAccess_token() == null) {
            log.error("Failed to get access token {}", response);
            return null;
        }

        redisTemplate.delete("TEMP:LOGIN:TIKTOK:"+state);

        // Save access token to database
        log.info("Access token: " + response);
        TiktokUserResponse tiktokUserResponse = getUserInfo(response.getAccess_token());
        log.info("User info: " + tiktokUserResponse);

        Optional<TProfile> profileOptional = profileRepository.findByAccountIdAndSocialMediaAccount(tiktokUserResponse.getData().getUser().getUnion_id(), tiktok);
        //if a profile exist with this user simply log the user in
        if (profileOptional.isPresent()) {
            profileOptional.get().setAccessToken(response.getAccess_token());
            profileRepository.save(profileOptional.get());

            log.info("User already exists, logging in");

            ResponseEntity<JwtAuthenticationResponse> loginResponse = loginUser(profileOptional.get());

            return redirectUserIfRedirectUrlPresent(signUpRequest, loginResponse);
        }

        signUpRequest.setRoles(Collections.singletonList("CUSTOMER"));
        signUpRequest.setPassword(signUpRequest.getUsername());
        log.info("image url: , image length" + tiktokUserResponse.getData().getUser().getAvatar_url(), tiktokUserResponse.getData().getUser().getAvatar_url().length());
        log.info("display name: " + tiktokUserResponse.getData().getUser().getDisplay_name());
        signUpRequest.setImageUrl(tiktokUserResponse.getData().getUser().getAvatar_url());

        //if use already exist in the system, just create another profile for the user
        Optional<TUser> userOptional = userRepository.findByUsernameOrEmailOrPhone(signUpRequest.getUsername(), signUpRequest.getEmail(), signUpRequest.getPhone());

        TUser user = userOptional.orElse(loginUiResourceImpl.createUser(signUpRequest, Enumerations.AUTH_PRIVIDERS.tiktok));

        TProfile profile = new TProfile();
        profile.setSocialMediaAccount(tiktok);
        profile.setAccountName(tiktokUserResponse.getData().getUser().getDisplay_name());
        profile.setAccountId(tiktokUserResponse.getData().getUser().getUnion_id());
        profile.setAccessToken(response.getAccess_token());
        profile.setUser(user);
        profile.setDtCreatedAt(Instant.now());
        profileRepository.save(profile);

        ResponseEntity<JwtAuthenticationResponse> loginResponse =  loginUser(profile);

        return redirectUserIfRedirectUrlPresent(signUpRequest, loginResponse);

    }

    private ResponseEntity<JwtAuthenticationResponse> redirectUserIfRedirectUrlPresent(SignUpRequest signUpRequest, ResponseEntity<JwtAuthenticationResponse> loginResponse) {
        if (signUpRequest.getRedirectUrl() != null) {
            log.info("Redirecting user to: " + signUpRequest.getRedirectUrl());
            String redirectUrl = signUpRequest.getRedirectUrl() + "?token=" + Objects.requireNonNull(loginResponse.getBody()).getAccessToken();
            log.info("Redirect URL: " + redirectUrl);
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(URI.create(redirectUrl));
            return ResponseEntity.status(302).headers(headers).build();
        }

        log.info("No redirect URL found, returning login response");

        return loginResponse;
    }

    @NotNull
    private ResponseEntity<JwtAuthenticationResponse> loginUser(TProfile tProfile) {
        UserPrincipal userPrincipal = UserPrincipal.create(tProfile.getUser());
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
        String jwt = jwtTokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt, userPrincipal.getId()));
    }

    @Override
    public void tiktokUpdates(String body) {

    }

    public TiktokUserResponse getUserInfo(String accessToken) {
        // Set up the headers with the access token
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        // Create an HTTP entity with headers
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Use RestTemplate to make the API call
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<TiktokUserResponse> response = restTemplate.exchange(
                userInfoUri + "?fields=open_id,union_id,avatar_url, display_name",
                HttpMethod.GET,
                entity,
                TiktokUserResponse.class
        );

        log.info("User info response: " + response.getBody());

        // Return the response body, which contains the user info
        return response.getBody();
    }

    public TiktokTokenResponse getToken(String code) throws Exception {
        OkHttpClient client = new OkHttpClient();

        String urlEncodedBody =
                "client_key" + "=" + clientId +
                "&client_secret" + "=" + clientSecret +
                "&code" + "=" + code +
                "&grant_type" + "=" + "authorization_code" +
                "&redirect_uri" + "=" + redirectUri.replaceAll("\"", "");

        RequestBody body = RequestBody.create(urlEncodedBody.getBytes(StandardCharsets.UTF_8));

        Request request = new Request.Builder()
                .url(tokenUri)
                .post(body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) throw new IOException("Unexpected code " + response);
            String responseBody = response.body().string();
            return new Gson().fromJson(responseBody, TiktokTokenResponse.class);
        }

    }
}
