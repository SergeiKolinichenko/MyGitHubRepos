package info.sergeikolinichenko.mygithubrepos.repository

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import info.sergeikolinichenko.mygithubrepos.models.GithubComment
import info.sergeikolinichenko.mygithubrepos.models.GithubCommentDto
import info.sergeikolinichenko.mygithubrepos.models.GithubPullRequest
import info.sergeikolinichenko.mygithubrepos.models.GithubPullRequestDto
import info.sergeikolinichenko.mygithubrepos.models.GithubRepo
import info.sergeikolinichenko.mygithubrepos.models.GithubRepoDto
import info.sergeikolinichenko.mygithubrepos.network.ApiFactory
import info.sergeikolinichenko.mygithubrepos.utils.Mapper
import okhttp3.ResponseBody
import javax.inject.Inject

/** Created by Sergei Kolinichenko on 12.09.2023 at 17:22 (GMT+3) **/

class RepositoryImpl @Inject constructor(
  private val preferences: SharedPreferences,
  private val mapper: Mapper
) : Repository {


  @Suppress("UNCHECKED_CAST")
  override fun <T> onAuthenticate() = Intent(
    Intent.ACTION_VIEW,
    Uri.parse("$OAUTH_URL?client_id=$CLIENT_ID&scope=repo&redirect_uri=$CALLBACK_URL")
  ) as T

  override suspend fun getToken(uri: String): Boolean {

    if (uri.startsWith(CALLBACK_URL)) {
      val code = Uri.parse(uri).getQueryParameter(KEY_CODE)
      code?.let {
        val token = ApiFactory.getUnauthorizedApi().getAuthToken(
          clientId = CLIENT_ID,
          clientSecret = CLIENT_SECRET,
          code = code
        )
        preferences.edit().putString(KEY_TOKEN, token.accessToken).apply().let {
          return true
        }
      }
    }
    return false
  }

  override fun clearToken() {
    preferences.edit().clear().apply()
  }

  override suspend fun getGithubRepos(): List<GithubRepo> {
    val token = preferences.getString(KEY_TOKEN, "")
    val list = mutableListOf<GithubRepoDto>()
    if (!token.isNullOrEmpty()) {
      list.addAll(
        ApiFactory.getAuthorizedApi(token = token).getAllRepos()
      )
    }
    return list.map { mapper.mapDtoToRepo(it) }
  }

  override suspend fun getPullRequests(
    owner: String,
    repo: String
  ): List<GithubPullRequest> {
    val token = preferences.getString(KEY_TOKEN, "")
    val list = mutableListOf<GithubPullRequestDto>()
    if (!token.isNullOrEmpty()) {
      list.addAll(
        ApiFactory.getAuthorizedApi(token = token)
          .getPullRequests(owner = owner, repo = repo)
      )
    }
    return list.map { mapper.mapDtoToPullRequest(it) }
  }

  override suspend fun getComments(
    ownerName: String,
    repoName: String,
    numberReq: String
  ): List<GithubComment> {
    val token = preferences.getString(KEY_TOKEN, "")
    val list = mutableListOf<GithubCommentDto>()
    if (!token.isNullOrEmpty()) {
      list.addAll(
        ApiFactory.getAuthorizedApi(token = token)
          .getComments(owner = ownerName, repo = repoName, pullNumber = numberReq)
      )
    }
    return list.map { mapper.mapDtoToComment(it) }
  }

  override suspend fun postComment(
    repo: GithubRepo,
    pullNumber: String,
    content: GithubComment
  ): ResponseBody? {
    val token = preferences.getString(KEY_TOKEN, "")
    val commentDto = mapper.mapCommentToDto(content)
    var responseBody: ResponseBody? = null
    var repoName = ""
    repo.name?.let {
      repoName = it
    }
    if (!token.isNullOrEmpty() && repoName.isNotEmpty()) {
      responseBody = ApiFactory.getAuthorizedApi(token = token)
        .postComment(
          repo = repoName,
          owner = repo.owner.login,
          pullNumber = pullNumber,
          comment = commentDto
        )
    }
    return responseBody
  }

  companion object {
    private const val OAUTH_URL = "This is where you need to insert your Oauth Url"
    private const val CLIENT_ID = "This is where you need to insert your GitGub Client ID"
    private const val CALLBACK_URL = "This is where you need to insert your Callback URL"
    private const val CLIENT_SECRET = "This is where you need to insert your Client Secret"
    private const val KEY_TOKEN = "key_token"
    private const val KEY_CODE = "code"
  }
}