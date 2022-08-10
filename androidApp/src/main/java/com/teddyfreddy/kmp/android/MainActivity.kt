package com.teddyfreddy.kmp.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.teddyfreddy.kmp.Greeting
import com.teddyfreddy.kmp.android.ui.login.LoginView
import com.teddyfreddy.kmp.android.ui.registration.AccountView
import com.teddyfreddy.kmp.android.ui.theme.Material3Theme
import com.teddyfreddy.kmp.sharedModule
import com.teddyfreddy.kmp.viewmodel.AccountComposeViewModel
import com.teddyfreddy.kmp.viewmodel.RegistrationContext
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

fun greet(): String {
    return Greeting().greeting()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startKoin {
            androidContext(applicationContext)
            modules(androidModule, sharedModule)
        }

        setContent {
            Material3Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
//                    AsyncView()
//                    LoginView(
//                        onLogin = { user, password ->
//
//                        },
//                        onSignup = {
//                        }
//                    )
                    val registrationContext = RegistrationContext()
                    var vm = remember { mutableStateOf(AccountComposeViewModel(registrationContext)) }
                    vm.value.onViewCreated()
                    vm.value.onViewStarted()

                    var signup = remember { mutableStateOf(false) }
                    if (signup.value) {
                        AccountView(vm.value)
                    }
                    else {
                        LoginView(
                            onLogin = { user, password ->
                            },
                            onSignup = {
                                signup.value = true
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AsyncView() {
    var text by remember { mutableStateOf("Loading...") }

    Text(text = text)
    LaunchedEffect(key1 = "abc" ) {
        kotlin.runCatching {
            Greeting().greeting()
        }.onSuccess {
            text = it
        }.onFailure {
            text = it.localizedMessage ?: "Unknown error"
        }
    }
}