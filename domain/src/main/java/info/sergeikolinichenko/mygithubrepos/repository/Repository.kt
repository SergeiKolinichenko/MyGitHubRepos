package info.sergeikolinichenko.mygithubrepos.repository

import info.sergeikolinichenko.mygithubrepos.models.GithubComment
import info.sergeikolinichenko.mygithubrepos.models.GithubPullRequest
import info.sergeikolinichenko.mygithubrepos.models.GithubRepo
import okhttp3.ResponseBody

/** Created by Sergei Kolinichenko on 12.09.2023 at 17:18 (GMT+3) **/

interface Repository {
  fun <T> onAuthenticate(): T
  suspend fun getToken(uri: String): Boolean
  fun clearToken()
  suspend fun getGithubRepos(): List<GithubRepo>
  suspend fun getPullRequests(
    owner: String,
    repo: String
  ): List<GithubPullRequest>

  suspend fun getComments(
    ownerName: String,
    repoName: String,
    numberReq: String
  ): List<GithubComment>

  suspend fun postComment(
    repo: GithubRepo,
    pullNumber: String,
    content: GithubComment
  ): ResponseBody?
}