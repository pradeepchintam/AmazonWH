package com.amazonwh.wms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data @AllArgsConstructor
public class AuthResponse {
    private String token;
    private String username;
    private String fullName;
    private List<String> roles;
}
