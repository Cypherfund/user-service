package com.cypherfund.campaign.user.dal.repository;

import com.cypherfund.campaign.user.dal.entity.TProfile;
import com.cypherfund.campaign.user.utils.Enumerations;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TProfileRepository extends JpaRepository<TProfile, Integer> {
    Optional<TProfile> findByAccountIdAndSocialMediaAccount(String accountId, Enumerations.SOCIAL_MEDIA_PLATFORM socialMediaAccount);

    List<TProfile> findByUser_UserId(String userId);
}
