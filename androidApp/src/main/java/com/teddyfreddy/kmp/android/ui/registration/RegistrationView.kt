package com.teddyfreddy.kmp.android.ui.registration

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.teddyfreddy.kmp.android.ui.decompose.Registration

@Composable
fun RegistrationView(
    component: Registration,
    modifier: Modifier? = Modifier
) {
    Text("Registration!")
    Button(
        onClick = { component.finish(username = "drobertson") }
    ) {
        Text("Finish")
    }
}