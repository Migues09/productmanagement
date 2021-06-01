package com.svl.productmanagement.business

import com.svl.productmanagement.dao.CompanyRepository
import com.svl.productmanagement.exception.BusinessException
import com.svl.productmanagement.exception.NotFoundException
import com.svl.productmanagement.model.Company
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*
import kotlin.jvm.Throws

@Service
class CompanyBusiness : ICompanyBusiness {

    // Inject the repository into business
    @Autowired
    val companyRepository : CompanyRepository? = null

    @Throws(BusinessException::class)
    override fun listAllCompanies(): List<Company> {
        try {
            return companyRepository!!.findAll()
        }catch (e: Exception){
            throw BusinessException(e.message)
        }
    }

    @Throws(BusinessException::class, NotFoundException::class)
    override fun listCompany(id: Long): Company {
        val op : Optional<Company>
        try{
            op = companyRepository!!.findById(id)
        } catch (e: Exception){
            throw BusinessException(e.message)
        }
        if(!op.isPresent){
            throw NotFoundException("The company with id $id is not found")
        }
        return op.get()
    }

    @Throws(BusinessException::class)
    override fun saveCompany(company: Company): Company {
        try {
            return companyRepository!!.save(company)
        } catch (e: Exception){
            throw BusinessException(e.message)
        }
    }
}