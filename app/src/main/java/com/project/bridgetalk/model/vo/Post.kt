package com.project.bridgetalk.model.vo

import java.time.LocalDateTime
import java.util.UUID

data class Post(
    val postId: UUID,
    var user: User?,
    var schools: Schools?,
    var title: String,
    var content: String,
    var like_count: Int,
    var type: String,
    var createdAt: LocalDateTime,
    var updatedAt: LocalDateTime
)
