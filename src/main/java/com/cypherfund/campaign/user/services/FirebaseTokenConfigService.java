package com.cypherfund.campaign.user.services;

import com.cypherfund.campaign.user.dal.entity.TFirebaseToken;
import com.cypherfund.campaign.user.dal.repository.TFirebaseTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Author: E.Ngai
 * Date: 10/24/2024
 * Time: 1:15 PM
 **/
@Service
@RequiredArgsConstructor
public class FirebaseTokenConfigService {
    private final TFirebaseTokenRepository firebaseTokenRepository;

    public void saveToken(String token, String userId) {
        if (!firebaseTokenRepository.existsByFirebaseTokenAndUserId(token, userId)) {
            firebaseTokenRepository.save(new TFirebaseToken(token, userId));
        }
    }
}
