package com.spinteg.lv.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private String fullName;
    private String email;
    private java.util.Collection<String> roles;
}
