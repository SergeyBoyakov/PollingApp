package com.example.polls.features.poll.payload.responsepayload

import com.fasterxml.jackson.annotation.JsonInclude
import java.time.Instant

class PollResponse {
    var id: Long? = null
    var question: String? = null
    var choices: List<ChoiceResponse>? = null
    var createdBy: UserSummary? = null
    var creationDateTime: Instant? = null
    var expirationDateTime: Instant? = null
    var isExpired: Boolean? = null

    @JsonInclude(JsonInclude.Include.NON_NULL)
    var selectedChoice : Long ? = null
    var totalVotes: Long ? = null
}