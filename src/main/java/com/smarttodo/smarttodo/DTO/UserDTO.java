package com.smarttodo.smarttodo.DTO;

import java.time.LocalDate;

public record UserDTO(Long userId,
                      String email,
                      String firstName,
                      String lastName,
                      LocalDate lastDailyReset,
                      boolean isAdmin,
                      boolean isPremiumUser
                      ) {
}
