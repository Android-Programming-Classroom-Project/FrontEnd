package com.project.bridgetalk.model.vo

data class Post(
    val postId: String,
    val user: User?,
    val schools: Schools?,
    val title: String,
    val content: String,
    val like_count: Int,
    val createdAt: String,
    val updatedAt: String
)
