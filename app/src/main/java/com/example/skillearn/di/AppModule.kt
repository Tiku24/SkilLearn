package com.example.skillearn.di

import com.example.skillearn.data.repository.SkillsRepositoryImpl
import com.example.skillearn.domain.repository.SkillsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Singleton
    @Binds
    abstract fun bindSkillsRepository(skillsRepositoryImpl: SkillsRepositoryImpl
    ): SkillsRepository
}