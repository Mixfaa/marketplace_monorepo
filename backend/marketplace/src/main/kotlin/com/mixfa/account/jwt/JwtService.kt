package com.mixfa.account.jwt

import com.mixfa.account.model.Account
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.util.*
import kotlin.math.exp


@Component
class JwtService(
    @Value("\${jwt-signing-key}") private val jwtSigningKey: String
) {
    private val signingKey = Decoders.BASE64.decode(jwtSigningKey).let(Keys::hmacShaKeyFor)
    private val jwtParser = Jwts.parser().verifyWith(signingKey).build()

    fun generateToken(account: Account): String = generateToken(buildMap {
        this["username"] = account.username
        this["email"] = account.email
        this["role"] = account.email
    }, account)

    private fun generateToken(extraClaims: Map<String, Any?>, userDetails: UserDetails): String {
        return Jwts.builder().claims(extraClaims).subject(userDetails.username)
            .issuedAt(Date(System.currentTimeMillis()))
            .expiration(Date(System.currentTimeMillis() + 100000 * 60 * 24))
            .signWith(signingKey).compact()
    }

    fun extractAllClaims(token: String): Claims {
        return jwtParser.parseSignedClaims(token).payload
    }
}