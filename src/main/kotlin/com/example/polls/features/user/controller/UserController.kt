package com.example.polls.features.user.controller

import com.example.polls.exception.ResourceNotFoundException
import com.example.polls.features.poll.payload.responsepayload.*
import com.example.polls.features.poll.repository.PollRepository
import com.example.polls.features.poll.repository.VoteRepository
import com.example.polls.features.poll.service.PollService
import com.example.polls.features.security.CurrentUser
import com.example.polls.features.security.UserPrincipal
import com.example.polls.features.user.repository.UserRepository
import com.example.polls.utils.AppConstants
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class UserController(
    val userRepository: UserRepository,
    val pollRepository: PollRepository,
    val voteRepository: VoteRepository,
    val pollService: PollService
) {
    val logger: Logger = LoggerFactory.getLogger(this.javaClass.name)

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    fun getCurrentUser(currentUser: @CurrentUser UserPrincipal): UserSummary =
        UserSummary(currentUser.id, currentUser.username, currentUser.name)

    @GetMapping("/user/checkUserNameAvailability")
    fun checkUsernameAvailability(@RequestParam(value = "username") username: String): UserIdentityAvailability {
        val isAvailable = !userRepository.existsByUsername(username)

        return UserIdentityAvailability(isAvailable)
    }

    @GetMapping("/user/checkEmailAvailability")
    fun checkEmailAvailability(email: String): UserIdentityAvailability {
        val isAvailable = !userRepository.existsByEmail(email)

        return UserIdentityAvailability(isAvailable)
    }

    @GetMapping("/user/{username}")
    fun getUserProfile(@PathVariable(value = "username") username: String): UserProfile {
        val user = userRepository.findByUsername(username)
            ?: throw ResourceNotFoundException("User", "username", username)

        val pollCount = pollRepository.countByCreatedBy(user.id!!)
        val voteCount = voteRepository.countByUserId(user.id!!)

        return UserProfile(user.id!!, user.username, user.name, user.createdAt, pollCount, voteCount)
    }

    @GetMapping("/user/{username}/polls")
    fun getPollsCreatedBy(
        @PathVariable(value = "username") username: String,
        currentUser: @CurrentUser UserPrincipal,
        @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) page: Int,
        @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) size: Int
    ): PagedResponse<PollResponse> {
        return pollService.getPollsCreatedBy(username, currentUser, page, size)
    }

    @GetMapping("/users/{username}/votes")
    fun getPollsVoteBy(
        @PathVariable(value = "username") username: String,
        currentUser: @CurrentUser UserPrincipal,
        @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) page: Int,
        @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) size: Int
    ): PagedResponse<PollResponse> {
        return pollService.getPollsVotedBy(username, currentUser, page, size)
    }
}