package com.ali.foldergallery.presentation.screens.album

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.ali.foldergallery.R
import com.ali.foldergallery.domain.model.Album
import com.ali.foldergallery.presentation.common.VideoThumbnail
import java.net.URLEncoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumListScreen(
    onNavigateToAlbum: (String, String, String) -> Unit,
    viewModel: AlbumListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var isGridView by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gallery") },
                actions = {
                    IconButton(onClick = { isGridView = !isGridView }) {
                        Icon(
                            painter = if (isGridView) painterResource(R.drawable.ic_list) else painterResource(
                                R.drawable.ic_grid
                            ),
                            contentDescription = if (isGridView) "Switch to List View" else "Switch to Grid View"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is AlbumListUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is AlbumListUiState.Success -> {
                if (isGridView) {
                    // Grid View
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 160.dp),
                        contentPadding = PaddingValues(8.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        items(state.albums) { album ->
                            AlbumGridItem(
                                album = album,
                                onAlbumClick = {
                                    onNavigateToAlbum(
                                        URLEncoder.encode(album.id, "utf-8"),
                                        album.name,
                                        album.albumType.name
                                    )
                                }
                            )
                        }
                    }
                } else {
                    // Linear View
                    LazyColumn(
                        contentPadding = PaddingValues(8.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        items(state.albums) { album ->
                            AlbumListItem(
                                album = album,
                                onAlbumClick = {
                                    onNavigateToAlbum(
                                        URLEncoder.encode(album.id, "utf-8"),
                                        album.name,
                                        album.albumType.name
                                    )
                                }
                            )
                        }
                    }
                }
            }

            is AlbumListUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = state.message)
                }
            }
        }
    }
}

@Composable
fun AlbumGridItem(
    album: Album,
    onAlbumClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(4.dp)
            .clickable(onClick = onAlbumClick)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                if (album.coverUri != null) {
                    if (album.coverUri.path?.contains("/video/") == true) {
                        VideoThumbnail(album.coverUri)
                    } else {
                        AsyncImage(
                            model = album.coverUri,
                            contentDescription = album.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                } else {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Text("No Media")
                    }
                }
            }
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    maxLines = 1,
                    text = album.name,
                    style = MaterialTheme.typography.bodyLarge,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${album.mediaCount} items",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun AlbumListItem(
    album: Album,
    onAlbumClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .clickable(onClick = onAlbumClick)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.height(80.dp)
        ) {
            // Thumbnail
            Box(
                modifier = Modifier
                    .size(80.dp)
            ) {
                if (album.coverUri != null) {
                    if (album.coverUri.path?.contains("/video/") == true) {
                        VideoThumbnail(album.coverUri)
                    } else {
                        AsyncImage(
                            model = album.coverUri,
                            contentDescription = album.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                } else {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Text("No Media")
                    }
                }
            }

            // Album details
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .weight(1f)
            ) {
                Text(
                    maxLines = 1,
                    text = album.name,
                    style = MaterialTheme.typography.bodyLarge,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${album.mediaCount} items",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}