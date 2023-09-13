package info.sergeikolinichenko.mygithubrepos.repository

/** Created by Sergei Kolinichenko on 12.09.2023 at 17:18 (GMT+3) **/

interface Repository {
  fun <T> onAuthenticate(): T
  suspend fun getToken(uri: String): Boolean
  fun clearToken()
}