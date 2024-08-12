package com.cypherfund.campaign.user.services;

import com.cypherfund.campaign.user.controller.LoginUiResource;
import com.cypherfund.campaign.user.dal.entity.TRole;
import com.cypherfund.campaign.user.dal.entity.TRoleUser;
import com.cypherfund.campaign.user.dal.repository.TRoleUserRepository;
import com.cypherfund.campaign.user.dal.entity.TUser;
import com.cypherfund.campaign.user.dal.repository.TRoleRepository;
import com.cypherfund.campaign.user.dal.repository.TUserRepository;
import com.cypherfund.campaign.user.dto.Enumerations;
import com.cypherfund.campaign.user.exceptions.AppException;
import com.cypherfund.campaign.user.model.ApiResponse;
import com.cypherfund.campaign.user.model.JwtAuthenticationResponse;
import com.cypherfund.campaign.user.model.LoginRequest;
import com.cypherfund.campaign.user.model.SignUpRequest;
import com.cypherfund.campaign.user.security.JwtTokenProvider;
import com.cypherfund.campaign.user.security.UserPrincipal;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.cypherfund.campaign.user.dto.Enumerations.USER_ROLES.areAllRolesPresent;

@RestController
public class LoginUiResourceImpl implements LoginUiResource {
    final AuthenticationManager authenticationManager;
    final JwtTokenProvider tokenProvider;
    final TUserRepository userRepository;

    final TRoleRepository roleRepository;
    final TRoleUserRepository tRoleUserRepository;

    final PasswordEncoder passwordEncoder;
    public LoginUiResourceImpl(AuthenticationManager authenticationManager,
                               JwtTokenProvider tokenProvider,
                               TUserRepository userRepository,
                               TRoleRepository roleRepository,
                               TRoleUserRepository tRoleUserRepository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.tRoleUserRepository = tRoleUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public ResponseEntity<JwtAuthenticationResponse> authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsernameOrEmailOrPhone(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt, ((UserPrincipal) authentication.getPrincipal()).getId()));
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse> registerUser(SignUpRequest signUpRequest) {
        if(StringUtils.isNotBlank(signUpRequest.getUsername()) && userRepository.existsByUsername(signUpRequest.getUsername())) {
            return new ResponseEntity<>(new ApiResponse(false, "Username is already taken!", ""),
                    HttpStatus.CONFLICT);
        }

        if(StringUtils.isNotBlank(signUpRequest.getEmail()) && userRepository.existsByEmail(signUpRequest.getEmail())) {
            return new ResponseEntity<>(new ApiResponse(false, "Email Address already in use!", ""),
                    HttpStatus.CONFLICT);
        }

        if(StringUtils.isNotBlank(signUpRequest.getPhone()) && userRepository.existsByPhone(signUpRequest.getPhone())) {
            return new ResponseEntity<>(new ApiResponse(false, "Phone number already in use!", ""),
                    HttpStatus.CONFLICT);
        }

        if(StringUtils.isBlank(signUpRequest.getUsername())
                && StringUtils.isBlank(signUpRequest.getEmail())
                && StringUtils.isBlank(signUpRequest.getPhone())) {
            return new ResponseEntity<>(new ApiResponse(false, "Username, Email or Phone is required!", ""),
                    HttpStatus.BAD_REQUEST);
        }

        // Creating user's account
        TUser result = createUser(signUpRequest, Enumerations.AUTH_PRIVIDERS.local);


        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/users/{username}")
                .buildAndExpand(result.getUsername()).toUri();

        return ResponseEntity.created(location).body(new ApiResponse(true, "User registered successfully", "x"));
    }

    public TUser createUser(SignUpRequest signUpRequest, Enumerations.AUTH_PRIVIDERS authPrividers) {
        TUser user = new TUser();
        user.setUserId(UUID.randomUUID().toString());
        user.setName(signUpRequest.getName());
        user.setUsername(signUpRequest.getUsername());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setEmail(signUpRequest.getEmail());
        user.setPhone(signUpRequest.getPhone());
        user.setDtCreated(new Date().toInstant());
        user.setStatus(Enumerations.USER_STATUS.active.name());
        user.setStrLoginProvider(authPrividers.name());
        user.setImgUrl(signUpRequest.getImageUrl());

        boolean allRolesPresent = areAllRolesPresent(signUpRequest.getRoles());

        if (!allRolesPresent) {
            throw new AppException("Invalid role not set.");
        }

        List<TRoleUser> tRoleUsers = new ArrayList<>();
        for (String role :
                signUpRequest.getRoles()) {
            TRole userRole = roleRepository.findById(role.toUpperCase())
                    .orElseThrow(() -> new AppException( role.toUpperCase() + " Role not set."));
            TRoleUser tRoleUser = new TRoleUser();
            tRoleUser.setUser(user);
            tRoleUser.setLgRole(userRole);
            tRoleUsers.add(tRoleUser);
        }

        TUser result = userRepository.save(user);
        tRoleUserRepository.saveAll(tRoleUsers);
        return result;
    }
}
