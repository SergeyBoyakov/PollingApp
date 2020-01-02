package com.example.polls.payloads

import javax.validation.constraints.NotBlank

class LoginRequest {
    @NotBlank
    lateinit var usernameOrEmail: String

    @NotBlank
    lateinit var password: String
}