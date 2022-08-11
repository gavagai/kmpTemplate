package com.teddyfreddy.kmp.viewmodel

interface Field {
    val label: String
    val required: Boolean
    val validator: Validator?

    fun interface Validator {
        fun validate(field: Field, value: Any?, vararg args: Any?) : String?
    }
}
