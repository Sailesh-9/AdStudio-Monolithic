package com.cts.adstudio.iam.entity;

import com.cts.adstudio.iam.enums.Role;
import com.cts.adstudio.iam.enums.UserStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Application user. One row per person, regardless of role.
 * Role drives RBAC. Table named "users" because USER is reserved in MySQL.
 */
@Entity
@Table(
        name = "users",
        uniqueConstraints = @UniqueConstraint(name = "uk_users_email", columnNames = "email")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 30)
    private Role role;

    @Column(name = "email", nullable = false, length = 150)
    private String email;

    /** BCrypt hash of the password. Never store the raw password. */
    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(name = "phone", length = 20)
    private String phone;

    /** Plain grouping value as defined in the schema; no foreign key. Nullable. */
    @Column(name = "account_id")
    private Long accountId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private UserStatus status;
}
