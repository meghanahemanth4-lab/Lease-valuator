package com.spinteg.lv.entity;

import com.spinteg.lv.entity.enums.Role;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "users", schema = "contract")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Password (no validation here – validation handled in DTO)
    @Column(nullable = false, length = 255)
    private String password;

    @Transient
    private String confirmPassword;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Transient
    public String getFullName() {
        return (firstName + " " + lastName).trim();
    }

    @Column(
            name = "email",
            unique = true,
            nullable = false,
            columnDefinition = "VARCHAR(100) COLLATE SQL_Latin1_General_CP1_CI_AS"
    )
    private String email;

    @Column(name = "company_name", nullable = false, length = 100)
    private String companyName;

    // User roles
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", schema = "contract",
            joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role", columnDefinition = "user_role")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private Set<Role> roles = new HashSet<>();

    // ===== UserDetails Overrides =====

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getUsername() {
        return email; // IMPORTANT
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
