package com.project.bridgetalk.model.vo.dto.request

import com.project.bridgetalk.model.vo.ChatItem
import com.project.bridgetalk.model.vo.User

data class UserChatroomRequest(val user: User?, val chatRoom: ChatItem? )