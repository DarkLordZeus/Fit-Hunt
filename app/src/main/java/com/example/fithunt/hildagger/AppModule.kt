package com.example.fithunt.hildagger

import android.content.Context
import androidx.room.Room
import com.example.fithunt.FitHuntroomdatabase.FitHuntDatabase
import com.example.fithunt.Util.utilvalues.Companion.databasename
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn

import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideFitHuntdatabase(@ApplicationContext app:Context)=Room.databaseBuilder(app,FitHuntDatabase::class.java,databasename)
        .build()

    @Singleton
    @Provides
    fun provideFitHuntDao(db:FitHuntDatabase)=db.FitHuntDao()


}