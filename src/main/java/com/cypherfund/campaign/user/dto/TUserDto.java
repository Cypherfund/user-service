package com.cypherfund.campaign.user.dto;

import com.cypherfund.campaign.user.dal.entity.TRole;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

/**
 * DTO for {@link com.cypherfund.campaign.user.dal.entity.TUser}
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TUserDto implements Serializable {
    @Size(max = 50)
    String userId;
    @Size(max = 255)
    String name;
    @NotNull
    @Size(max = 255)
    String username;
    @Size(max = 15)
    String phone;
    @Size(max = 15)
    String status;
    Instant dtCreated;
    Instant dtUpdated;
    @Size(max = 40)
    String email;
    List<String> roles;
    @Size(max = 15)
    String strLoginProvider;
}
