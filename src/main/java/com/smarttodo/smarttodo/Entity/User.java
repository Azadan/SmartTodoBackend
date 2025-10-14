package com.smarttodo.smarttodo.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long userId;

    // Nedan kommer att behövas sen för korrekt stripe impl
    //private String stripeId;

    @NotBlank(message = "Email is required")
    @Column(unique = true, nullable = false)
    @Email(
            regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}",
            flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "Please enter a correct email adress"
    )
    private String email;

    @NotBlank(message = "Password is required")
    @Column(nullable = false)
    @Size(min = 8, max = 18, message = "Password must be between 8 and 18 characters")
    private String password;

    @NotBlank(message = "First name is required")
    @Column(nullable = false)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Column(nullable = false)
    private String lastName;

    // Admin ska kunna logga in och ha en chatt med en användare som kundtjänst
    @Column(nullable = false)
    private String isAdmin;

    // Premium user för att kunna använda obegränsat mängd tasks
    @Column(nullable = false)
    private String isPremiumUser;

    // Sparar datum för senaste dagliga reset av tasks
    @Column(nullable = true)
    private LocalDate lastDailyReset;
}
