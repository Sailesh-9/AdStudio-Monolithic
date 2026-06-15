package com.cts.adstudio.iam.dto.response;

import com.cts.adstudio.iam.enums.Role;
import com.cts.adstudio.iam.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Safe view of a user (never exposes the password). */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private Long userId;
    private String name;
    private String email;
    private String phone;
    private Role role;
    private Long accountId;
    private UserStatus status;
}
