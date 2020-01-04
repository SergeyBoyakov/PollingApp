package com.example.polls.features.audit.model

import com.example.polls.features.user.model.audit.DateAudit
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import javax.persistence.Column
import javax.persistence.MappedSuperclass

@MappedSuperclass
@JsonIgnoreProperties(
    value = ["createdBy", " updateBy"],
    allowGetters = true
)
abstract class UserDateAudit : DateAudit() {

    @CreatedBy
    @Column(updatable = false)
    var createdBy: Long? = null

    @LastModifiedBy
    var updateBy: Long? = null
}