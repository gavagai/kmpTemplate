package com.teddyfreddy.kmp.decompose.registration

import com.arkivanov.decompose.value.Value
import com.teddyfreddy.common.ValidatedStringField
import kotlinx.datetime.LocalDate

interface Account {
    data class Model (
        val email: ValidatedStringField = ValidatedStringField(data = ""),
        val password: ValidatedStringField = ValidatedStringField(data = ""),
        val passwordConfirmation: ValidatedStringField = ValidatedStringField(data = ""),
        val givenName: ValidatedStringField = ValidatedStringField(data = ""),
        val familyName: ValidatedStringField = ValidatedStringField(data = ""),
        val phone: String = "",
        val dateOfBirth: LocalDate? = null,
    ) {
        val valid: Boolean
            get() =
                email.error == null &&
                        password.error == null &&
                        passwordConfirmation.error == null &&
                        givenName.error == null &&
                        familyName.error == null
    }
    val model: Value<Model>

    fun changeEmail(newVal: String)
    fun focusChangeEmail(focused: Boolean)
    fun changePassword(newVal: String)
    fun focusChangePassword(focused: Boolean)
    fun changePasswordConfirmation(newVal: String)
    fun focusChangePasswordConfirmation(focused: Boolean)
    fun changeFirstName(newVal: String)
    fun focusChangeFirstName(focused: Boolean)
    fun changeLastName(newVal: String)
    fun focusChangeLastName(focused: Boolean)
    fun changePhoneNumber(newVal: String)
    fun changeDateOfBirth(newVal: LocalDate?)

    fun cancelPressed()
    fun continuePressed()
}