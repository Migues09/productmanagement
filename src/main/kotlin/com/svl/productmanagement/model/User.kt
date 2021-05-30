package com.svl.productmanagement.model

import javax.persistence.*

@Entity
@Table(name = "user")
data class User(val userName : String, val pass : String, val email : String, val name : String = "",
                val lastName : String = ""){
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id : Long = 0
}