package info.sergeikolinichenko.mygithubrepos.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import info.sergeikolinichenko.mygithubrepos.screens.main.MainViewModel

/** Created by Sergei Kolinichenko on 06.09.2023 at 20:28 (GMT+3) **/
@Module
interface ViewModelsModule {
  @Binds
  @IntoMap
  @ViewModelKey(MainViewModel::class)
  fun bindMainViewModule(viewModel: MainViewModel): ViewModel
}