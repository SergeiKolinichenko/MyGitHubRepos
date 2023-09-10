package info.sergeikolinichenko.mygithubrepos.di

import dagger.Module
import dagger.Provides
import info.sergeikolinichenko.mygithubrepos.network.ApiFactory

/** Created by Sergei Kolinichenko on 08.09.2023 at 20:37 (GMT+3) **/

@Module
interface DataModule {

  companion object{
    @Provides
    @ApplicationScope
    fun provideGetUnauthorizedApi() = ApiFactory.getUnauthorizedApi()

  }
}