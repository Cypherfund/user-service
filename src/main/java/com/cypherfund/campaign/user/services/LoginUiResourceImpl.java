package com.cypherfund.campaign.user.services;

import com.cypherfund.campaign.api.exceptions.AppException;
import com.cypherfund.campaign.api.model.ApiResponse;
import com.cypherfund.campaign.user.dal.entity.TRole;
import com.cypherfund.campaign.user.dal.entity.TRoleUser;
import com.cypherfund.campaign.user.dal.entity.TRoleUserRepository;
import com.cypherfund.campaign.user.dal.entity.TUser;
import com.cypherfund.campaign.user.dal.repository.TRoleRepository;
import com.cypherfund.campaign.user.dal.repository.TUserRepository;
import com.cypherfund.campaign.user.dto.Enumerations;
import com.cypherfund.campaign.api.user.model.JwtAuthenticationResponse;
import com.cypherfund.campaign.api.user.model.LoginRequest;
import com.cypherfund.campaign.api.user.model.SignUpRequest;
import com.cypherfund.campaign.user.security.JwtTokenProvider;
import com.cypherfund.campaign.api.user.ui.LoginUiResource;
import jakarta.transaction.Transactional;
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
import java.util.Date;
import java.util.UUID;

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
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse> registerUser(SignUpRequest signUpRequest) {
        if(userRepository.existsByUsername(signUpRequest.getUsername())) {
            return new ResponseEntity<>(new ApiResponse(false, "Username is already taken!", ""),
                    HttpStatus.CONFLICT);
        }

        if(userRepository.existsByEmail(signUpRequest.getEmail())) {
            return new ResponseEntity<>(new ApiResponse(false, "Email Address already in use!", ""),
                    HttpStatus.CONFLICT);
        }

        if(userRepository.existsByPhone(signUpRequest.getPhone())) {
            return new ResponseEntity<>(new ApiResponse(false, "Phone number already in use!", ""),
                    HttpStatus.CONFLICT);
        }
        // Creating user's account
        TUser user = new TUser();
        user.setUserId(UUID.randomUUID().toString());
        user.setName(signUpRequest.getName());
        user.setUsername(signUpRequest.getUsername());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setEmail(signUpRequest.getEmail());
        user.setPhone(signUpRequest.getPhone());
        user.setDtCreated(new Date().toInstant());
        user.setStatus(Enumerations.USER_STATUS.active.name());
        user.setStrLoginProvider(Enumerations.AUTH_PRIVIDERS.local.name());
        TRole userRole = roleRepository.findById(Enumerations.USER_ROLES.CUSTOMER.name())
                .orElseThrow(() -> new AppException("CUSTOMER Role not set."));

        TRoleUser tRoleUser = new TRoleUser();
        tRoleUser.setUser(user);
        tRoleUser.setLgRole(userRole);

        TUser result = userRepository.save(user);
        tRoleUserRepository.save(tRoleUser);


        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/users/{username}")
                .buildAndExpand(result.getUsername()).toUri();

        return ResponseEntity.created(location).body(new ApiResponse(true, "User registered successfully", "x"));
    }
}
