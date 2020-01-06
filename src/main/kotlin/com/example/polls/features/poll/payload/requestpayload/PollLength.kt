package com.example.polls.features.poll.payload.requestpayload

import javax.validation.constraints.Max
import javax.validation.constraints.NotNull

class PollLength {
    @NotNull
    @Max(7)
    var days: Int = 0

    @NotNull
    @Max(23)
    var hours: Int = 0
}