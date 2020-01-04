package com.example.polls.features.poll.payload.responsepayload

class PagedResponse<T>() {
    constructor(
        content: List<T>,
        page: Int,
        size: Int,
        totalElements: Long,
        totalPages: Long,
        last: Boolean
    ) : this()
}

