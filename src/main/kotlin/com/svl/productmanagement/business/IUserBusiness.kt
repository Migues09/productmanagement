package com.svl.productmanagement.business

import com.svl.productmanagement.model.User

interface IUserBusiness {

    fun listAllUsers() : List<User>
    fun listUser(id : Long) : User
    fun saveUser(user : User) : User
    fun deleteUser(id : Long)
    fun findByEmail(email : String) : User?
}