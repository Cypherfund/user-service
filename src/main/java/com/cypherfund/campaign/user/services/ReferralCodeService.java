package com.cypherfund.campaign.user.services;

import com.cypherfund.campaign.user.dal.entity.TUser;
import com.cypherfund.campaign.user.dal.repository.TUserRepository;
import com.cypherfund.campaign.user.dal.repository.TransactionRepository;
import com.cypherfund.campaign.user.exceptions.NotFoundException;
import com.cypherfund.campaign.user.model.ReferredUser;
import com.cypherfund.campaign.user.model.UserReferralData;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.List;

import static com.cypherfund.campaign.user.utils.Enumerations.TRANSACTION_TYPE.REFERRAL_REWARD;

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
    private final TransactionRepository transactionRepository;

    @Cacheable(value = "referralCode", key = "#userId")
    public UserReferralData getUserReferralData(String userId) {
        TUser user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        UserReferralData userReferralData = new UserReferralData();
        userReferralData.setReferralCode(getReferralCode(userId));
        userReferralData.setReferralLink("https://auth.task.tech-ascend.com?referral_code=" + user.getReferralCode());
        List<ReferredUser> referredUsers = userRepository.getReferralData(user.getReferralCode())
                .stream()
                .map(d -> {
                    ReferredUser referredUser = new ReferredUser();
                    referredUser.setUsername((String) d[0]);
                    referredUser.setCompleted(((BigDecimal) d[2]).compareTo(BigDecimal.valueOf(1000)) >= 0);
                    referredUser.setCollected(transactionRepository.existsByReferenceAndType((String) d[1], REFERRAL_REWARD));
                    referredUser.setCoins(((BigDecimal) d[1]).intValue());
                    return referredUser;
                })
                .toList();
        userReferralData.setReferredUsers(referredUsers);

        return userReferralData;
    }

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
