package info.sergeikolinichenko.mygithubrepos.screens.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import info.sergeikolinichenko.mygithubrepos.models.GithubComment
import info.sergeikolinichenko.mygithubrepos.models.GithubPullRequest
import info.sergeikolinichenko.mygithubrepos.models.GithubRepo
import info.sergeikolinichenko.mygithubrepos.models.GithubToken
import info.sergeikolinichenko.mygithubrepos.network.ApiFactory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import javax.inject.Inject

/** Created by Sergei Kolinichenko on 06.09.2023 at 20:21 (GMT+3) **/

class MainViewModel @Inject constructor() : ViewModel() {

  private val compositeDisposable = CompositeDisposable()

  val tokenLd = MutableLiveData<String>()
  val errorLd = MutableLiveData<String>()
  val reposLD = MutableLiveData<List<GithubRepo>>()
  val pullRequestsLD = MutableLiveData<List<GithubPullRequest>>()
  val commentsLD = MutableLiveData<List<GithubComment>>()
  val postCommentsLD = MutableLiveData<Unit>()

  fun getToken(clientID: String, clientSecret: String, code: String) {
    compositeDisposable.add(
      ApiFactory.getUnauthorizedApi().getAuthToken(
        clientId = clientID,
        clientSecret = clientSecret,
        code = code
      )
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeWith(object : DisposableSingleObserver<GithubToken>() {
          override fun onSuccess(t: GithubToken) {
            tokenLd.value = t.accessToken
          }

          override fun onError(e: Throwable) {
            e.printStackTrace()
            errorLd.value = "Cannot load token. Error: $e"
          }
        })
    )
  }

  fun loadRepositories(token: String) {
    compositeDisposable.add(
      ApiFactory.getAuthorizedApi(token = token).getAllRepos()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeWith(object : DisposableSingleObserver<List<GithubRepo>>() {
          override fun onSuccess(t: List<GithubRepo>) {
            reposLD.value = t
          }

          override fun onError(e: Throwable) {
            e.printStackTrace()
            errorLd.value = "Cannot load repositories"
          }

        })
    )
  }

  fun loadPullRequests(
    token: String,
    owner: String?,
    repo: String?
  ) {

    if (!owner.isNullOrEmpty() && !repo.isNullOrEmpty()) {
      ApiFactory.getAuthorizedApi(token = token)
        .getPullRequests(owner = owner, repo = repo)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeWith(object : DisposableSingleObserver<List<GithubPullRequest>>() {
          override fun onSuccess(t: List<GithubPullRequest>) {
            pullRequestsLD.value = t
          }

          override fun onError(e: Throwable) {
            e.printStackTrace()
            errorLd.value = "Cannot load pull requests"
          }

        })
    }
  }

  fun loadCommentsPullRequest(
    token: String,
    owner: String?,
    repo: String?,
    pullNumber: String?
  ) {

    if (!owner.isNullOrEmpty() && !repo.isNullOrEmpty() && !pullNumber.isNullOrEmpty()) {
      ApiFactory.getAuthorizedApi(token = token)
        .getComments(owner = owner, repo = repo, pullNumber = pullNumber)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeWith(object : DisposableSingleObserver<List<GithubComment>>() {
          override fun onSuccess(t: List<GithubComment>) {
            commentsLD.value = t
          }

          override fun onError(e: Throwable) {
            e.printStackTrace()
            errorLd.value = "Cannot load comments on pull request"
          }

        })
    }
  }

  fun onPostComment(
    token: String,
    repo: GithubRepo,
    pullNumber: String?,
    content: GithubComment
  ) {
    if (
      !repo.name.isNullOrEmpty() &&
      repo.owner.login.isNotEmpty() &&
      !pullNumber.isNullOrEmpty()
    ) {
      compositeDisposable.add(
        ApiFactory.getAuthorizedApi(token).postComment(
          owner = repo.owner.login,
          repo = repo.name!!,
          pullNumber = pullNumber,
          comment = content
        )
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribeWith(object : DisposableSingleObserver<ResponseBody>(){
            override fun onSuccess(t: ResponseBody) {
              postCommentsLD.value = Unit
            }

            override fun onError(e: Throwable) {
              e.printStackTrace()
              errorLd.value = "Cannot create comment"
            }

          })
      )
    }
  }

  override fun onCleared() {
    super.onCleared()
    compositeDisposable.clear()
  }
}