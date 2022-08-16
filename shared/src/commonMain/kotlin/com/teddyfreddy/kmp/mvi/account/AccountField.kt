package com.teddyfreddy.kmp.mvi.account

import com.teddyfreddy.common.emailValidator
import com.teddyfreddy.common.stringValidator
import com.teddyfreddy.common.Field

enum class AccountField(
    override val label: String,
    override val required: Boolean = false,
    override val validator: Field.Validator? = null
) : Field {
    Username("Email", true,
        validator = Field.Validator { field: Field, value: Any?, _ ->
            emailValidator(field.label, value as? String, field.required)
        }
    ),
    Password("Password", true,
        validator = Field.Validator { field: Field, value: Any?, _ ->
            stringValidator(field.label, value as? String, field.required)
        }
    ),
    PasswordConfirmation("Password confirmation", true,
        validator = Field.Validator { field: Field, value: Any?, args: Array<out Any?> ->
            var error = stringValidator(field.label, value as? String, field.required)
            if (error == null) {
                if (value != args[0]) {
                    error = "Passwords don't match"
                }
            }
            error
        }
    ),
    FirstName("First name", true,
        validator = Field.Validator { field: Field, value: Any?, _ ->
            stringValidator(field.label, value as? String, field.required)
        }
    ),
    LastName("Last name", true,
        validator = Field.Validator { field: Field, value: Any?, _ ->
            stringValidator(field.label, value as? String, field.required)
        }
    ),
    PhoneNumber("Phone number"),
    DateOfBirth("Date of birth")
}
