package com.amazonwh.wms.dto;

import lombok.Data;
import java.util.List;

@Data
public class UserDto {
    private Long id;
    private String username;
    private String password;
    private String fullName;
    private String email;
    private Long dcId;
    private String dcCode;
    private List<String> roles;
    private Boolean active;
}
