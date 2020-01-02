package com.example.polls.features.security

import com.example.polls.features.user.model.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class UserPrincipal : UserDetails {

    var id: Long?
    private var password: String?
    var name: String?
    private var username: String?
    var email: String?
    private var authorities: MutableCollection<out GrantedAuthority>

    constructor(
        id: Long?,
        password: String?,
        name: String?,
        username: String?,
        email: String?,
        authorities: MutableCollection<out GrantedAuthority>
    ) {
        this.id = id
        this.password = password
        this.name = name
        this.username = username
        this.email = email
        this.authorities = authorities
    }

    constructor(user: User) : this(
        user.id,
        user.name,
        user.username,
        user.email,
        user.password,
        user.roles.map { role -> SimpleGrantedAuthority(role.name?.name) }.toMutableList()
    )

    override fun isEnabled() = true
    override fun getUsername() = this.username
    override fun getPassword(): String? = this.password
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> = this.authorities
    override fun isCredentialsNonExpired() = true
    override fun isAccountNonExpired() = true
    override fun isAccountNonLocked() = true

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserPrincipal

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }


}