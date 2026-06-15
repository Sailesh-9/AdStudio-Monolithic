package com.cts.adstudio.iam.dto.response;

import com.cts.adstudio.iam.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Returned on successful login: the JWT plus basic identity info. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {
    private String token;
    private String tokenType;
    private Long userId;
    private String name;
    private String email;
    private Role role;
    private long expiresInMs;
}
