package com.cypherfund.campaign.user.services;

import com.cypherfund.campaign.user.controller.UserUiResource;
import com.cypherfund.campaign.user.dal.entity.TUser;
import com.cypherfund.campaign.user.dal.repository.TUserRepository;
import com.cypherfund.campaign.user.dto.Enumerations;
import com.cypherfund.campaign.user.exceptions.NotFoundException;
import com.cypherfund.campaign.user.model.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserUiResourceImpl implements UserUiResource {
    final
    TUserRepository userRepository;

    public UserUiResourceImpl(TUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public ResponseEntity<ApiResponse<List<Enumerations.TUserDto>>> getAllUsers(int page, int limit) {
        List<Enumerations.TUserDto> users = userRepository.findAll()
                .stream()
                .map(this::mapToUserDto)
                .toList();
        return ResponseEntity.ok(new ApiResponse<>(true, "", users));
    }

    @Override
    public ResponseEntity<?> getUserbyID(String id) {
        Enumerations.TUserDto userDto = userRepository.findById(id)
                .map(this::mapToUserDto)
                .orElseThrow(() -> new NotFoundException("user not found"));
        return  ResponseEntity.ok(new ApiResponse<>(true, "", userDto));
    }

    @Override
    public ResponseEntity<ApiResponse<Enumerations.TUserDto>> getUser(String username) {
        Enumerations.TUserDto userDto = userRepository.findByUsernameOrEmailOrPhone(username, username, username)
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
        return null;
    }

    private Enumerations.TUserDto mapToUserDto(TUser user) {
        return new Enumerations.TUserDto(
                user.getUserId(),
                user.getName(),
                user.getUsername(),
                user.getPhone(),
                user.getStatus(),
                user.getDtCreated(),
                user.getEmail()
        );
    }
}
