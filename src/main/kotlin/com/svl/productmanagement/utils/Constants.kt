package com.svl.productmanagement.utils

class Constants {

    companion object{
        private const val URL_API_BASE = "/api"
        private const val URL_API_VERSION = "/v1"
        private const val URL_BASE = URL_API_BASE + URL_API_VERSION
        // User Base API endpoint
        const val URL_BASE_USER = "$URL_BASE/users"
    }
}