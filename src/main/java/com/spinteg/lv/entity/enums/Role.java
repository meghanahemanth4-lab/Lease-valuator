package com.spinteg.lv.entity.enums;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ROLE_SUPER_ADMIN,
    ROLE_DEPUTY_ADMIN,
    ROLE_CONTRACT_TYPE_CREATOR,
    ROLE_LEASE_CREATOR,
    ROLE_DEPLOYER;

    @Override
    public String getAuthority() {
        return name();
    }
}
