package com.project.bridgetalk.model.vo

import java.util.UUID

data class Comment(var commentId: UUID, var post: Post, var user: User, var content:String, var updatedAt:String, var createdAt:String)

// 박진수의 코멘트
//data class Comment(
//    val userName: String,
//    val date: String,
//    val content: String,
//    val likeCount: Int,
//    val replies: List<Reply>
//)