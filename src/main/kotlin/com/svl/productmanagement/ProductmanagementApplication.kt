package com.svl.productmanagement

import com.sendgrid.Method
import com.sendgrid.Request
import com.sendgrid.SendGrid
import com.sendgrid.helpers.mail.Mail
import com.sendgrid.helpers.mail.objects.Content
import com.svl.productmanagement.dao.UserRepository
import com.svl.productmanagement.exception.BusinessException
import com.svl.productmanagement.model.User
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.*

@SpringBootApplication(exclude = [SecurityAutoConfiguration::class])
class ProductmanagementApplication : CommandLineRunner {

	@Autowired
	val userRepository : UserRepository? = null

	override fun run(vararg args: String?) {
		var user = User()
		user.userName = "migues09"
		user.firstName = ""
		user.lastName = ""
		user.email = "migues09@gmail.com"
		user.pass = "pass"
		user.validated = true
		try {
			userRepository!!.save(user)

		} catch (e: Exception){
			throw BusinessException(e.message)
		}

	}

}

fun main(args: Array<String>) {
	runApplication<ProductmanagementApplication>(*args)
}
