package com.cts.adstudio.iam.enums;

/**
 * Account status of a user.
 * Login is allowed only for ACTIVE users; INACTIVE/SUSPENDED are rejected.
 */
public enum UserStatus {
    ACTIVE,
    INACTIVE,
    SUSPENDED
}
