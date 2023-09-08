package info.sergeikolinichenko.mygithubrepos.network

import info.sergeikolinichenko.mygithubrepos.models.GithubToken
import io.reactivex.Single
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.POST

/** Created by Sergei Kolinichenko on 08.09.2023 at 17:46 (GMT+3) **/

interface ApiService {
  @Headers("Accept: application/json")
  @FormUrlEncoded
  @POST("https://github.com/login/oauth/access_token")
  fun getAuthToken(
    @Field("client_id") clientId: String,
    @Field("client_secret") clientSecret: String,
    @Field("code") code: String
  ): Single<GithubToken>
}