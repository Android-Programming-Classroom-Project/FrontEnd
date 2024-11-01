package com.project.bridgetalk.model.vo

data class Comment(
    val userName: String,
    val date: String,
    val content: String,
    val likeCount: Int,
    val replies: List<Reply>
)