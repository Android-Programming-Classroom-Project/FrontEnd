package com.project.bridgetalk.model.vo

data class LikeRequest( // 좋아요 및 게시물작성에 이용
    val post: Post, // Post 객체
    val user: User  // User 객체
)

