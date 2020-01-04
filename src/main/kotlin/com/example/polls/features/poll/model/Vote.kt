package com.example.polls.features.poll.model

import com.example.polls.features.user.model.User
import com.example.polls.features.user.model.audit.DateAudit
import javax.persistence.*

@Entity
@Table(name = "votes", uniqueConstraints = [UniqueConstraint(columnNames = ["poll_id", "user_id"])])
class Vote : DateAudit() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "poll_id", nullable = false)
    lateinit var poll: Poll

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "choice_id", nullable = false)
    lateinit var choice: Choice

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    lateinit var user: User
}