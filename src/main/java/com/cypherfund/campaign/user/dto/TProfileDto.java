package com.cypherfund.campaign.user.dto;

import com.cypherfund.campaign.user.utils.Enumerations;
import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link com.cypherfund.campaign.user.dal.entity.TProfile}
 */
@Getter
@Setter
@NoArgsConstructor
public class TProfileDto implements Serializable {
    Integer id;
    private String userUserId;
    @NotNull
    Enumerations.SOCIAL_MEDIA_PLATFORM socialMediaAccount;
    @NotNull
    @Size(max = 100)
    String accountName;
    @NotNull
    @Size(max = 100)
    String accountId;
    @NotNull
    @Size(max = 255)
    String accessToken;
    @NotNull
    Instant dtCreatedAt;
    @Size(max = 500)
    String imgUrl;
}