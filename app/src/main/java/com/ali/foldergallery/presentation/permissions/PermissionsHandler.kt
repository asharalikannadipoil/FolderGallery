package com.ali.foldergallery.presentation.permissions

import android.Manifest
import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.compose.ui.platform.LocalView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

@Composable
fun PermissionsHandler(
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val activity = context as? ComponentActivity
    val view = LocalView.current

    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        listOf(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO
        )
    } else {
        listOf(
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }

    var allPermissionsGranted by rememberSaveable {
        mutableStateOf(permissions.all { permission ->
            ContextCompat.checkSelfPermission(context, permission) == PermissionChecker.PERMISSION_GRANTED
        })
    }

    var showRationale by rememberSaveable { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsResult ->
        allPermissionsGranted = permissionsResult.all { it.value }

        // Check if rationale should be shown (for denied permissions)
        showRationale = permissions.any { permission ->
            activity?.let {
                shouldShowRequestPermissionRationale(it, permission)
            } == true
        }
    }

    var initialPermissionRequest by remember { mutableStateOf(true) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME && initialPermissionRequest) {
                permissionLauncher.launch(permissions.toTypedArray())
                initialPermissionRequest = false
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    if (allPermissionsGranted) {
        content()
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(24.dp)
            ) {
                val textToShow = if (showRationale) {
                    "Media permissions are needed to display your photos and videos. Please grant the permission."
                } else {
                    "Media permissions are required for this app to work. Please grant the permissions in app settings."
                }

                Text(
                    text = textToShow,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    permissionLauncher.launch(permissions.toTypedArray())
                }) {
                    Text("Request Permissions")
                }
            }
        }
    }
}
