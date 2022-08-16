package com.teddyfreddy.kmp.mvi

data class RegistrationContext(
    var email: String? = null,
    var givenName: String? = null,
    var familyName: String? = null
)
