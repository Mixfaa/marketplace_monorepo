package com.mixfa.account.controller

import com.mixfa.account.jwt.AuthenticationService
import com.mixfa.account.jwt.model.JwtAuthRequest
import com.mixfa.account.model.Account
import com.mixfa.account.service.AccountService
import com.mixfa.shared.model.CheckedPageable
import com.mixfa.shared.orThrow
import jakarta.validation.constraints.Email
import org.springframework.web.bind.annotation.*
import java.security.Principal


@RestController
@RequestMapping("/account")
class AccountControllerV2(
    private val accountService: AccountService,
    private val authenticationService: AuthenticationService
) {
    @PostMapping("/register")
    fun register(@RequestBody request: Account.RegisterRequest) = accountService.register(request)

    @PostMapping("/auth")
    fun authenticate(@RequestBody request: JwtAuthRequest): String = authenticationService.authenticate(request)

    @PostMapping("/shipping_address")
    fun addShippingAddress(shippingAddress: String) =
        accountService.addShippingAddress(shippingAddress)

    @DeleteMapping("/shipping_address")
    fun removeShippingAddress(shippingAddress: String) =
        accountService.removeShippingAddress(shippingAddress)

    @GetMapping("/me")
    fun getMe(principal: Principal) =
        accountService.accountByPrincipal(principal).orThrow().toPrivateDetails()

    @GetMapping("/users")
    fun findUsers(query: String, page: Int, pageSize: Int) =
        accountService.findUsers(query, CheckedPageable(page, pageSize))

    @PostMapping("/send_email_code")
    fun sendEmailVerifyCode(@Email email: String) =
        accountService.sendEmailTo(email)
}
