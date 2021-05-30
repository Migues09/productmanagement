package com.svl.productmanagement

import com.svl.productmanagement.dao.UserRepository
import com.svl.productmanagement.model.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.runApplication

@SpringBootApplication(exclude = [SecurityAutoConfiguration::class])
class ProductmanagementApplication : CommandLineRunner {

	@Autowired
	val userRepository : UserRepository? = null

	override fun run(vararg args: String?) {
		var user = User()
		user.userName = "migues06"
		user.firstName = ""
		user.lastName = ""
		user.email = "m@g.com"
		user.pass = "pass"
		userRepository!!.save(user)
//		val user = User("migues06", "migues06@gmail.com",
//						false, "", "")
//		userRepository!!.save(user)
//		val user1 = User("migues09", "migues09@gmail.com",
//			true, "", "")
//		userRepository!!.save(user1)


	}

}

fun main(args: Array<String>) {
	runApplication<ProductmanagementApplication>(*args)
}
