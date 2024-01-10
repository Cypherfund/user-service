package com.cypherfund.campaign.user.services;

import com.cypherfund.campaign.user.controller.UserUiResource;
import com.cypherfund.campaign.user.dal.entity.TUser;
import com.cypherfund.campaign.user.dal.repository.TUserRepository;
import com.cypherfund.campaign.user.dto.Enumerations;
import com.cypherfund.campaign.user.dto.TUserDto;
import com.cypherfund.campaign.user.exceptions.NotFoundException;
import com.cypherfund.campaign.user.model.ApiResponse;
import com.cypherfund.campaign.user.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
        if (userRepository.existsByPhoneOrEmailOrUsername(username, username, username)) {
            throw new RuntimeException("user already exists with username {}");
        }
        return  ResponseEntity.ok(new ApiResponse<>(true, "", ""));
    }

    @Override
    public ResponseEntity<?> validateToken(String token) {
        String userId = tokenProvider.getUserIdFromJWT(token);
        TUserDto userDto = userRepository.findById(userId)
                .map(this::mapToUserDto)
                .orElseThrow(() -> new NotFoundException("user not found"));

        return ResponseEntity.ok(new ApiResponse<>(true, "", userDto));
    }

    private TUserDto mapToUserDto(TUser user) {
        return new ModelMapper().map(user, TUserDto.class);
    }
}
