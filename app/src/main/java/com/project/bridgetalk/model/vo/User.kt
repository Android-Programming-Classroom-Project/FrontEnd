package com.project.bridgetalk.model.vo

import java.time.LocalDateTime

data class User(
    val userId: String,
    var username: String,
    var email: String,
    var password: String,
    val schools: Schools,
    var role: String,
    var createdAt: LocalDateTime,
    var updatedAt: LocalDateTime
)