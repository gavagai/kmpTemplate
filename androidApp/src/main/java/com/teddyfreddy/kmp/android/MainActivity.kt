package com.teddyfreddy.kmp.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.arkivanov.decompose.defaultComponentContext
import com.teddyfreddy.kmp.Greeting
import com.teddyfreddy.kmp.android.ui.theme.Material3Theme
import com.teddyfreddy.kmp.sharedModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import com.teddyfreddy.kmp.android.ui.decompose.RootComponent
import com.teddyfreddy.android.ui.adaptive.AdaptiveDesign
import com.teddyfreddy.android.ui.adaptive.devicePostureFlow
import com.teddyfreddy.kmp.android.ui.compose.app.RootView
import org.koin.core.context.stopKoin

fun greet(): String {
    return Greeting().greeting()
}

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startKoin {
            androidContext(applicationContext)
            modules(androidModule, sharedModule)
        }

        // Create the root component before starting Compose
        val root = RootComponent(componentContext = defaultComponentContext())

        setContent {
            Material3Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Determine adaptive design parameters
                    val windowWidthSizeClass = calculateWindowSizeClass(this).widthSizeClass
                    val devicePosture = devicePostureFlow(this, lifecycleScope).collectAsState().value
                    val navigationType = AdaptiveDesign.navigationType(windowWidthSizeClass, devicePosture)
                    val contentType = AdaptiveDesign.contentType(windowWidthSizeClass, devicePosture)

                    RootView(component = root)
//                    AsyncView()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        stopKoin()
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