package info.sergeikolinichenko.mygithubrepos.screens.main

import android.content.Intent
import androidx.lifecycle.ViewModel
import info.sergeikolinichenko.mygithubrepos.models.GithubComment
import info.sergeikolinichenko.mygithubrepos.models.GithubRepo
import info.sergeikolinichenko.mygithubrepos.usecases.ClearTokenUseCase
import info.sergeikolinichenko.mygithubrepos.usecases.GetAuthoriseUseCase
import info.sergeikolinichenko.mygithubrepos.usecases.GetCommentsUseCase
import info.sergeikolinichenko.mygithubrepos.usecases.GetPullRequestsUseCase
import info.sergeikolinichenko.mygithubrepos.usecases.GetReposUseCase
import info.sergeikolinichenko.mygithubrepos.usecases.GetTokenUseCase
import info.sergeikolinichenko.mygithubrepos.usecases.PostCommentUseCase
import info.sergeikolinichenko.mygithubrepos.utils.EventMainActivity
import info.sergeikolinichenko.mygithubrepos.utils.EventMainActivity.GetAuthoriseGithub
import info.sergeikolinichenko.mygithubrepos.utils.EventMainActivity.GetComments
import info.sergeikolinichenko.mygithubrepos.utils.EventMainActivity.GetListRepos
import info.sergeikolinichenko.mygithubrepos.utils.EventMainActivity.GetPullRequests
import info.sergeikolinichenko.mygithubrepos.utils.EventMainActivity.GetToken
import info.sergeikolinichenko.mygithubrepos.utils.EventMainActivity.Init
import info.sergeikolinichenko.mygithubrepos.utils.EventMainActivity.PostComment
import info.sergeikolinichenko.mygithubrepos.utils.EventMainActivity.ShowToast
import info.sergeikolinichenko.mygithubrepos.utils.StateMainActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/** Created by Sergei Kolinichenko on 06.09.2023 at 20:21 (GMT+3) **/

class MainViewModel @Inject constructor(
  private val getAuthoriseUseCase: GetAuthoriseUseCase,
  private val getTokenUseCase: GetTokenUseCase,
  private val clearTokenUseCase: ClearTokenUseCase,
  private val getReposUseCase: GetReposUseCase,
  private val getPullRequestsUseCase: GetPullRequestsUseCase,
  private val getCommentsUseCase: GetCommentsUseCase,
  private val postCommentUseCase: PostCommentUseCase
) : ViewModel() {

  private val _state = MutableStateFlow<StateMainActivity>(StateMainActivity.Init)
  val state = _state.asStateFlow()

  suspend fun event(event: EventMainActivity) {
    when (event) {
      Init -> {}
      GetAuthoriseGithub -> getAuthoriseGithub()
      GetListRepos -> loadRepositories()
      is GetToken -> {
        getGithubToken(event.uri)
      }

      is ShowToast -> {
        _state.emit(StateMainActivity.ShowToast(event.message))
      }

      is GetPullRequests -> {
        loadPullRequests(owner = event.owner, repo = event.repo)
      }

      is GetComments -> {
        loadCommentsPullRequest(
          owner = event.owner,
          repo = event.repo,
          pullNumber = event.pullNumber
        )
      }

      is PostComment -> {
        onPostComment(
          repo = event.repo,
          pullNumber = event.pullNumber,
          content = event.content
        )
      }
    }
  }

  private suspend fun getAuthoriseGithub() {
    val intent = getAuthoriseUseCase.invoke<Intent>()
    _state.emit(StateMainActivity.GetAuthoriseGithub(intent = intent))
  }

  private suspend fun getGithubToken(uri: String) {
    val result = getTokenUseCase.invoke(uri = uri)
    _state.emit(StateMainActivity.GotToken(result = result))
  }

  private suspend fun loadRepositories() {
    val list = getReposUseCase.invoke()
    _state.emit(StateMainActivity.GotListRepos(list = list))
  }

  private suspend fun loadPullRequests(owner: String?, repo: String?) {

    if (!owner.isNullOrEmpty() && !repo.isNullOrEmpty()) {

      val list = getPullRequestsUseCase.invoke(owner = owner, repo = repo)
      _state.emit(StateMainActivity.GotListPullRequests(list = list))
    }
  }

  private suspend fun loadCommentsPullRequest(
    owner: String?,
    repo: String?,
    pullNumber: String?
  ) {
    if (!owner.isNullOrEmpty() && !repo.isNullOrEmpty() && !pullNumber.isNullOrEmpty()) {
      val list = getCommentsUseCase.invoke(
        ownerName = owner,
        repoName = repo,
        numberReq = pullNumber
      )
      _state.emit(StateMainActivity.GotListComments(list = list))
    }
  }

  private suspend fun onPostComment(
    repo: GithubRepo,
    pullNumber: String?,
    content: GithubComment
  ) {
    if (
      !repo.name.isNullOrEmpty() &&
      repo.owner.login.isNotEmpty() &&
      !pullNumber.isNullOrEmpty()
    ) {
      val result = postCommentUseCase.invoke(
        repo = repo,
        pullNumber = pullNumber,
        content = content
      )
      if (result != null) {
        _state.emit(StateMainActivity.ShowToast(message = "Comment created"))
        _state.emit(StateMainActivity.PostCommentSucceeds)
      }
    }
  }

  override fun onCleared() {
    super.onCleared()
    clearTokenUseCase.invoke()
  }
}