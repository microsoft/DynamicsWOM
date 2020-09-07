package com.ttpsc.dynamics365fieldService

class AppConfiguration {
    companion object {
        val ENDPOINT_SUFFIX = "/api/data/v9.0/"
        private var _endpoint = ""
        var endpoint: String
            get() = _endpoint
            set(value) {
                _endpoint = value
        }
    }

    class StorageKeys {
        companion object {
            val environmentKey = "ENVIRONMENT_KEY"
            val userAccountKey = "USER_ACCOUNT_KEY"
            val accessTokenKey = "TOKEN_STORAGE_KEY"
            val userResourceIdKey = "USER_RESOURCE_ID_KEY"
            val accessTokenExpirationDateKey = "ACCESS_TOKEN_EXPIRATION_DATE_KEY"
            val userFirstNameKey = "USER_FIRST_NAME_KEY"
            val userLastNameKey = "USER_LAST_NAME_KEY"
        }
    }

    class RequestTags{
        companion object {
            val leaveUrl = "LEAVE_URL"
        }
    }
}