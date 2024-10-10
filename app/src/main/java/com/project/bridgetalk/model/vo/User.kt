package com.project.bridgetalk.model.vo

data class User(
    val userId: String,
    val username: String,
    val email: String,
    val schools: Schools,
    val role: String,
    val createdAt: String,
    val updatedAt: String
)