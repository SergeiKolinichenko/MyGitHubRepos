package info.sergeikolinichenko.mygithubrepos.screens.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import info.sergeikolinichenko.mygithubrepos.models.GithubToken
import info.sergeikolinichenko.mygithubrepos.network.ApiFactory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/** Created by Sergei Kolinichenko on 06.09.2023 at 20:21 (GMT+3) **/

class MainViewModel @Inject constructor(

): ViewModel() {

  private val compositeDisposable = CompositeDisposable()

  val tokenId = MutableLiveData<String>()
  val errorId = MutableLiveData<String>()

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
            tokenId.value = t.accessToken
          }

          override fun onError(e: Throwable) {
            e.printStackTrace()
            errorId.value = "Cannot load token. Error: $e"
          }

        })
    )
  }

  override fun onCleared() {
    super.onCleared()
    compositeDisposable.clear()
  }
}