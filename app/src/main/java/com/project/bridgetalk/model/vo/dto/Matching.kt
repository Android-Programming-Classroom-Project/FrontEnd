package com.project.bridgetalk.model.vo.dto

data class Matching(
    val type: String?,
    val userId: String,
    val users: HashSet<String>?,
    val chatRoom: String?
)
