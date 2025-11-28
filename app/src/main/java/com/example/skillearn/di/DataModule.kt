package com.example.skillearn.di

import android.content.Context
import androidx.room.Room
import com.example.skillearn.data.local.dao.SkillDao
import com.example.skillearn.data.local.database.SkillDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideSkillDatabase(@ApplicationContext context: Context): SkillDatabase {
        return Room.databaseBuilder(
            context,
            SkillDatabase::class.java,
            "skill_db"
        )
            .fallbackToDestructiveMigration(false)
            .build()
    }

    @Provides
    fun provideSkillDao(database: SkillDatabase): SkillDao {
        return database.skillDao()
    }
}