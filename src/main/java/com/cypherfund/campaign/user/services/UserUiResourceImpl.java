package com.cypherfund.campaign.user.services;

import com.cypherfund.campaign.user.controller.UserUiResource;
import com.cypherfund.campaign.user.dal.entity.TRoleUser;
import com.cypherfund.campaign.user.dal.entity.TUser;
import com.cypherfund.campaign.user.dal.repository.TUserRepository;
import com.cypherfund.campaign.user.dto.Enumerations;
import com.cypherfund.campaign.user.dto.TUserDto;
import com.cypherfund.campaign.user.exceptions.NotFoundException;
import com.cypherfund.campaign.user.model.ApiResponse;
import com.cypherfund.campaign.user.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserUiResourceImpl implements UserUiResource {
    final TUserRepository userRepository;
    final JwtTokenProvider tokenProvider;

    @Override
    public ResponseEntity<ApiResponse<List<TUserDto>>> getAllUsers(int page, int limit) {
        List<TUserDto> users = userRepository.findAll()
                .stream()
                .map(this::mapToUserDto)
                .toList();
        return ResponseEntity.ok(new ApiResponse<>(true, "", users));
    }

    @Override
    public ResponseEntity<?> getUserbyID(String id) {
        TUserDto userDto = userRepository.findById(id)
                .map(this::mapToUserDto)
                .orElseThrow(() -> new NotFoundException("user not found"));
        return  ResponseEntity.ok(new ApiResponse<>(true, "", userDto));
    }

    @Override
    public ResponseEntity<ApiResponse<TUserDto>> getUser(String username) {
        TUserDto userDto = userRepository.findByUsernameOrEmailOrPhone(username, username, username)
                .map(this::mapToUserDto)
                .orElseThrow(() -> new NotFoundException("user not found"));
        return  ResponseEntity.ok(new ApiResponse<>(true, "", userDto));
    }

    @Override
    public ResponseEntity<ApiResponse<String>> validUsername(String username) {
        boolean userExists = userRepository.existsByPhoneOrEmailOrUsername(username, username, username);
        return  ResponseEntity.ok(new ApiResponse<>(true, "", userExists ? Enumerations.USER.USER_EXISTS.name() : Enumerations.USER.USER_NOT_EXISTS.name()));
    }

    @Override
    public ResponseEntity<?> validateToken(String token) {
        log.info("Validating token {}", token);
        String userId = tokenProvider.getUserIdFromJWT(token);
        TUserDto userDto = userRepository.findById(userId)
                .map(this::mapToUserDto)
                .orElseThrow(() -> new NotFoundException("user not found"));

        return ResponseEntity.ok(new ApiResponse<>(true, "", userDto));
    }

    private TUserDto mapToUserDto(TUser user) {
        TUserDto userDto = new ModelMapper().map(user, TUserDto.class);
        userDto.setRoles(user.getLgRole().stream().map(tRoleUser -> tRoleUser.getLgRole().getLgRoleId()).toList());
        return userDto;
    }
}
