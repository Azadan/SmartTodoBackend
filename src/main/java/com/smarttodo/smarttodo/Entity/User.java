package com.smarttodo.smarttodo.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

    private String email;

    private String password;

    private String firstName;

    private String lastName;


    // Admin ska kunna logga in och ha en chatt med en användare som kundtjänst
    private String isAdmin;

    // Premium user för att kunna använda obegränsat mängd tasks
    private String isPremiumUser;

    // Sparar datum för senaste dagliga reset av tasks
    private LocalDate lastDailyReset;
}
