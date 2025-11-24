package com.example.consecutivepractices.di

import android.content.Context
import com.example.consecutivepractices.data.local.ProfilePreferences
import com.example.consecutivepractices.data.local.datastore.FilterPreferencesManager
import com.example.consecutivepractices.utils.cache.BadgeCache
import com.example.consecutivepractices.utils.cache.FavoriteCache
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
    fun provideProfilePreferences(@ApplicationContext context: Context): ProfilePreferences {
        return ProfilePreferences(context)
    }

    @Provides
    @Singleton
    fun provideFilterPreferencesManager(@ApplicationContext context: Context): FilterPreferencesManager {
        return FilterPreferencesManager(context)
    }

    @Provides
    @Singleton
    fun provideBadgeCache(): BadgeCache {
        return BadgeCache()
    }

    @Provides
    @Singleton
    fun provideFavoriteCache(): FavoriteCache {
        return FavoriteCache()
    }
}