package com.teddyfreddy.kmp.android

import android.content.Context
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val androidModule = module {
    factory {
       androidApplication().getSharedPreferences(
            SharedPreferenceKeys.PreferencesFileKey.key,
            Context.MODE_PRIVATE
        )
    }
}
