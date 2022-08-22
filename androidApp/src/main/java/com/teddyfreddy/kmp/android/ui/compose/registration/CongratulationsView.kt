package com.teddyfreddy.kmp.android.ui.compose.registration

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.teddyfreddy.kmp.android.ui.decompose.registration.Congratulations

@Composable
fun CongratulationsView(
    component: Congratulations,
    modifier: Modifier? = Modifier
) {
    Column {
        Text("Congratulations")
        Button(onClick = { component.finishPressed() }) {
            Text("Finish")
        }
    }
}