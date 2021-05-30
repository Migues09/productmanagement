package com.svl.productmanagement.web

import com.svl.productmanagement.business.IUserBusiness
import com.svl.productmanagement.dtos.LoginDTO
import com.svl.productmanagement.exception.BusinessException
import com.svl.productmanagement.exception.NotFoundException
import com.svl.productmanagement.model.User
import com.svl.productmanagement.utils.Constants
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletResponse

// Return the mapped data
@RestController
@RequestMapping(Constants.URL_BASE)
class UserRestController {

    @Autowired
    val userBusiness : IUserBusiness? = null

    //Return list of all users
    @GetMapping("/users")
    fun listAllUsers() : ResponseEntity<List<User>>{
        return try {
            ResponseEntity(userBusiness!!.listAllUsers(), HttpStatus.OK)
        } catch (e: Exception){
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    //Return a user by id
    @GetMapping("/{id}")
    fun listUser(@PathVariable("id") id: Long) : ResponseEntity<User>{
        return try {
            ResponseEntity(userBusiness!!.listUser(id), HttpStatus.OK)
        } catch (e: BusinessException){
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        } catch (e: NotFoundException){
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    //Save an user
    @PostMapping("/signUp")
    fun saveUser(@RequestBody user : User): ResponseEntity<Any> {
        return try {
            userBusiness!!.saveUser(user)
            ResponseEntity.ok(HttpStatus.CREATED)
        } catch (e: BusinessException){
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    //Update user
    @PutMapping("")
    fun updateUser(@RequestBody user : User): ResponseEntity<Any> {
        return try {
            userBusiness!!.saveUser(user)
            ResponseEntity(HttpStatus.OK)
        } catch (e: BusinessException){
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    //Delete user
    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable("id") id : Long) : ResponseEntity<Any>{
        return try {
            userBusiness!!.deleteUser(id)
            ResponseEntity(HttpStatus.OK)
        } catch (e: BusinessException){
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        } catch (e: NotFoundException){
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @PostMapping("/login")
    fun login(@RequestBody body : LoginDTO, response: HttpServletResponse) : ResponseEntity<Any> {
        val user = userBusiness!!.findByEmail(body.email) ?:
            return ResponseEntity.badRequest().body("USER NOT FOUND")

        if(!BCryptPasswordEncoder().matches(body.pass, user.pass)){
            return ResponseEntity.badRequest().body("INVALID PASSWORD")
        }

        val issuer = user.id.toString()
        val jwt = Jwts.builder()
            .setIssuer(issuer)
            .setExpiration(Date(System.currentTimeMillis() + 60 * 24 * 1000)) // 1 day\
            .signWith(SignatureAlgorithm.HS512, "secret").compact() // the secret must be stored in a secure place, for test purposes is located here

        val cookie = Cookie("jwt", jwt)
        cookie.isHttpOnly = true
        response.addCookie(cookie)

        return ResponseEntity.ok("OK")
    }
}