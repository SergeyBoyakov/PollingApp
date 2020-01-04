package com.example.polls.features.security.payloads

data class JwtAuthenticationResponse(val accessToken: String) {
    val tokenType = "Bearer"
}