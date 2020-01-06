package com.example.polls.features.poll.service

import com.example.polls.exception.BadRequestException
import com.example.polls.exception.ResourceNotFoundException
import com.example.polls.features.poll.model.Choice
import com.example.polls.features.poll.model.Poll
import com.example.polls.features.poll.model.Vote
import com.example.polls.features.poll.payload.requestpayload.PollRequest
import com.example.polls.features.poll.payload.requestpayload.VoteRequest
import com.example.polls.features.poll.payload.responsepayload.PagedResponse
import com.example.polls.features.poll.payload.responsepayload.PollResponse
import com.example.polls.features.poll.repository.PollRepository
import com.example.polls.features.poll.repository.VoteRepository
import com.example.polls.features.security.UserPrincipal
import com.example.polls.features.user.model.User
import com.example.polls.features.user.repository.UserRepository
import com.example.polls.utils.AppConstants
import com.example.polls.utils.ModelMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.Instant
import java.util.*


// TODO refactor this class

@Suppress("UNREACHABLE_CODE")
@Service
@Transactional
class PollService(
    val voteRepository: VoteRepository,
    val pollRepository: PollRepository,
    val userRepository: UserRepository
) {
    val logger: Logger = LoggerFactory.getLogger(this.javaClass.name)

    fun getAllPolls(currentUser: UserPrincipal, page: Int, size: Int): PagedResponse<PollResponse> {
        validatePageNumberAndSize(page, size)

        val pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt")
        val polls = pollRepository.findAll(pageable)

        if (polls.numberOfElements == 0) {
            return PagedResponse<PollResponse>(
                Collections.emptyList(),
                polls.number,
                polls.size,
                polls.totalElements,
                polls.totalPages.toLong(),
                polls.isLast
            )
        }

        val pollIds = polls.map { it.id }.filterNotNull()
        val choiceIdToVoteCount = getChoiceVoteCountMap(pollIds)
        val pollToUserVote = getPollUserVoteMap(currentUser, pollIds)
        val pollIdToCreator = getPollCreatorMap(polls.content)

        val pollResponses = polls.map {
            ModelMapper().mapPollToPollResponse(
                it,
                choiceIdToVoteCount,
                // todo remove !!!!!!!!!!!!!!!!
                pollIdToCreator[it.createdBy]!!,
                pollToUserVote.getOrDefault(it.id, null)!!
            )
        }.content

        return PagedResponse(
            pollResponses,
            polls.number,
            polls.size,
            polls.totalElements,
            polls.totalPages.toLong(),
            polls.isLast
        )
    }

    fun getPollsCreatedBy(
        username: String,
        currentUser: UserPrincipal,
        page: Int,
        size: Int
    ): PagedResponse<PollResponse> {
        validatePageNumberAndSize(page, size)

        val user =
            userRepository.findByUsername(username) ?: throw ResourceNotFoundException("User", "username", username)

        val pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt")

        val polls = pollRepository.findByCreatedBy(user.id!!, pageable)
        if (polls.numberOfElements == 0) {
            return PagedResponse<PollResponse>(
                listOf(),
                polls.number,
                polls.size,
                polls.totalElements,
                polls.totalPages.toLong(),
                polls.isLast
            )
        }

        val pollIds = polls.map { it.id }.content.filterNotNull()
        val choiceToVoteCount = getChoiceVoteCountMap(pollIds)
        val pollIdToUserVote = getPollUserVoteMap(currentUser, pollIds)
        val pollResponses = polls.map {
            ModelMapper().mapPollToPollResponse(
                it,
                choiceToVoteCount,
                user,
                pollIdToUserVote.getOrDefault(it.id, null)!! // todo remove !!
            )
        }.content

        return PagedResponse<PollResponse>(
            pollResponses,
            polls.number,
            polls.size,
            polls.totalElements,
            polls.totalPages.toLong(),
            polls.isLast
        )
    }

    fun getPollsVotedBy(
        username: String,
        currentUser: UserPrincipal,
        page: Int,
        size: Int
    ): PagedResponse<PollResponse> {
        validatePageNumberAndSize(page, size)

        val votedUser =
            userRepository.findByUsername(username) ?: throw ResourceNotFoundException("User", "username", username)

        val pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt")
        val userVotedPollIds = voteRepository.findVotedPollIdsByUserId(votedUser.id!!, pageable)

        if (userVotedPollIds.numberOfElements == 0) {
            return PagedResponse<PollResponse>(
                listOf(),
                userVotedPollIds.number,
                userVotedPollIds.size,
                userVotedPollIds.totalElements,
                userVotedPollIds.totalPages.toLong(),
                userVotedPollIds.isLast
            )
        }

        val pollIds = userVotedPollIds.content

        val sort = Sort.by(Sort.Direction.DESC, "createdAt")
        val polls = pollRepository.findByIdIn(pollIds, sort)

        val choiceVoteCountMap = getChoiceVoteCountMap(pollIds)
        val pollUserVoteMap = getPollUserVoteMap(currentUser, pollIds)
        val pollCreatorMap = getPollCreatorMap(polls)

        val pollResponses = polls.map {
            ModelMapper().mapPollToPollResponse(
                it,
                choiceVoteCountMap,
                pollCreatorMap[it.createdBy],
                pollUserVoteMap.getOrDefault(it.id, null)
            )
        }

        return PagedResponse(
            pollResponses,
            userVotedPollIds.number,
            userVotedPollIds.size,
            userVotedPollIds.totalElements,
            userVotedPollIds.totalPages.toLong(),
            userVotedPollIds.isLast
        )
    }

    fun createPoll(pollRequest: PollRequest): Poll {
        val poll = Poll().apply {
            question = pollRequest.question
        }

        pollRequest.choices.forEach { poll.addChoice(Choice(it.text)) }

        val expirationDateTime = Instant.now().plus(Duration.ofDays(pollRequest.pollLength.days.toLong()))
            .plus(Duration.ofHours(pollRequest.pollLength.hours.toLong()))

        poll.expirationDateTime = expirationDateTime

        return pollRepository.save(poll)
    }

    fun getPollById(pollId: Long, currentUser: UserPrincipal): PollResponse {
        val poll = pollRepository.findById(pollId).orElse(throw ResourceNotFoundException("Poll", "id", pollId))

        val votes = voteRepository.countByPollIdGroupByChoiceId(pollId)

        val choceVoteMap = votes.map { it.choiceId to it.voteCount }.toMap()

        val creator = userRepository.findById(poll.createdBy!!)
            .orElseThrow { ResourceNotFoundException("User", "id", poll.createdBy!!) }

        val userVote = voteRepository.findByUserIdAndPollId(currentUser.id!!, pollId)

        return ModelMapper().mapPollToPollResponse(poll, choceVoteMap, creator, userVote.choice.id)
    }

    fun castVoteAndGetUpdatedPoll(pollId: Long, voteRequest: VoteRequest, currentUser: UserPrincipal): PollResponse {
        val poll =
            pollRepository.findById(pollId).orElseThrow { ResourceNotFoundException("Poll", "Id", pollId) }

        if (poll.expirationDateTime.isBefore(Instant.now())) {
            throw  BadRequestException("This Poll has already expired")
        }

        val user = userRepository.getOne(currentUser.id!!)

        val selectedChoice = poll.choices.first { it.id == voteRequest.choiceId }

        var vote = Vote().apply {
            this.poll = poll
            this.user = user
            this.choice = selectedChoice
        }

        try {
            vote = voteRepository.save(vote)
        } catch (ex: DataIntegrityViolationException) {
            logger.info("User ${currentUser.id} has already voted in Poll $pollId")
            throw BadRequestException("You have already cast your vote in this poll")
        }

        val votes = voteRepository.countByPollIdGroupByChoiceId(pollId)

        val choiceVotesMap = votes.map { it.choiceId to it.voteCount }.toMap()

        val creator = userRepository.findById(poll.createdBy!!)
            .orElseThrow { ResourceNotFoundException("User", "id", poll.createdBy!!) }

        return ModelMapper().mapPollToPollResponse(poll, choiceVotesMap, creator, vote.choice.id)
    }

    fun getPollCreatorMap(polls: List<Poll>): Map<Long, User> {
        val creatorIds = polls.mapNotNull { it.createdBy }.distinct()
        val creators = userRepository.findByIdIn(creatorIds)

        return creators.filter { it.id != null }.map { it.id!! to it }.toMap()
    }

    private fun getPollUserVoteMap(currentUser: UserPrincipal, pollIds: List<Long>): Map<Long, Long> {
        val userVotes = voteRepository.findByUSerIdAndPollIdIn(currentUser.id!!, pollIds)

        return userVotes
            .filter { it.poll.id != null }
            .filter { it.choice.id != null }
            .map { it.poll.id!! to it.choice.id!! }
            .toMap()
    }

    private fun getChoiceVoteCountMap(pollIds: List<Long>): Map<Long, Long> {
        val votes = voteRepository.countByPollIdInGroupByChoiceId(pollIds)

        return votes.map { it.choiceId to it.voteCount }.toMap()
    }

    private fun validatePageNumberAndSize(page: Int, size: Int) {
        if (page < 0) {
            throw BadRequestException("Page number cannot be less than zero")
        }
        if (size > AppConstants.MAX_PAGE_SIZE) {
            throw BadRequestException("Page size must not be greater than ${AppConstants.MAX_PAGE_SIZE}")
        }
    }
}