package com.cypherfund.campaign.user.dal.repository;

import com.cypherfund.campaign.user.dal.entity.TFirebaseToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TFirebaseTokenRepository extends JpaRepository<TFirebaseToken, Integer> {
    boolean existsByFirebaseTokenAndUserId(String token, String userId);
}
