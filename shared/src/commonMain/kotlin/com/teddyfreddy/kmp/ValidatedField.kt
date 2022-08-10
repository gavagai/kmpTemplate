package com.teddyfreddy.kmp

data class ValidatedField(
    var value: Any? = null,
    var error: String? = null
)

fun stringValidator(name: String, value: String?, required: Boolean? = false, regex: Regex? = null) : String? {
    if (required != null && required) {
        if (value == null || value.isEmpty()) {
            return "$name is required"
        }
    }

    if (regex != null && value != null) {
        if (!regex.matches(value)) {
            return "$name is not in the correct format"
        }
    }

    return null
}

fun emailValidator(name: String, value: String?, required: Boolean? = false) : String? {
    // Matches constraint procedure in postgres
    val pattern = """
^[a-zA-Z0-9.!#$%&''*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$
""".trim()
    return stringValidator(name, value, required, pattern.toRegex())
}
