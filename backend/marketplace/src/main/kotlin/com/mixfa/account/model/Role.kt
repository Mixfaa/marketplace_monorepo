package com.mixfa.account.model

import org.springframework.security.core.authority.SimpleGrantedAuthority

enum class Role(
    permissions: Set<Permission>
) {
    ADMIN(
        setOf(
            Permission.FILES_EDIT,
            Permission.MARKETPLACE_EDIT,
            Permission.COMMENTS_EDIT,
            Permission.ORDER_EDIT,
            Permission.FAVLIST_EDIT,
        )
    ),
    CUSTOMER(
        setOf(
            Permission.FILES_EDIT,
            Permission.MARKETPLACE_EDIT,
            Permission.ORDER_EDIT,
            Permission.FAVLIST_EDIT,
            Permission.COMMENTS_EDIT
        )
    );

    val grantedAuthorities: MutableList<SimpleGrantedAuthority> = permissions
        .mapTo(mutableListOf()) { SimpleGrantedAuthority(it.normalName) }
            .also { it.add(SimpleGrantedAuthority("ROLE_${this.name}")) }
}