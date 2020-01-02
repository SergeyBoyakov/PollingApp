package com.example.polls.features.security

import com.example.polls.features.user.model.User
import com.example.polls.features.user.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CustomUserDetailsService(val userRepository: UserRepository) : UserDetailsService {

    @Transactional
    override fun loadUserByUsername(usernameOrEmail: String): UserDetails {
        // let people login with either username or email
        val user: User = (userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
            ?: throw UsernameNotFoundException("User not found with username or email: $usernameOrEmail "))

        return UserPrincipal(user)
    }

    // This method is used by JWTAuthenticationFilter
    fun loadUserById(id: Long): UserDetails {
        val user: User = userRepository.findById(id)
            .orElseThrow { UsernameNotFoundException("User not found with id: $id") }

        return UserPrincipal(user)
    }
}