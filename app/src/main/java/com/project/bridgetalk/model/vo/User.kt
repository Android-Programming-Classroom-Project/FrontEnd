package com.project.bridgetalk.model.vo

data class User(
    val userId: String,
    var username: String,
    var email: String,
    var password: String?,
    val schools: Schools,
    var role: String,
    var createdAt: String,
    var updatedAt: String
)