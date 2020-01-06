package com.example.polls.features.poll.repository

import com.example.polls.features.poll.model.ChoiceVoteCount
import com.example.polls.features.poll.model.Vote
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface VoteRepository : JpaRepository<Vote, Long> {
    @Query("SELECT NEW com.example.polls.features.poll.model.ChoiceVoteCount(v.choice.id, count(v.id)) FROM Vote v WHERE v.poll.id in :pollIds GROUP BY v.choice.id")
    fun countByPollIdInGroupByChoiceId(@Param(value = "pollIds") pollIds: List<Long>): List<ChoiceVoteCount>

    @Query("SELECT NEW com.example.polls.features.poll.model.ChoiceVoteCount(v.choice.id, count(v.id)) FROM Vote v WHERE v.poll.id = :pollId GROUP BY v.choice.id")
    fun countByPollIdGroupByChoiceId(@Param(value = "pollId") pollId: Long): List<ChoiceVoteCount>

    @Query("SELECT v FROM Vote v WHERE v.user.id=:userId and v.poll.id in :pollIds")
    fun findByUSerIdAndPollIdIn(@Param("userId") userId: Long, @Param("pollIds") pollIds: List<Long>): List<Vote>

    @Query("SELECT v FROM Vote v WHERE v.user.id = :userId and v.poll.id = :pollId")
    fun findByUserIdAndPollId(@Param("userId") userId: Long, @Param("pollId") pollId: Long): Vote

    @Query("SELECT COUNT(v.id) from Vote v WHERE v.user.id = :userId")
    fun countByUserId(@Param("userId") userId: Long): Long

    @Query("SELECT v.poll.id FROM Vote v WHERE v.user.id = :userId")
    fun findVotedPollIdsByUserId(@Param("userId") userId: Long, pageable: Pageable): Page<Long>
}