package com.svl.productmanagement.model

import javax.persistence.*

@Entity
@Table(name = "company")
class Company() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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