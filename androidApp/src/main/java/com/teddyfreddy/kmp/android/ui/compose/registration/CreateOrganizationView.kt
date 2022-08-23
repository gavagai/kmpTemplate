package com.teddyfreddy.kmp.android.ui.compose.registration

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.teddyfreddy.kmp.decompose.registration.CreateOrganization

@Composable
fun CreateOrganizationView(
    component: CreateOrganization,
    modifier: Modifier? = Modifier
) {
    Column(
        modifier = modifier ?: Modifier
    ) {
        Text("CreateOrganization")
        Row(horizontalArrangement = Arrangement.SpaceAround) {
            Button(onClick = { component.cancelPressed() }) {
                Text("Cancel")
            }
            Spacer(modifier = Modifier.padding(10.dp))
            Button(onClick = { component.continuePressed() }) {
                Text("Continue")
            }
            Spacer(modifier = Modifier.padding(10.dp))
            Button(onClick = { component.backPressed() }) {
                Text("Back")
            }
        }
    }
}