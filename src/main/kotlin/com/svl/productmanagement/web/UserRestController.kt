package com.svl.productmanagement.web

import com.sendgrid.*
import com.sendgrid.helpers.mail.Mail
import com.sendgrid.helpers.mail.objects.Content
import com.svl.productmanagement.business.IUserBusiness
import com.svl.productmanagement.dtos.UserDTO
import com.svl.productmanagement.exception.BusinessException
import com.svl.productmanagement.exception.NotFoundException
import com.svl.productmanagement.model.User
import com.svl.productmanagement.utils.Constants
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse

// Return the mapped data
@Controller
@RequestMapping(Constants.URL_USERS)
class UserRestController {

    @Autowired
    val userBusiness : IUserBusiness? = null

    //Return list of all users
    @GetMapping("/all")
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
    @GetMapping("/{id}")
    fun listUser(@CookieValue("jwt") jwt : String?, @PathVariable("id") id : Long) : ResponseEntity<Any>{
        try {
            if(jwt == null){
                return ResponseEntity.status(401).body("unauthenticated")
            }

            val body = Jwts.parser().setSigningKey("secret").parseClaimsJws(jwt).body

            return ResponseEntity(userBusiness!!.listUser(id), HttpStatus.OK)
        }catch (e: BusinessException){
            return ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        } catch (e: NotFoundException){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not Found")
        }
    }

    //Save an user
    @PostMapping("/signup")
    fun saveUser(@RequestBody user : User): ResponseEntity<Any> {
        return try {
            userBusiness!!.saveUser(user)
            val issuer = user.id.toString()
			val emailToken = Jwts.builder()
				.setIssuer(issuer)
				.setExpiration(Date(System.currentTimeMillis() + 60 * 24 * 1000)) // 1 hour
				.signWith(SignatureAlgorithm.HS512, "emailSecret").compact()

			val validateUrl = "http://localhost:8080/api/v1/user/validate/${emailToken}"
			val sendGrid = SendGrid("#API_KEY#")
			val from = com.sendgrid.helpers.mail.objects.Email("migues09@gmail.com")
			val subject = "Validate your Email"
			val to = com.sendgrid.helpers.mail.objects.Email(user.email)
			val content = Content("text/plain", "Click on the link below to validate your email /n $validateUrl")
			val mail = Mail(from, subject, to, content)

			val request = Request()
			request.method = Method.POST
			request.endpoint = "mail/send"
			request.body = mail.build()
			val response = sendGrid.api(request)

            ResponseEntity.ok("User created. Please verify your e-mail to validate your account ")
        } catch (e: BusinessException){
            ResponseEntity.status(500).body("Server Error")
        }
    }

    //Update user
    @PutMapping("/{id}")
    fun updateUser(@CookieValue("jwt") jwt : String?, @RequestBody user : User, @PathVariable("id") id : Long): ResponseEntity<Any> {
        return try {
            if(jwt == null){
                return ResponseEntity.status(401).body("unauthenticated")
            }
            val body = Jwts.parser().setSigningKey("secret").parseClaimsJws(jwt).body
            if(body.issuer.toLong() != id){
                return ResponseEntity.status(401).body("You can modify only your data")
            }

            var auxUser = userBusiness!!.listUser(id)

            auxUser.firstName = user.firstName
            auxUser.lastName = user.lastName
            auxUser.email = user.email
            auxUser.userName = user.userName

            userBusiness!!.saveUser(auxUser)

            ResponseEntity.ok("User updated!!")
        } catch (e: BusinessException){
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }catch (e:NotFoundException){
            ResponseEntity.status(HttpStatus.NOT_FOUND).body("Failed to update. User not Found")
        }
    }

    //Restore pass
    @PutMapping("/restore_pass")
    fun updatePassword(@CookieValue("jwt") jwt : String?, @RequestBody userDTO : UserDTO): ResponseEntity<Any> {
        return try {
            if(jwt == null){
                return ResponseEntity.status(401).body("unauthenticated")
            }
            var user = userBusiness!!.findByEmail(userDTO.email)
            if(user == null){
                return ResponseEntity.status(404).body("User Not Found")
            }
            user!!.pass = userDTO.pass
            userBusiness!!.saveUser(user)

            return ResponseEntity.ok("Password changed!!")
        } catch (e: BusinessException){
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        } catch (e: NotFoundException){
            ResponseEntity.status(HttpStatus.NOT_FOUND).body("User Not Found")
        }
    }

    //User login
    @PostMapping("/login")
    fun login(@RequestBody body : UserDTO, response: HttpServletResponse) : ResponseEntity<Any> {
       try {
           val user =
               userBusiness!!.findByEmail(body.email) ?: return ResponseEntity.badRequest().body("USER NOT FOUND")

           if (!BCryptPasswordEncoder().matches(body.pass, user.pass)) {
               return ResponseEntity.badRequest().body("INVALID PASSWORD")
           }

           if (!user.validated) {
               return ResponseEntity.badRequest().body("YOU MUST VALIDATE YOUR EMAIL")
           }

           val issuer = user.id.toString()
           val jwt = Jwts.builder()
               .setIssuer(issuer)
               .setExpiration(Date(System.currentTimeMillis() + 60 * 24 * 1000)) // 1 day\
               .signWith(SignatureAlgorithm.HS512, "secret")
               .compact() // the secret must be stored in a secure place, for test purposes is located here

           val cookie = Cookie("jwt", jwt)
           cookie.isHttpOnly = true
           response.addCookie(cookie)

           return ResponseEntity.ok("Logged in!!")
       } catch (e: Exception) {
           return ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
       }
    }

    @GetMapping("/logout")
    fun logout(response: HttpServletResponse) : ResponseEntity<Any>{
        var cookie = Cookie("jwt", "")
        cookie.maxAge = 0

        response.addCookie(cookie)
        return ResponseEntity.ok("Logout success!!")
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
