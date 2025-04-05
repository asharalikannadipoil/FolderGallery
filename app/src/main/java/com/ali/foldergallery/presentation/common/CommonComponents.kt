package com.ali.foldergallery.presentation.common

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.VideoFrameDecoder
import coil.request.ImageRequest
import coil.request.videoFrameMillis
import com.ali.foldergallery.R

@Composable
fun VideoThumbnail(uri: Uri) {
    val context = LocalContext.current

    val imageLoader = remember {
        ImageLoader.Builder(context)
            .components {
                add(VideoFrameDecoder.Factory())
            }
            .build()
    }

    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(uri)
            .videoFrameMillis(1000L)
            .crossfade(true)
            .placeholder(R.drawable.ic_placeholder)
            .error(R.drawable.ic_error)
            .build(),
        imageLoader = imageLoader,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun ZoomableAsyncImage(
    model: Any,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val maxScale = 3f

    // Function to reset zoom and position
    val resetZoomAndPosition = {
        scale = 1f
        offset = Offset.Zero
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    // Update scale with constraints
                    scale = (scale * zoom).coerceIn(1f, maxScale)

                    // Apply pan offset, taking current scale into account
                    offset += pan
                }
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = { tapOffset ->
                        // Double tap to zoom in/out
                        if (scale > 1f) {
                            // If already zoomed in, reset to original size
                            resetZoomAndPosition()
                        } else {
                            // Zoom in to 2x at the tapped position
                            scale = 2f
                            // Adjust offset to zoom toward tap point
                            val newOffset = Offset(
                                x = (size.width / 2f - tapOffset.x) * scale,
                                y = (size.height / 2f - tapOffset.y) * scale
                            )
                            offset = newOffset
                        }
                    }
                )
            }
    ) {
        AsyncImage(
            model = model,
            contentDescription = contentDescription,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y
                )
        )

        // Add a reset button when zoomed in
        if (scale > 1f) {
            IconButton(
                onClick = { resetZoomAndPosition() },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(36.dp)
                    .background(Color.Gray.copy(alpha = 0.7f), shape = CircleShape)
            ) {
                // You can use an appropriate icon here
                Text(
                    text = "×",
                    color = Color.White,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}