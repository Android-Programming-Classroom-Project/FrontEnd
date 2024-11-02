package com.project.bridgetalk.model.vo

import java.util.UUID

data class Comment(var commentId: UUID, var post: Post, var user: User, var content:String, var updatedAt:String, var createdAt:String)
