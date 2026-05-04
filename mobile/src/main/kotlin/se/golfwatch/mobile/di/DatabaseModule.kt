package se.golfwatch.mobile.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import se.golfwatch.mobile.data.local.CourseDao
import se.golfwatch.mobile.data.local.GolfDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
    ): GolfDatabase = Room.databaseBuilder(context, GolfDatabase::class.java, "golf.db").build()

    @Provides
    fun provideCourseDao(db: GolfDatabase): CourseDao = db.courseDao()
}
