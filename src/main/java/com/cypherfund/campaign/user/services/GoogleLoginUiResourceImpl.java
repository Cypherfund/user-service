package com.cypherfund.campaign.user.services;

import com.cypherfund.campaign.user.controller.GoogleLoginUiResource;
import com.cypherfund.campaign.user.dal.entity.TProfile;
import com.cypherfund.campaign.user.dal.entity.TUser;
import com.cypherfund.campaign.user.dal.repository.TProfileRepository;
import com.cypherfund.campaign.user.dal.repository.TUserRepository;
import com.cypherfund.campaign.user.dto.Enumerations;
import com.cypherfund.campaign.user.model.ApiResponse;
import com.cypherfund.campaign.user.model.GoogleSignupRequest;
import com.cypherfund.campaign.user.model.JwtAuthenticationResponse;
import com.cypherfund.campaign.user.model.SignUpRequest;
import com.cypherfund.campaign.user.security.JwtTokenProvider;
import com.cypherfund.campaign.user.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.Optional;

import static com.cypherfund.campaign.user.utils.Enumerations.SOCIAL_MEDIA_PLATFORM.google;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleLoginUiResourceImpl implements GoogleLoginUiResource {
    private final JwtTokenProvider jwtTokenProvider;
    private final TUserRepository userRepository;
    private final LoginUiResourceImpl loginUiResourceImpl;
    private final TProfileRepository profileRepository;
    @Override
    public void initiateGoogleLogin(String body) {

    }

    @Override
    public ApiResponse<JwtAuthenticationResponse> initiateGoogleLoginWithCode(String body) {
        return null;
    }

    @Override
    public ResponseEntity<JwtAuthenticationResponse> completeGoogleLogin(GoogleSignupRequest body) {
        try {
            log.info("User google info: " + body);

            Optional<TProfile> profileOptional = profileRepository.findByAccountIdAndSocialMediaAccount(body.getId(), google);
            //if a profile exist with this user simply log the user in
            if (profileOptional.isPresent()) {
                String userId = profileOptional.get().getUser().getUserId();
                log.info("google User already exist, logging in user: " + userId);

                return loginUser(profileOptional.get());
            }

            String password = body.getUsername();
            String username = "@" + body.getUsername();
            String email = body.getEmail();

            SignUpRequest signUpRequest = new SignUpRequest();

            signUpRequest.setRoles(Collections.singletonList("CUSTOMER"));
            signUpRequest.setPassword(password);
            log.info("image url: , image length" + body.getImageUrl().length());
            log.info("display name: " + body.getName());
            signUpRequest.setImageUrl(body.getImageUrl());

            signUpRequest.setEmail(email);
            signUpRequest.setUsername(username);

            //if user already exists in the system, just create another profile for the user
            Optional<TUser> userOptional = userRepository.findFirstByUsernameOrEmail(username, email);

            TUser user = userOptional.orElse(loginUiResourceImpl.createUser(signUpRequest, Enumerations.AUTH_PRIVIDERS.tiktok));

            TProfile profile = new TProfile();
            profile.setSocialMediaAccount(google);
            profile.setAccountName(body.getName());
            profile.setAccountId(body.getId());
            profile.setImgUrl(body.getImageUrl());
            profile.setUser(user);
            profile.setDtCreatedAt(Instant.now());
            profileRepository.save(profile);

            return loginUser(profile);
        } catch (Exception e) {
            log.error("Failed to complete google login", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @Override
    public void googleUpdates(String body) {

    }

    @NotNull
    private ResponseEntity<JwtAuthenticationResponse> loginUser(TProfile tProfile) {
        UserPrincipal userPrincipal = UserPrincipal.create(tProfile.getUser());
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
        String jwt = jwtTokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt, userPrincipal.getId()));
    }
}
