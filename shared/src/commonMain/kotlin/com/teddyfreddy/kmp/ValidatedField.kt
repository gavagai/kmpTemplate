package com.teddyfreddy.kmp

data class ValidatedStringField(
    var data: String = "",
    var error: String? = null
)

data class ValidatedField<T>( // No Swift
    var value: T,
    var error: String? = null
)

data class ValidatedNullableField<T>( // No Swift
    var value: T? = null,
    var error: String? = null
)

fun stringValidator(label: String, value: String?, required: Boolean? = false, regex: Regex? = null) : String? {
    if (required != null && required) {
        if (value == null || value.isEmpty()) {
            return "$label is required"
        }
    }

    if (regex != null && value != null) {
        if (!regex.matches(value)) {
            return "$label is not in the correct format"
        }
    }

    return null
}

fun emailValidator(label: String, value: String?, required: Boolean? = false) : String? {
    // Matches constraint procedure in postgres
    val pattern = """
^[a-zA-Z0-9.!#$%&''*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$
""".trim()
    return stringValidator(label, value, required, pattern.toRegex())
}
