package com.example.polls.features.user.model

import com.example.polls.features.user.model.audit.DateAudit
import org.hibernate.annotations.NaturalId
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Entity
@Table(
    name = "users", uniqueConstraints = [
        UniqueConstraint(columnNames = ["username"]),
        UniqueConstraint(columnNames = ["email"])]
)
class User() : DateAudit() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @NotBlank
    @Size(max = 40)
    lateinit var name: String

    @NotBlank
    @Size(max = 15)
    lateinit var username: String

    @NaturalId
    @NotBlank
    @Size(max = 40)
    lateinit var email: String

    @NotBlank
    @Size(max = 100)
    lateinit var password: String

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_roles",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "role_id")]
    )
    var roles = mutableSetOf<Role>()
}