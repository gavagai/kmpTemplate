package com.teddyfreddy.kmp.viewmodel.login

import com.arkivanov.mvikotlin.core.view.BaseMviView
import com.arkivanov.mvikotlin.core.view.MviView
import com.teddyfreddy.common.ValidatedStringField
import com.teddyfreddy.common.network.NetworkRequestError
import com.teddyfreddy.kmp.mvi.login.LoginField
import com.teddyfreddy.kmp.mvi.login.LoginStore


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
    fun focusChangeUsername(focused: Boolean)
    fun changePassword(newVal: String)
    fun focusChangePassword(focused: Boolean)
    fun changeVerificationCode(newVal: String)
    fun focusChangeVerificationCode(focused: Boolean)
    fun setEmailVerificationCodeError(error: String)

    val onLabel: suspend (label: LoginStore.Label) -> Unit

    fun onSuccessfulLogin()
    fun onSignup()
}




@Suppress("unused")
open class LoginBaseMviView :
    BaseMviView<LoginMviView.Model, LoginMviView.Event>(), LoginMviView {

    private var model: LoginMviView.Model = LoginMviView.Model()
    override fun render(model: LoginMviView.Model) {
        this.model = model
    }

    override val onLabel: suspend (label: LoginStore.Label) -> Unit = { // Invoked via bindings in binder
        when (it) {
            LoginStore.Label.LoginInitiated -> {}
            is LoginStore.Label.LoginComplete -> {
                when (it.exception) {
                    is NetworkRequestError.EmailVerificationFailed -> {
                        setEmailVerificationCodeError(it.exception.failureReason!!)
                        dispatch(LoginMviView.Event.SetEmailVerificationRequired(true))
                    }
                    is NetworkRequestError.EmailVerificationCodeExpired -> {
                        setEmailVerificationCodeError(it.exception.failureReason!!)
                        dispatch(LoginMviView.Event.SetEmailVerificationRequired(true))
                    }
                    null -> {
                        dispatch(LoginMviView.Event.SetEmailVerificationRequired(false))
                    }
                    else -> {}
                }
                val snackbarMessage = when (it.exception) {
                    is NetworkRequestError -> {
                        when (it.exception) {
                            is NetworkRequestError.TransportError -> "Failed to connect to the server - Try again later" // iOS specific messages are unreadable
                            else -> "${it.exception.failureReason!!}${if (it.exception.recoverySuggestion != null) " - ${it.exception.recoverySuggestion!!}" else ""}"
                        }
                    }
                    else -> it.exception?.message
                }
                this@LoginBaseMviView.onLoginComplete(snackbarMessage)
                this@LoginBaseMviView.onSuccessfulLogin()
            }
            is LoginStore.Label.EmailVerificationCodeSent -> {
                val snackbarMessage = when (it.exception) {
                    is NetworkRequestError -> {
                        when (it.exception) {
                            is NetworkRequestError.TransportError -> "Failed to connect to the server - Try again later" // iOS specific messages are unreadable
                            else -> "${it.exception.failureReason!!}${if (it.exception.recoverySuggestion != null) " - ${it.exception.recoverySuggestion!!}" else ""}"
                        }
                    }
                    null -> null
                    else -> it.exception.message
                }

                this@LoginBaseMviView.onEmailVerificationCodeSent(snackbarMessage)
            }
        }
    }

    override fun onSuccessfulLogin() {

    }
    override fun onSignup() {

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
    override fun focusChangeUsername(focused: Boolean) {
        dispatch(LoginMviView.Event.ValidateField(LoginField.Username, focused && model.username.data.isEmpty()))
    }
    @Suppress("unused")
    override fun changePassword(newVal: String) {
        dispatch(LoginMviView.Event.ChangeField(LoginField.Password, newVal, false))
    }
    @Suppress("unused")
    override fun focusChangePassword(focused: Boolean) {
        dispatch(LoginMviView.Event.ValidateField(LoginField.Password, focused && model.password.data.isEmpty()))
    }
    @Suppress("unused")
    override fun changeVerificationCode(newVal: String) {
        dispatch(LoginMviView.Event.ChangeField(LoginField.VerificationCode, newVal, false))
    }
    @Suppress("unused")
    override fun focusChangeVerificationCode(focused: Boolean) {
        dispatch(LoginMviView.Event.ValidateField(LoginField.VerificationCode, focused && model.verificationCode.data.isEmpty()))
    }
    @Suppress("unused")
    override fun setEmailVerificationCodeError(error: String) {
        dispatch(LoginMviView.Event.SetFieldError(LoginField.VerificationCode, error))
    }
}



