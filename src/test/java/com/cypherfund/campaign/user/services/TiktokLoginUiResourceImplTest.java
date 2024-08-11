package com.cypherfund.campaign.user.services;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TiktokLoginUiResourceImplTest {
    @Autowired TiktokLoginUiResourceImpl tiktokLoginUiResource;

    @Test
    void completeTiktokLogin() {
        String code = "rk2KAl2oY52dfirTNMVmToI1lnfkqNgSk_e-S-rA809E_Cd7IIDL6UrdZ47KrZldB2w-FysuUjOB1yvqfs6hZBNgZJQeDpLeXrTkCIcVKaBOgcf7DR2_srQoO8hTIS-oi9PaHnE9Y4vyXAfgtOSKubK9BpDS_6vW4vYYLoxGwP3sXaSKVd1wzzuuDqZL6J_-8Jhc2av7gVpfoO4M7UotDDjXIYhnIRVGC252OPp3e3nuc7z-vFIlSpw8ve2rM-Q1*0!4600.va";
        String state = "c8f6f7e4-604e-47c4-8bd9-6640434f70be";
        String scopes = "scopes";
        String error = null;
        String errorDescription = null;
        String csrfState = "c8f6f7e4-604e-47c4-8bd9-6640434f70be";
        tiktokLoginUiResource.completeTiktokLogin(code, state, scopes, error, errorDescription, csrfState);
    }

    @Test
    @SneakyThrows
    void getToken() {
        String code = "t_7nuQ2oplKvwlBZzh4aAJNBWNComVswYWKw6FShOb9gYp7e8j7jzwlpw68EL-DCk6ct1LR2i1ltrWLl3Ql9HRvRtHoBIFRd66C7_vIMGz6eQSemM07e9dkLU8DRd1vefuwrvqzhboaXxhvYwIZgXA-FjjVL2zVwRl2CmzahnlkVfMXu6Qp0OGuhSHfwJSOmVaJ-MdI2ZX-Pu3-r0GITdK1BIkZ3r3Hhz0UbFDCVMvJHlfkqVV-GvVHNkvbVwBgJ*2!4585.va";
        tiktokLoginUiResource.getToken(code);
    }
}