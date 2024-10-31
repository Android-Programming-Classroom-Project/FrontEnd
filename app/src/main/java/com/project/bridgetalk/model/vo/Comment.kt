package com.project.bridgetalk.model.vo

import java.time.LocalDateTime
import java.util.UUID

data class Comment(var commentId: UUID, var post: Post, var user: User, var content:String, var updatedAt:LocalDateTime, var createdAt:LocalDateTime)
