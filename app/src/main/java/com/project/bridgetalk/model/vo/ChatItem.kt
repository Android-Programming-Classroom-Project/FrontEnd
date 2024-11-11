package com.project.bridgetalk.model.vo

import java.util.UUID


data class ChatItem(
    val roomId: UUID,
    val user: User,
    val school: Schools,
    val created_at: String
)