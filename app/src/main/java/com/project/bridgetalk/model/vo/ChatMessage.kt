package com.project.bridgetalk.model.vo

import java.util.UUID

data class ChatMessage(
    val messageId: UUID? = null,
    val content: String,
    val createdAt: String? = null,
    val isSent: Boolean// true: 보낸 메시지, false: 받은 메시지
) {
    constructor(content: String, isSent: Boolean) : this(
        messageId = null,
        content = content,
        createdAt = null,
        isSent = isSent
    )

    constructor(content: String) : this(
        messageId = null,
        content = content,
        createdAt = null,
        isSent = true
    )
}
