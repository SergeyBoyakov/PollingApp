package com.example.polls.features.security.payloads

import javax.validation.constraints.NotBlank

class LoginRequest {
    @NotBlank
    lateinit var usernameOrEmail: String

    @NotBlank
    lateinit var password: String
}