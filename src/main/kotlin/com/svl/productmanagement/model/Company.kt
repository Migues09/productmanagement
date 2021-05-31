package com.svl.productmanagement.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import net.bytebuddy.build.ToStringPlugin
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.validation.annotation.Validated
import java.security.Principal
import javax.persistence.*

@Entity
@Table(name = "product")
class Company() {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id : Long = 0

    @Column(unique = true)
    var companyName = ""

    @Column
    var adress = ""

    @Column
    var phone = ""

    @Column
    var city = ""

}