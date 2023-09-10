package info.sergeikolinichenko.mygithubrepos.di

import dagger.Module
import dagger.Provides
import info.sergeikolinichenko.mygithubrepos.network.ApiFactory

/** Created by Sergei Kolinichenko on 10.09.2023 at 18:24 (GMT+3) **/

@Module
class TokenModule(private val token: String) {

  @Provides
  @ApplicationScope
  fun getAuthorizedApi() = ApiFactory.getAuthorizedApi(token)
}