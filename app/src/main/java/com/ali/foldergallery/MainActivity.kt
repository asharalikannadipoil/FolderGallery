package com.ali.foldergallery

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.ali.foldergallery.presentation.album.AlbumListUiState
import com.ali.foldergallery.presentation.album.AlbumListViewModel
import com.ali.foldergallery.ui.theme.FolderGalleryTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //TODO: Add permission handler - Permission request is pending
        //Now we need enable permission manually
        val viewModel: AlbumListViewModel by viewModels()
        enableEdgeToEdge()
        setContent {
            FolderGalleryTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val albumstate = viewModel.uiState.collectAsState()
                    if(albumstate.value is AlbumListUiState.Success){
                        val albums = (albumstate.value as AlbumListUiState.Success).albums
                        LazyColumn(modifier = Modifier.padding(innerPadding)) {
                            items(albums){
                                Text(text = it.name)
                            }
                            item {
                                Text("If app permission is not enabled, please enable it from app settings")
                            }
                            item {
                                Text("If permission is not enabled, only mandatory folder will be shown on the screen")
                            }
                        }
                    }
                }
            }
        }
    }
}
