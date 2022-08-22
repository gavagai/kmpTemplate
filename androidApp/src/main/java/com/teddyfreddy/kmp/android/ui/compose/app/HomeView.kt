package com.teddyfreddy.kmp.android.ui.compose.app

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.teddyfreddy.kmp.android.ui.decompose.app.Home

@Composable
fun HomeView(
    component: Home,
    modifier: Modifier? = Modifier
) {
    Column {
        Text("Home")
        Row(horizontalArrangement = Arrangement.SpaceAround) {
            Button(onClick = { component.logoutPressed() }) {
                Text("Logout")
            }
            Spacer(modifier = Modifier.padding(10.dp))
            Button(onClick = { component.backPressed() }) {
                Text("Back")
            }
        }
    }
}