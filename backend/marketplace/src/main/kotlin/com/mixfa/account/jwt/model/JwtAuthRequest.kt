package com.mixfa.account.jwt.model

data class JwtAuthRequest(
    val username: String,
    val password: String
)