package info.sergeikolinichenko.mygithubrepos.network

import info.sergeikolinichenko.mygithubrepos.models.GithubCommentDto
import info.sergeikolinichenko.mygithubrepos.models.GithubPullRequestDto
import info.sergeikolinichenko.mygithubrepos.models.GithubRepoDto
import info.sergeikolinichenko.mygithubrepos.models.GithubTokenDto
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

/** Created by Sergei Kolinichenko on 08.09.2023 at 17:46 (GMT+3) **/

interface ApiService {
  @Headers("Accept: application/json")
  @FormUrlEncoded
  @POST("https://github.com/login/oauth/access_token")
  suspend fun getAuthToken(
    @Field("client_id") clientId: String,
    @Field("client_secret") clientSecret: String,
    @Field("code") code: String
  ): GithubTokenDto //Single<GithubToken>

  @GET("user/repos")
  suspend fun getAllRepos(): List<GithubRepoDto> //Single<List<GithubRepoDto>>

  @GET("/repos/{owner}/{repo}/pulls")
  fun getPullRequests(
    @Path("owner") owner: String,
    @Path("repo") repo: String
  ): Single<List<GithubPullRequestDto>>

  @GET("/repos/{owner}/{repo}/issues/{issue_number}/comments")
  fun getComments(
    @Path("owner") owner: String,
    @Path("repo") repo: String,
    @Path("issue_number") pullNumber: String
  ): Single<List<GithubCommentDto>>

  @POST("/repos/{owner}/{repo}/issues/{issue_number}/comments")
  fun postComment(
    @Path("owner") owner: String,
    @Path("repo") repo: String,
    @Path("issue_number") pullNumber: String,
    @Body comment: GithubCommentDto
  ): Single<ResponseBody>
}