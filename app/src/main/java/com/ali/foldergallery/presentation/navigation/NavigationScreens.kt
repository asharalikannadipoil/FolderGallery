package com.ali.foldergallery.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ali.foldergallery.presentation.album.AlbumListScreen

sealed class Screen(val route: String) {
    object Albums : Screen("albums")
    object AlbumDetail : Screen("albumDetails")
}

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Albums.route,
        modifier = modifier
    ) {
        composable(Screen.Albums.route) {
            AlbumListScreen(
                onNavigateToAlbum = { albumId, albumName, albumType ->
                    navController.navigate(Screen.AlbumDetail.route)
                }
            )
        }
    }
}
