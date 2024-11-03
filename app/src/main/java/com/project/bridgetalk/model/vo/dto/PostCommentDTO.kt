package com.project.bridgetalk.model.vo.dto

import com.project.bridgetalk.model.vo.Comment
import com.project.bridgetalk.model.vo.Post

data class PostCommentDTO (val post: Post, val commentList: MutableList<Comment>)
