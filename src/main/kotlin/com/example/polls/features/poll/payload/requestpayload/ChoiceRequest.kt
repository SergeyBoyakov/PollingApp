package com.example.polls.features.poll.payload.requestpayload

import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

class ChoiceRequest {
    @NotBlank
    @Size(max = 40)
    lateinit var text: String
}