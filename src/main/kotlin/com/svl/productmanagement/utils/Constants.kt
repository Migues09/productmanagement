package com.svl.productmanagement.utils

class Constants {

    companion object{
        private const val URL_API_BASE = "/api"
        private const val URL_API_VERSION = "/v1"
        const val URL_BASE = URL_API_BASE + URL_API_VERSION
        const val URL_COMPANY = "$URL_BASE/company"
        // User Base API endpoint
        //const val URL_BASE = "$URL_BASE/users"
    }
}