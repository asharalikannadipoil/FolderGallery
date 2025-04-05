package com.ali.foldergallery.presentation.mediadetail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MediaDetailScreen(onNavigateBack: () -> Unit) {
    Column(modifier = Modifier.padding(10.dp).padding(top = 30.dp)) {
        Text("Work under progress :)")
        Button(onClick = { onNavigateBack.invoke() }) {
            Text("OK")
        }
    }
}