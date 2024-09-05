package com.cypherfund.campaign.user.dal.repository;

import com.cypherfund.campaign.user.dal.entity.TTiktokLogin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TTiktokLoginRepository extends JpaRepository<TTiktokLogin, Integer> {
    Optional<TTiktokLogin> findFirstByUserIdOrderByDtCreatedAtDesc(String userId);
}
