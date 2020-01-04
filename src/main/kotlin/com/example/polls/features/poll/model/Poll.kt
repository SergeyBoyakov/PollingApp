package com.example.polls.features.poll.model

import com.example.polls.features.audit.model.UserDateAudit
import org.hibernate.annotations.BatchSize
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
import java.time.Instant
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity
@Table(name = "polls")
class Poll : UserDateAudit() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @NotBlank
    @Size(max = 140)
    lateinit var question: String

    @OneToMany(
        mappedBy = "poll",
        cascade = [CascadeType.ALL],
        fetch = FetchType.EAGER,
        orphanRemoval = true
    )
    @Size(min = 2, max = 6)
    @Fetch(FetchMode.SELECT)
    @BatchSize(size = 30)
    val choices = mutableListOf<Choice>()

    @NotNull
    lateinit var expirationDateTime: Instant

    fun addChoice(choice: Choice) {
        choices.add(choice)
        choice.poll = this
    }

    fun removeChoice(choice: Choice) {
        choices.remove(choice)
        choice.poll = null
    }
}