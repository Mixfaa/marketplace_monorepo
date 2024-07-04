package com.mixfa.account.jwt

import com.mixfa.account.jwt.model.JwtAuthenticatedPrincipal
import com.mixfa.account.model.Role
import com.mixfa.account.service.AccountService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.boot.web.servlet.filter.ApplicationContextHeaderFilter.HEADER_NAME
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.Calendar


@Component
class JwtAuthFilter(
    private val jwtService: JwtService,
    private val accountService: AccountService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader(HEADER_NAME)
        if (authHeader.isNullOrEmpty() || authHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response)
            return
        }

        val jwt = authHeader.substring(BEARER_PREFIX.length)
        val claims = jwtService.extractAllClaims(jwt)
        val username = claims["username"] as String
        val email = claims["email"] as String
        val role = Role.valueOf(claims["role"] as String)
        val expiration = claims.expiration

        if (username.isNotBlank() && SecurityContextHolder.getContext().authentication == null) {
            if (!expiration.before(Calendar.getInstance().time) && accountService.existsByUsername(username)) {
                val context = SecurityContextHolder.createEmptyContext()

                val authToken = UsernamePasswordAuthenticationToken(
                    JwtAuthenticatedPrincipal(username, email, role),
                    null,
                    role.grantedAuthorities
                )

                authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                context.authentication = authToken
                SecurityContextHolder.setContext(context)
            }
        }
        filterChain.doFilter(request, response)
    }

    companion object {
        const val BEARER_PREFIX = "Bearer"
    }
}