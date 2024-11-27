package com.project.bridgetalk.model.vo

import java.util.UUID


data class ChatItem(
    var roomId: UUID?,
    var lastMessage: String?,
    val user: User?,
    val school: Schools?,
    val created_at: String?
) {
    constructor(roomId: UUID) : this(
        roomId = roomId,
        lastMessage = "",
        user = null,
        school = null,
        created_at = ""
    )

    constructor() : this(  roomId = null,
        lastMessage = "",
        user = null,
        school = null,
        created_at = "")
}