package com.project.bridgetalk.model.vo.dto

import com.project.bridgetalk.model.vo.User
import java.util.UUID

data class ChatRoom(
    val roomId: UUID,
    var user: User,
    var user1: User,
    var createdAt: String?,
    var updatedAt: String?
)