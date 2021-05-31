package com.svl.productmanagement.web

import com.sendgrid.*
import com.sendgrid.helpers.mail.Mail
import com.sendgrid.helpers.mail.objects.Content
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
import javax.validation.constraints.Email

// Return the mapped data
@RestController
@RequestMapping(Constants.URL_BASE)
class UserRestController {

    @Autowired
    val userBusiness : IUserBusiness? = null

    //Return list of all users
    @GetMapping("/users")
    fun listAllUsers(@CookieValue("jwt") jwt : String?) : ResponseEntity<Any>{
        return try {
            if(jwt == null){
                return ResponseEntity.status(401).body("unauthenticated")
            }

            val body = Jwts.parser().setSigningKey("secret").parseClaimsJws(jwt).body
            ResponseEntity(userBusiness!!.listAllUsers(), HttpStatus.OK)
        } catch (e: Exception){
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    //Return a user by id
    @GetMapping("/user")
    fun listUser(@CookieValue("jwt") jwt : String?) : ResponseEntity<Any>{
        try {
            if(jwt == null){
                return ResponseEntity.status(401).body("unauthenticated")
            }

            val body = Jwts.parser().setSigningKey("secret").parseClaimsJws(jwt).body

            return ResponseEntity(userBusiness!!.listUser(body.issuer.toLong()), HttpStatus.OK)
            //return ResponseEntity(body.issuer, HttpStatus.OK)
        } catch (e: Exception){
            return ResponseEntity.status(401).body("unauthenticated")
        }
//        return try {
//            ResponseEntity(userBusiness!!.listUser(id), HttpStatus.OK)
//        } catch (e: BusinessException){
//            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
//        } catch (e: NotFoundException){
//            ResponseEntity(HttpStatus.NOT_FOUND)
//        }
    }

    //Save an user
    @PostMapping("/signup")
    fun saveUser(@RequestBody user : User): ResponseEntity<Any> {
        return try {
            userBusiness!!.saveUser(user)
            val issuer = user.id.toString()
            val emailToken = Jwts.builder()
                .setIssuer(issuer)
                .setExpiration(Date(System.currentTimeMillis() + 60 * 1 * 1000)) // 1 hour
                .signWith(SignatureAlgorithm.HS512, "emailSecret").compact()



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

        if (!user.validated){
            return ResponseEntity.badRequest().body("YOU MUST VALIDATE YOUR EMAIL")
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

    @PostMapping("/logout")
    fun logout(response: HttpServletResponse) : ResponseEntity<Any>{
        var cookie = Cookie("jwt", "")
        cookie.maxAge = 0

        response.addCookie(cookie)
        return ResponseEntity.ok("success")
    }

    @GetMapping("/validate/{token}")
    fun validateEmail(@PathVariable("token") token : String) : ResponseEntity<Any>{
        try{
            val body = Jwts.parser().setSigningKey("emailSecret").parseClaimsJws(token).body
            val user = userBusiness!!.listUser(body.issuer.toLong())
            if(!user.validated){
                user.validated = true
                userBusiness!!.saveUser(user)
            } else {
                return ResponseEntity.badRequest().body("This e-mail it's already validated")
            }
            return ResponseEntity("e-mail validated", HttpStatus.OK)
        } catch (e: Exception){
            return ResponseEntity.status(401).body(e.message)
        }

    }
}