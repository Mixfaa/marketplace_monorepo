package com.mixfa.account.jwt.model

import com.mixfa.account.model.Role

data class JwtAuthenticatedPrincipal(
    val username: String,
    val email: String,
    val role: Role
)