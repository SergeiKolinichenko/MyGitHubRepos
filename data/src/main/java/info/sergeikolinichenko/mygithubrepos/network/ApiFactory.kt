package info.sergeikolinichenko.mygithubrepos.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/** Created by Sergei Kolinichenko on 08.09.2023 at 18:53 (GMT+3) **/

object ApiFactory {
  private const val BASE_URL = "https://api.github.com/"

  fun getUnauthorizedApi(): ApiService =
    Retrofit.Builder()
      .baseUrl(BASE_URL)
      .addConverterFactory(GsonConverterFactory.create())
      .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
      .build()
      .create(ApiService::class.java)

  fun getAuthorizedApi(token: String): ApiService {
    val okHttpClient = OkHttpClient.Builder()

    val logging = HttpLoggingInterceptor()
    logging.level = HttpLoggingInterceptor.Level.BODY

    okHttpClient.addInterceptor(logging) // delete

    okHttpClient.addInterceptor { chain ->
      val request = chain.request()
      val newRequest = request.newBuilder()
        .addHeader("Authorization", "token $token")
        .build()

      chain.proceed(newRequest)
    }
    return  Retrofit.Builder()
      .baseUrl(BASE_URL)
      .client(okHttpClient.build())
      .addConverterFactory(GsonConverterFactory.create())
      .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
      .build()
      .create(ApiService::class.java)
  }


}