package info.sergeikolinichenko.mygithubrepos.utils

import android.app.Application
import info.sergeikolinichenko.mygithubrepos.di.DaggerApplicationComponent

/** Created by Sergei Kolinichenko on 06.09.2023 at 20:26 (GMT+3) **/

class App: Application() {

  val component by lazy {
    DaggerApplicationComponent.factory().create(this)
  }

}