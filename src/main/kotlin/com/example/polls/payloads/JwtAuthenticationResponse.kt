package com.example.polls.payloads

data class JwtAuthenticationResponse(val accessToken: String) {
    val tokenType = "Bearer"
}