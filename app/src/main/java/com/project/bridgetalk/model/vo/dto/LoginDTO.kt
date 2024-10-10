package com.project.bridgetalk.model.vo.dto

import com.project.bridgetalk.model.vo.Post
import com.project.bridgetalk.model.vo.User

data class LoginDTO(
    val user: User,
    val post: List<Post>
)