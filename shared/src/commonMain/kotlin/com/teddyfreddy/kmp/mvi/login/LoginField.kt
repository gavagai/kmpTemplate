package com.teddyfreddy.kmp.mvi.login

import com.teddyfreddy.common.stringValidator
import com.teddyfreddy.common.Field

enum class LoginField(
    override val label: String,
    override val required: Boolean = false,
    override val validator: Field.Validator? = null
) : Field {
    Username("Username", true,
        validator = Field.Validator { field: Field, value: Any?, _ ->
            stringValidator(field.label, value as? String, field.required)
        }
    ),
    Password("Password", true,
        validator = Field.Validator { field: Field, value: Any?, _ ->
            stringValidator(field.label, value as? String, field.required)
        }
    ),
    VerificationCode("Verification code", true,
        validator = Field.Validator { field: Field, value: Any?, _ ->
            stringValidator(field.label, value as? String, field.required, "[1-9][0-9]{5}".toRegex())
        }
    )
}
