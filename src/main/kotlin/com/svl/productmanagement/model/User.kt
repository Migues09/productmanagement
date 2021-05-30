package com.svl.productmanagement.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import net.bytebuddy.build.ToStringPlugin
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.validation.annotation.Validated
import java.security.Principal
import javax.persistence.*

@Entity
@Table(name = "user")
class User() {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id : Long = 0

    @Column
    var userName = ""

    @Column
    var firstName = ""

    @Column
    var lastName = ""

    @Column(unique = true)
    var email = ""

    @Column
    var pass = ""
        @JsonIgnore
        get() = field
        set(value) {
            val passwordEncoder = BCryptPasswordEncoder()
            field = passwordEncoder.encode(value)
        }

}