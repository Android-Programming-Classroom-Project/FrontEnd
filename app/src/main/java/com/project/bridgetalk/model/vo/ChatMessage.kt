package com.project.bridgetalk.model.vo

data class ChatMessage(
    val message: String,
    val isSent: Boolean // true: 보낸 메시지, false: 받은 메시지
)
