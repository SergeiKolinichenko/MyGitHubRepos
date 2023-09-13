package info.sergeikolinichenko.mygithubrepos.preferences

import android.app.Application
import android.content.Context
import android.content.SharedPreferences

/** Created by Sergei Kolinichenko on 12.09.2023 at 19:51 (GMT+3) **/

abstract class AppSharedPreferences {

  companion object {
    const val SHARED_PREFS_NAME = "shared_prefs"
    private var INSTANCE: SharedPreferences? = null
    private val LOCK = Any()

    fun getInstance(application: Application): SharedPreferences {
      INSTANCE?.let {
        return it
      }
      synchronized(LOCK) {
        INSTANCE?.let {
          return it
        }
        val sharPref =
          application.applicationContext.getSharedPreferences(
            SHARED_PREFS_NAME, Context.MODE_PRIVATE)
        INSTANCE = sharPref
        return sharPref
      }
    }
  }

}