package com.cypherfund.campaign.user.dal.repository;

import com.cypherfund.campaign.user.dal.entity.TUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TUserRepository extends JpaRepository<TUser, String> {
    Optional<TUser> findFirstByUsernameOrEmailOrPhone(String username, String email, String phone);
    Optional<TUser> findFirstByUsernameOrEmail(String username, String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    boolean existsByPhoneOrEmailOrUsername(String username, String email, String phone);
    Optional<TUser> findByEmail(String email);
}
