package com.ali.foldergallery.di

import android.content.ContentResolver
import android.content.Context
import com.ali.foldergallery.data.repository.MediaRepositoryImpl
import com.ali.foldergallery.domain.repository.MediaRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideContentResolver(@ApplicationContext context: Context): ContentResolver {
        return context.contentResolver
    }

    @Provides
    @Singleton
    fun provideMediaRepository(mediaRepositoryImpl: MediaRepositoryImpl): MediaRepository {
        return mediaRepositoryImpl
    }
}