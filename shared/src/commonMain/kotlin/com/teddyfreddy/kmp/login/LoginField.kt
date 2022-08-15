package com.teddyfreddy.kmp.login

import com.teddyfreddy.kmp.stringValidator
import com.teddyfreddy.kmp.viewmodel.Field

enum class LoginField(
    override val label: String,
    override val required: Boolean = false,
    override val validator: Field.Validator? = null
) : Field {
    Username("Username", true,
        validator = Field.Validator { field: Field, value: Any?, _ ->
            com.teddyfreddy.kmp.stringValidator(field.label, value as? String, field.required)
        }
    ),
    Password("Password", true,
        validator = Field.Validator { field: Field, value: Any?, _ ->
            stringValidator(field.label, value as? String, field.required)
        }
    )
}
