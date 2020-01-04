package com.example.polls.features.poll.payload.requestpayload

import javax.validation.constraints.NotNull

class VoteRequest {
    @NotNull
    var choiceId: Long? = null
}