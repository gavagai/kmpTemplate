package com.teddyfreddy.kmp.android

enum class SharedPreferenceKeys(val value: String? = null) {
    PreferencesFileKey("com.teddyfreddy.kmp.shared_preferences_file"),
    RecentUsername,
    EmailVerified
    ;

    val key: String
        get() {
            return value ?: name
        }
}