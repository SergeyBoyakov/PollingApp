package com.example.polls.features.poll.payload.responsepayload

import java.time.Instant

class UserProfile(
    var id: Long,
    var username: String,
    var name: String,
    var joinedAt: Instant,
    var pollCount: Long,
    var voteCount: Long
)



