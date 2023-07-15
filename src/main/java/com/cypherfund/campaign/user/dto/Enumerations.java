package com.cypherfund.campaign.user.dto;

import java.io.Serializable;
import java.time.Instant;

public class Enumerations {
    public enum USER_ROLES {
        CUSTOMER, ADMIN, MANAGER, SPECTATOR, DISTRIBUTOR
    }
    public enum USER_STATUS {
        active, blocked, suspended, disabled
    }
    public enum  AUTH_PRIVIDERS {
        local,
        facebook,
        google,
        github
    }
    /**
     * DTO for {@link TUser}
     */
    public static record TUserDto(String userId, String name, String username, String phone, String status, Instant dtCreated,
                                  String email) implements Serializable {
    }
}
