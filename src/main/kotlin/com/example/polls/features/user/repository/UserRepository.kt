package com.example.polls.features.user.repository

import com.example.polls.features.user.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun findByEmail(email: String): User?
    fun findByUsernameOrEmail(username: String, email: String): User?
    fun findByIdIn(userIds: List<Long>): List<User>
    fun findByUsername(username: String): User?
    fun existsByUsername(username: String): Boolean
    fun existsByEmail(email: String): Boolean
}