package com.example.notably.di

import com.example.notably.repos.FakeDataSourceImpl
import com.example.notably.repos.NoteDataSource
import com.example.notably.repos.NoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
annotation class RealRepos

@Qualifier
annotation class FakeRepos

@Module
@InstallIn(SingletonComponent::class)
abstract class ReposModule {

    @RealRepos
    @Singleton
    @Binds
    abstract fun provideNoteRepos(noteDataSourceImpl: NoteDataSourceImpl): NoteDataSource
}

@Module
@InstallIn(SingletonComponent::class)
abstract class FakeModule {

    @FakeRepos
    @Singleton
    @Binds
    abstract fun provideFakeRepos(fakeDataSourceImpl: FakeDataSourceImpl): NoteDataSource
}
