package com.example.polls.features.poll.payload.requestpayload

import com.example.polls.features.poll.payload.requestpayload.ChoiceRequest
import com.example.polls.features.poll.payload.requestpayload.PollLength
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

class PollRequest {
    @NotBlank
    @Size(max = 140)
    lateinit var question: String

    @NotNull
    @Size(min =2 , max = 6)
    @Valid
    lateinit var choices: List<ChoiceRequest>

    @NotNull
    @Valid
    lateinit var pollLength: PollLength
}