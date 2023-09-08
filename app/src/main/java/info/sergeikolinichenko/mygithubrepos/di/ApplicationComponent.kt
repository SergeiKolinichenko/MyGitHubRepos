package info.sergeikolinichenko.mygithubrepos.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import info.sergeikolinichenko.mygithubrepos.screens.main.MainActivity

/** Created by Sergei Kolinichenko on 06.09.2023 at 20:29 (GMT+3) **/
@ApplicationScope
@Component(modules = [ViewModelsModule::class])
interface ApplicationComponent {
  fun inject(activity: MainActivity)
  @Component.Factory
  interface Factory{
    fun create(@BindsInstance application: Application): ApplicationComponent
  }

}