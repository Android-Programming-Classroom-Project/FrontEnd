package com.project.bridgetalk.model.vo

import java.util.UUID

data class ChatMessage(
    val messageId: UUID? = null,
    var chatRoom: ChatItem?,
    val user: User?,
    var content: String,
    var isSent: Boolean,// true: 보낸 메시지, false: 받은 메시지
    val createdAt: String? = null
) {
    constructor(user: User,content: String, isSent: Boolean) : this(
        messageId = null,
        content = content,
        chatRoom = null,
        user = user,
        createdAt = null,
        isSent = isSent
    )

    constructor(content: String) : this(
        messageId = null,
        content = content,
        chatRoom = null,
        user = null,
        createdAt = null,
        isSent = true
    )
}
