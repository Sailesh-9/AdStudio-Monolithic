package com.cts.adstudio.iam.enums;

/**
 * Roles a user can hold in AdStudio. Stored as text in the DB.
 * Mapped to Spring Security authorities as "ROLE_" + name().
 */
// stored in db as string-text via conversion
public enum Role {
    BRAND_ADVERTISER,
    MEDIA_PLANNER,
    CREATIVE_MANAGER,
    DELIVERY_PUBLISHER,
    FINANCE_EXECUTIVE,
    ADMIN
}