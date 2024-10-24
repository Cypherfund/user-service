package com.cypherfund.campaign.user.services;

import com.cypherfund.campaign.user.dal.entity.TUser;
import com.cypherfund.campaign.user.dal.repository.TUserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

/**
 * Author: E.Ngai
 * Date: 10/24/2024
 * Time: 4:58 PM
 **/
@Service
@RequiredArgsConstructor
public class ReferralCodeService {

    private static final int REFERRAL_CODE_LENGTH = 5;
    private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    private static final String CHARACTERS = LETTERS + DIGITS;
    private final SecureRandom random = new SecureRandom();

    private final TUserRepository userRepository;

    public String getReferralCode(String userId) {
        TUser user = userRepository.findById(userId).orElseThrow();
        if (StringUtils.isNotBlank(user.getReferralCode())) {
            return user.getReferralCode();
        }
        String referralCode;
        do {
            referralCode = generateCodeWithAtLeastOneDigit();

        } while (isReferralCodeUsed(referralCode)); // Ensure uniqueness

        user.setReferralCode(referralCode);
        userRepository.save(user);

        return referralCode;
    }

    private boolean isReferralCodeUsed(String referralCode) {
        return userRepository.existsByReferralCode(referralCode);
    }

    private String generateCodeWithAtLeastOneDigit() {
        StringBuilder referralCode = new StringBuilder(REFERRAL_CODE_LENGTH);

        referralCode.append(" ".repeat(REFERRAL_CODE_LENGTH));

        int digitPosition = random.nextInt(REFERRAL_CODE_LENGTH);
        referralCode.setCharAt(digitPosition, DIGITS.charAt(random.nextInt(DIGITS.length())));

        for (int i = 0; i < REFERRAL_CODE_LENGTH; i++) {
            if (i != digitPosition) {
                referralCode.setCharAt(i, CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
            }
        }


        return referralCode.toString();
    }
}
