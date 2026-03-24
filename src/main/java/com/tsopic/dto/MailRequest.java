package com.tsopic.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MailRequest(
        @NotBlank(message = "Email must not be blank")
        @Email(message = "Email must be a valid email address")
        String email,

        @NotBlank(message = "Username must not be blank")
        @Size(min = 2, max = 100, message = "Username must be between 2 and 100 characters")
        String username
) {}
