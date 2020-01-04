package com.example.polls.utils

import com.example.polls.features.poll.model.Poll
import com.example.polls.features.poll.payload.responsepayload.ChoiceResponse
import com.example.polls.features.poll.payload.responsepayload.PollResponse
import com.example.polls.features.poll.payload.responsepayload.UserSummary
import com.example.polls.features.user.model.User
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class ModelMapper {

    fun mapPollToPollResponse(poll: Poll, choiceVotes: Map<Long, Long>, creator: User, userVote: Long): PollResponse {
        return PollResponse().apply {
            id = poll.id
            question = poll.question
            creationDateTime = poll.createdAt
            expirationDateTime = poll.expirationDateTime
            isExpired = poll.expirationDateTime.isBefore(Instant.now())
            choices = getChoicesFrom(poll, choiceVotes)
            createdBy = UserSummary(creator.id, creator.username, creator.name)
            selectedChoice = userVote
            totalVotes = getTotalVotesFrom(choices!!)
        }
    }

    private fun getTotalVotesFrom(choices: List<ChoiceResponse>): Long {
        return choices.mapNotNull { it.voteCount }.sum()
    }

    private fun getChoicesFrom(poll: Poll, choiceVotes: Map<Long, Long>): List<ChoiceResponse> {
        return poll.choices
            .map {
                ChoiceResponse().apply {
                    id = it.id
                    text = it.text
                    voteCount = choiceVotes.getOrDefault(it.id, 0)
                }
            }
    }
}