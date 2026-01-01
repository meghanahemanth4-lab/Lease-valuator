package com.spinteg.lv.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.Set;

@Data
public class SignupRequest {

    @NotBlank(message = "Password is required")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]{8,}$",
            message = "Password must contain 1 uppercase, 1 lowercase, 1 digit, 1 special character (@$!%*?&#), and be at least 8 characters long"
    )
    private String password;

    @Transient
    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;

    @JsonIgnore
    @AssertTrue(message = "Passwords do not match")
    public boolean isPasswordMatching() {
        return password != null && password.equals(confirmPassword);
    }

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50)
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Please enter a valid email address")
    @Size(max = 100)
    private String email;

    @NotBlank(message = "Company name is required")
    @Size(max = 100)
    private String companyName;

    private Set<String> roles;
}
