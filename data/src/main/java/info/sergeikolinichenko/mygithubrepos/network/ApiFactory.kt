package info.sergeikolinichenko.mygithubrepos.network

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/** Created by Sergei Kolinichenko on 08.09.2023 at 18:53 (GMT+3) **/

object ApiFactory {
  private const val BASE_URL = "https://api.github.com/"

  fun getUnauthorizedApi() =
    Retrofit.Builder()
      .baseUrl(BASE_URL)
      .addConverterFactory(GsonConverterFactory.create())
      .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
      .build()
      .create(ApiService::class.java)

}