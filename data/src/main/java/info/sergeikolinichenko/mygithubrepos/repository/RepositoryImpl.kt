package info.sergeikolinichenko.mygithubrepos.repository

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import info.sergeikolinichenko.mygithubrepos.network.ApiFactory
import javax.inject.Inject

/** Created by Sergei Kolinichenko on 12.09.2023 at 17:22 (GMT+3) **/

class RepositoryImpl @Inject constructor(
  private val preferences: SharedPreferences
)  : Repository {
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

  companion object {
    private const val OAUTH_URL = "https://github.com/login/oauth/authorize"
    private const val CLIENT_ID = "4b65325780700c2ccd2a"
    private const val CALLBACK_URL = "mygithub://callback"
    private const val CLIENT_SECRET = "6e97c9eeecf73c6fbf7d1f7bcdf7917e8504dd4d"
    private const val KEY_TOKEN = "key_token"
    private const val KEY_CODE = "code"
  }
}