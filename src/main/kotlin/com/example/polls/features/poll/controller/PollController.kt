package com.example.polls.features.poll.controller

import com.example.polls.features.poll.model.Poll
import com.example.polls.features.poll.payload.requestpayload.PollRequest
import com.example.polls.features.poll.payload.requestpayload.VoteRequest
import com.example.polls.features.poll.payload.responsepayload.PollResponse
import com.example.polls.features.poll.repository.PollRepository
import com.example.polls.features.poll.repository.VoteRepository
import com.example.polls.features.poll.service.PollService
import com.example.polls.features.security.CurrentUser
import com.example.polls.features.security.UserPrincipal
import com.example.polls.features.security.payloads.ApiResponse
import com.example.polls.features.user.repository.UserRepository
import com.example.polls.utils.AppConstants
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import javax.validation.Valid

@RestController
@RequestMapping("/api/polls")
class PollController(
    val pollRepository: PollRepository,
    val voteRepository: VoteRepository,
    val userRepository: UserRepository,
    val pollService: PollService
) {
    val logger: Logger = LoggerFactory.getLogger(this.javaClass.name)

    @GetMapping
    fun getPolls(
        currentUser: @CurrentUser UserPrincipal,
        @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) page: Int,
        @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) size: Int
    ) = pollService.getAllPolls(currentUser, page, size)

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    fun createPoll(@Valid @RequestBody pollRequest: PollRequest): ResponseEntity<*> {
        val poll: Poll = pollService.createPoll(pollRequest)

        val location = ServletUriComponentsBuilder
            .fromCurrentRequest().path("/{pollId}")
            .buildAndExpand(poll.id)
            .toUri()

        return ResponseEntity.created(location).body(ApiResponse(true, "Poll created successfully"))
    }

    @GetMapping("/{pollId}")
    fun getPollById(currentUser: @CurrentUser UserPrincipal, @PathVariable(value = "pollId") pollId: Long) =
        pollService.getPollById(pollId, currentUser)

    @PostMapping("/{pollId}/votes")
    @PreAuthorize("hasRole('USEDR')")
    fun castVote(
        currentUser: @CurrentUser UserPrincipal,
        @PathVariable(value = "pollId") pollId: Long,
        @Valid @RequestBody voteRequest: VoteRequest
    ): PollResponse {
        return pollService.castVoteAndGetUpdatedPoll(pollId, voteRequest, currentUser)
    }
}