package com.teddyfreddy.common

data class ValidatedStringField(
    val data: String,
    val error: String?
) {
    constructor() : this(
        data = "",
        error = null
    )
    constructor(data: String) : this(
        data = data,
        error = null
    )

}

data class ValidatedField<T>( // No Swift
    val data: T,
    val error: String? = null
)

data class ValidatedNullableField<T>( // No Swift
    val data: T? = null,
    val error: String? = null
)

fun stringValidator(
    label: String,
    value: String?,
    required: Boolean? = false,
    regex: Regex? = null,
    regexDescription: String? = null) : String? {
    if (required != null && required) {
        if (value == null || value.isEmpty()) {
            return "$label is required"
        }
    }

    if (regex != null && value != null) {
        if (!regex.matches(value)) {
            return "$label is not in the correct format${if (regexDescription != null) " [$regexDescription]" else ""}"
        }
    }

    return null
}

fun emailValidator(label: String, value: String?, required: Boolean? = false) : String? {
    // Matches constraint procedure in postgres
    val pattern =
        "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$"
    return stringValidator(label, value, required, pattern.toRegex(), "email address")
}

fun oneTimeCodeValidator(label: String, value: String?, required: Boolean? = false) : String? {
    // Matches constraint procedure in postgres
    val pattern = "[1-9][0-9]{5}"
    return stringValidator(label, value, required, pattern.toRegex(), "6 digits")
}
