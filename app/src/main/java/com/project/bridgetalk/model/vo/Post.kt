package com.project.bridgetalk.model.vo

import java.util.UUID

data class Post(
    var postId: UUID,
    var user: User?,
    var schools: Schools?,
    var title: String,
    var content: String,
    var like_count: Int,
    var type: String?,
    var createdAt: String?,
    var updatedAt: String?,
)
