package com.cypherfund.campaign.user.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Enumerations {

    public enum USER_ROLES {
        CUSTOMER, ADMIN, SUPER_ADMIN, DISTRIBUTOR, TALENT, CLIENT, TUTOR, STUDENT;
        private static final Set<String> ROLE_NAMES = new HashSet<>();

        static {
            for (USER_ROLES role : USER_ROLES.values()) {
                ROLE_NAMES.add(role.name());
            }
        }

        public static boolean areAllRolesPresent(List<String> roles) {
            for (String role : roles) {
                if (!ROLE_NAMES.contains(role)) {
                    return false; // Role not found
                }
            }
            return true; // All roles found
        }
    }
    public enum USER_STATUS {
        active, blocked, suspended, disabled
    }
    public enum  AUTH_PRIVIDERS {
        local,
        facebook,
        google,
        github,
        tiktok
    }

    public enum USER {
        USER_EXISTS, USER_NOT_EXISTS
    }
}
