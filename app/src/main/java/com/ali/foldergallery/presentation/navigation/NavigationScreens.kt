package com.ali.foldergallery.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ali.foldergallery.presentation.album.AlbumListScreen
import com.ali.foldergallery.presentation.albumdetail.AlbumDetailScreen
import com.ali.foldergallery.presentation.mediadetail.MediaDetailScreen

sealed class Screen(val route: String) {
    object Albums : Screen("albums")
    object AlbumDetail : Screen("album/{albumId}/{albumName}/{albumType}") {
        fun createRoute(albumId: String, albumName: String, albumType: String): String {
            return "album/$albumId/$albumName/$albumType"
        }
    }
    object MediaDetail : Screen("media/{mediaId}") {
        fun createRoute(mediaId: Long): String {
            return "media/$mediaId"
        }
    }
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
                    navController.navigate(Screen.AlbumDetail.createRoute(albumId, albumName, albumType))
                }
            )
        }

        composable(
            route = Screen.AlbumDetail.route,
            arguments = listOf(
                navArgument("albumId") { type = NavType.StringType },
                navArgument("albumName") { type = NavType.StringType },
                navArgument("albumType") { type = NavType.StringType }
            )
        ) {
            AlbumDetailScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToMediaDetail = { mediaId ->
                    navController.navigate(Screen.MediaDetail.createRoute(mediaId))
                }
            )
        }

        composable(
            route = Screen.MediaDetail.route,
            arguments = listOf(
                navArgument("mediaId") { type = NavType.LongType }
            )
        ) {
            MediaDetailScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
