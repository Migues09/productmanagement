package com.svl.productmanagement.business

import com.svl.productmanagement.dao.UserRepository
import com.svl.productmanagement.exception.BusinessException
import com.svl.productmanagement.exception.NotFoundException
import com.svl.productmanagement.model.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*
import kotlin.jvm.Throws

@Service
class UserBusiness : IUserBusiness {

    // Inject the repository into business
    @Autowired
    val userRepository : UserRepository? = null

    @Throws(BusinessException::class)
    override fun listAllUsers(): List<User> {
        try {
            return userRepository!!.findAll()
        }catch (e: Exception){
            throw BusinessException(e.message)
        }
    }

    @Throws(BusinessException::class, NotFoundException::class)
    override fun listUser(id: Long): User {
        val op : Optional<User>
        try{
            op = userRepository!!.findById(id)
        } catch (e: Exception){
            throw BusinessException(e.message)
        }
        if(!op.isPresent){
            throw NotFoundException("The user with id $id is not found")
        }
        return op.get()
    }

    @Throws(BusinessException::class)
    override fun saveUser(user: User): User {
        try {
            return userRepository!!.save(user)
        } catch (e: Exception){
            throw BusinessException(e.message)
        }
    }

    @Throws(BusinessException::class, NotFoundException::class)
    override fun deleteUser(id: Long) {
        val op : Optional<User>
        try{
            op = userRepository!!.findById(id)
        } catch (e: Exception){
            throw BusinessException(e.message)
        }
        if(!op.isPresent){
            throw NotFoundException("The user with id $id is not found")
        } else {
            try{
                userRepository!!.deleteById(id)
            } catch (e: Exception){
                throw BusinessException(e.message)
            }
        }
    }

    override fun findByEmail(email: String) : User? {
        return this.userRepository!!.findByEmail(email)
    }
}