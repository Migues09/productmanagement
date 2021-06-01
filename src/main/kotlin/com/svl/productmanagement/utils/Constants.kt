package com.svl.productmanagement.utils

class Constants {

    companion object{
        private const val URL_API_BASE = "/api"
        private const val URL_API_VERSION = "/v1"
        const val URL_BASE = URL_API_BASE + URL_API_VERSION
        // Company Base API endpoint
        const val URL_COMPANY = "$URL_BASE/company"
        // Product Base API endpoint
        const val URL_PRODUCT = "$URL_BASE/product"
        // User Base API endpoint
        const val URL_USERS= "$URL_BASE/user"
    }
}