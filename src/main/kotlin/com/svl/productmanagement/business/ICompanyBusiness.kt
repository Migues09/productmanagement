package com.svl.productmanagement.business

import com.svl.productmanagement.model.Company

interface ICompanyBusiness {

    fun listAllCompanies() : List<Company>
    fun listCompany(id : Long) : Company
    fun saveCompany(company: Company) : Company
}