package info.sergeikolinichenko.mygithubrepos.di

import android.app.Application
import android.content.SharedPreferences
import dagger.Binds
import dagger.Module
import dagger.Provides
import info.sergeikolinichenko.mygithubrepos.preferences.AppSharedPreferences
import info.sergeikolinichenko.mygithubrepos.repository.Repository
import info.sergeikolinichenko.mygithubrepos.repository.RepositoryImpl

/** Created by Sergei Kolinichenko on 08.09.2023 at 20:37 (GMT+3) **/

@Module
interface DataModule {

  @Binds
  @ApplicationScope
  fun bindRepository(impl: RepositoryImpl): Repository

  companion object {
    @Provides
    fun provideAppSharedPreferences(application: Application): SharedPreferences {
      return AppSharedPreferences.getInstance(application)
    }
  }
}