package com.project.bridgetalk.model.vo.dto.request

import com.project.bridgetalk.model.vo.Post
import com.project.bridgetalk.model.vo.User

data class CommentRequest(
    val post: Post,
    val user: User,
    val content: String
)