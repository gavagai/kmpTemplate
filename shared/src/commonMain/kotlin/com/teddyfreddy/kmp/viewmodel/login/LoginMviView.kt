package com.teddyfreddy.kmp.viewmodel.login

import com.arkivanov.mvikotlin.core.view.BaseMviView
import com.arkivanov.mvikotlin.core.view.MviView
import com.teddyfreddy.common.ValidatedStringField
import com.teddyfreddy.common.network.NetworkRequestError
import com.teddyfreddy.common.network.NetworkResponse
import com.teddyfreddy.kmp.mvi.login.LoginField
import com.teddyfreddy.kmp.mvi.login.LoginStore
import com.teddyfreddy.kmp.repository.LoginResponseDTO


interface LoginMviView : MviView<LoginMviView.Model, LoginMviView.Event> {

    data class Model(
        val username: ValidatedStringField,
        val password: ValidatedStringField,
        val verificationCode: ValidatedStringField,
        val emailVerificationRequired: Boolean
    ) {
        // No-arg constructor for Swift.
        @Suppress("unused")
        constructor() : this(
            username = ValidatedStringField(data = ""),
            password = ValidatedStringField(data = ""),
            verificationCode = ValidatedStringField(data = ""),
            emailVerificationRequired = false
        )
    }

    sealed interface Event {
        data class ChangeField(val field: LoginField, val value: Any?, val validate: Boolean = false) : Event
        data class ValidateField(val field: LoginField, val forceValid: Boolean? = false) : Event
        data class SetFieldError(val field: LoginField, val error: String) : Event
        data class SetEmailVerificationRequired(val required: Boolean) : Event
        object Login : Event
        object SendEmailVerificationCode: Event
    }

    fun login(onLoginComplete: (message: String?) -> Unit)
    fun signup()
    fun getNewCode(onEmailVerificationCodeSent: (message: String?) -> Unit)

    fun changeUsername(newVal: String)
    fun validateUsername(forceValid: Boolean)
    fun changePassword(newVal: String)
    fun validatePassword(forceValid: Boolean)
    fun changeVerificationCode(newVal: String)
    fun validateVerificationCode(forceValid: Boolean)
    fun setEmailVerificationCodeError(error: String)

    val onLabel: suspend (label: LoginStore.Label) -> Unit

    fun onLogin(response: NetworkResponse<LoginResponseDTO>?, exception: Throwable?)
    fun onSignup()

    fun onSetRecentUser(username: String)
    fun onSetEmailVerified(verified: Boolean)
}




@Suppress("unused")
open class LoginBaseMviView :
    BaseMviView<LoginMviView.Model, LoginMviView.Event>(), LoginMviView {

    override val onLabel: suspend (label: LoginStore.Label) -> Unit = { // Invoked via bindings in binder
        when (it) {
            LoginStore.Label.LoginInitiated -> {}
            is LoginStore.Label.LoginComplete -> {
                when (it.exception) {
                    is NetworkRequestError.EmailVerificationFailed -> {
                        setEmailVerificationCodeError(it.exception.failureReason!!)
                        dispatch(LoginMviView.Event.SetEmailVerificationRequired(true))
                        this@LoginBaseMviView.onSetEmailVerified(false)
                    }
                    is NetworkRequestError.EmailVerificationCodeExpired -> {
                        setEmailVerificationCodeError(it.exception.failureReason!!)
                        dispatch(LoginMviView.Event.SetEmailVerificationRequired(true))
                        this@LoginBaseMviView.onSetEmailVerified(false)
                    }
                    null -> {
                        this@LoginBaseMviView.onSetEmailVerified(true)
                        dispatch(LoginMviView.Event.SetEmailVerificationRequired(false))
                    }
                    else -> {}
                }
                val snackbarMessage = when (it.exception) {
                    is NetworkRequestError -> {
                        "${it.exception.failureReason!!}${if (it.exception.recoverySuggestion != null) " - ${it.exception.recoverySuggestion!!}" else ""}"
                    }
                    else -> it.exception?.message
                }
                this@LoginBaseMviView.onLoginComplete(snackbarMessage)
                this@LoginBaseMviView.onLogin(it.response, it.exception)
            }
            is LoginStore.Label.EmailVerificationCodeSent -> {
                val snackbarMessage = when (it.exception) {
                    is NetworkRequestError -> {
                        "${it.exception.failureReason!!}${if (it.exception.recoverySuggestion != null) " - ${it.exception.recoverySuggestion!!}" else ""}"
                    }
                    null -> null
                    else -> it.exception?.message
                }

                this@LoginBaseMviView.onEmailVerificationCodeSent(snackbarMessage)
            }
        }
    }

    override fun onLogin(response: NetworkResponse<LoginResponseDTO>?, exception: Throwable?) {

    }
    override fun onSignup() {

    }
    override fun onSetRecentUser(username: String) {

    }
    override fun onSetEmailVerified(verified: Boolean) {

    }


    private lateinit var onLoginComplete: (message: String?) -> Unit
    override fun login(onLoginComplete: (message: String?) -> Unit) {
        this.onLoginComplete = onLoginComplete
        dispatch(LoginMviView.Event.Login)
    }

    override fun signup() {
        this.onSignup()
    }

    private lateinit var onEmailVerificationCodeSent: (message: String?) -> Unit
    @Suppress("unused")
    override fun getNewCode(onEmailVerificationCodeSent: (message: String?) -> Unit) {
        this.onEmailVerificationCodeSent = onEmailVerificationCodeSent
        dispatch(LoginMviView.Event.SendEmailVerificationCode)
    }

    @Suppress("unused")
    override fun changeUsername(newVal: String) {
        dispatch(LoginMviView.Event.ChangeField(LoginField.Username, newVal, false))
    }
    @Suppress("unused")
    override fun validateUsername(forceValid: Boolean) {
        dispatch(LoginMviView.Event.ValidateField(LoginField.Username, forceValid))
    }
    @Suppress("unused")
    override fun changePassword(newVal: String) {
        dispatch(LoginMviView.Event.ChangeField(LoginField.Password, newVal, false))
    }
    @Suppress("unused")
    override fun validatePassword(forceValid: Boolean) {
        dispatch(LoginMviView.Event.ValidateField(LoginField.Password, forceValid))
    }
    @Suppress("unused")
    override fun changeVerificationCode(newVal: String) {
        dispatch(LoginMviView.Event.ChangeField(LoginField.VerificationCode, newVal, false))
    }
    @Suppress("unused")
    override fun validateVerificationCode(forceValid: Boolean) {
        dispatch(LoginMviView.Event.ValidateField(LoginField.VerificationCode, forceValid))
    }
    @Suppress("unused")
    override fun setEmailVerificationCodeError(error: String) {
        dispatch(LoginMviView.Event.SetFieldError(LoginField.VerificationCode, error))
    }
}


