package com.project.bridgetalk.model.vo

import java.util.UUID


data class ChatItem(
    var roomId: UUID?,
    var lastMessage: String?,
    val user: User?,
    val school: Schools?,
    val createdAt: String?
) {
    constructor(roomId: UUID) : this(
        roomId = roomId,
        lastMessage = "",
        user = null,
        school = null,
        createdAt = ""
    )

    constructor() : this(  roomId = null,
        lastMessage = "",
        user = null,
        school = null,
        createdAt = "")
}