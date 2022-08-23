package com.teddyfreddy.kmp

enum class SettingsKeys(val value: String? = null) {
    PreferencesFileKey("com.teddyfreddy.kmp.shared_preferences_file"),
    RecentUsername,
    EmailVerified
    ;

    val key: String
        get() {
            return value ?: name
        }
}