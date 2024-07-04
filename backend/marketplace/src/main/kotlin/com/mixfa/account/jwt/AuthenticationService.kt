package com.mixfa.account.jwt

import com.mixfa.account.jwt.model.JwtAuthRequest
import com.mixfa.account.model.Account
import com.mixfa.account.service.AccountService
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Service


@Service
class AuthenticationService(
    private val accountService: AccountService,
    private val jwtService: JwtService,
    private val authenticationManager: AuthenticationManager
) {
    fun authenticate(request: JwtAuthRequest): String {
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                request.username,
                request.password
            )
        )

        return jwtService.generateToken(
            accountService.loadUserByUsername(request.username) as Account
        )
    }
}