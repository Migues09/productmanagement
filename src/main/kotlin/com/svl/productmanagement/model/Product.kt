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
class Product() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id : Long = 0

    @Column(unique = true)
    var productName = ""

    @Column
    var productType = ""

    @Column
    var productDesc = ""

    @Column
    var productImg = ""

    @Column
    var companyName = ""

}