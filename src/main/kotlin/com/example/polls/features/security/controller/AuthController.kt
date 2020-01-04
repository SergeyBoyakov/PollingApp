package com.example.polls.features.security.controller

import com.example.polls.exception.AppException
import com.example.polls.features.security.JwtTokenProvider
import com.example.polls.features.user.model.RoleName
import com.example.polls.features.user.model.User
import com.example.polls.features.user.repository.RoleRepository
import com.example.polls.features.user.repository.UserRepository
import com.example.polls.features.security.payloads.ApiResponse
import com.example.polls.features.security.payloads.JwtAuthenticationResponse
import com.example.polls.features.security.payloads.LoginRequest
import com.example.polls.features.security.payloads.SignUpRequest
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import javax.validation.Valid

@RestController
@RequestMapping("/api/auth")
class AuthController(
    val authenticationManager: AuthenticationManager,
    // its just a tutorial -> do not call repo in controller, rewrite to services
    val userRepository: UserRepository,
    val roleRepository: RoleRepository,
    val passwordEncoder: PasswordEncoder,
    val tokenProvider: JwtTokenProvider
) {
    fun authenticateUser(@Valid @RequestBody loginRequest: LoginRequest): ResponseEntity<*> {
        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                loginRequest.usernameOrEmail,
                loginRequest.password
            )
        )

        SecurityContextHolder.getContext().authentication = authentication

        val generatedToken = tokenProvider.generateToken(authentication)

        return ResponseEntity.ok(JwtAuthenticationResponse(generatedToken))
    }

    // once again: this is just a tutorial from callicoder.com, do not try this at home
    @PostMapping("/signup")
    fun registerUser(@Valid @RequestBody req: SignUpRequest): ResponseEntity<*> {
        if (userRepository.existsByUsername(req.username)) {
            return ResponseEntity.badRequest().body(ApiResponse(success = false, message = "Username is already taken"))
        }

        if (userRepository.existsByEmail(req.email)) {
            return ResponseEntity.badRequest()
                .body(ApiResponse(success = false, message = "Emails address already in use"))
        }

        // Creating user account
        val user = User().apply {
            name = req.name
            username = req.username
            email = req.email
            password = req.password
        }

        user.password = passwordEncoder.encode(user.password)

        val userRole = roleRepository.findByName(RoleName.ROLE_USER.name) ?: throw AppException("User role is not set")

        user.roles = mutableSetOf(userRole)

        val savedUser = userRepository.save(user)

        val location = ServletUriComponentsBuilder
            .fromCurrentContextPath()
            .path("/api/users/{username}")
            .buildAndExpand(savedUser.username)
            .toUri()

        return ResponseEntity.created(location).body(ApiResponse(true, "User registered"))
    }
}