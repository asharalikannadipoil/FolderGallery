package com.ali.foldergallery.domain.model

import android.net.Uri
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals
import kotlin.test.assertNull

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class AlbumTest {
    @Test
    fun `Album creation with all fields`() {
        val id = "test_album"
        val name = "Test Album"
        val coverUri = Uri.parse("content://media/external/images/media/1")
        val mediaCount = 5
        val albumType = AlbumType.REGULAR

        val album = Album(
            id = id,
            name = name,
            coverUri = coverUri,
            mediaCount = mediaCount,
            albumType = albumType
        )

        assertEquals(id, album.id)
        assertEquals(name, album.name)
        assertEquals(coverUri, album.coverUri)
        assertEquals(mediaCount, album.mediaCount)
        assertEquals(albumType, album.albumType)
    }

    @Test
    fun `Album creation with null coverUri`() {
        val id = "test_album"
        val name = "Test Album"
        val mediaCount = 0
        val albumType = AlbumType.ALL_IMAGES

        val album = Album(
            id = id,
            name = name,
            coverUri = null,
            mediaCount = mediaCount,
            albumType = albumType
        )

        assertEquals(id, album.id)
        assertEquals(name, album.name)
        assertNull(album.coverUri)
        assertEquals(mediaCount, album.mediaCount)
        assertEquals(albumType, album.albumType)
    }

    @Test
    fun `Album equality test`() {
        val album1 = Album(
            id = "test_album",
            name = "Test Album",
            coverUri = Uri.parse("content://media/external/images/media/1"),
            mediaCount = 5,
            albumType = AlbumType.REGULAR
        )

        val album2 = Album(
            id = "test_album",
            name = "Test Album",
            coverUri = Uri.parse("content://media/external/images/media/1"),
            mediaCount = 5,
            albumType = AlbumType.REGULAR
        )

        assertEquals(album1, album2)
    }

    @Test
    fun `Album copy test`() {
        val originalAlbum = Album(
            id = "test_album",
            name = "Test Album",
            coverUri = Uri.parse("content://media/external/images/media/1"),
            mediaCount = 5,
            albumType = AlbumType.REGULAR
        )

        val copiedAlbum = originalAlbum.copy(name = "New Name")

        assertEquals(originalAlbum.id, copiedAlbum.id)
        assertEquals("New Name", copiedAlbum.name)
        assertEquals(originalAlbum.coverUri, copiedAlbum.coverUri)
        assertEquals(originalAlbum.mediaCount, copiedAlbum.mediaCount)
        assertEquals(originalAlbum.albumType, copiedAlbum.albumType)
    }
} 