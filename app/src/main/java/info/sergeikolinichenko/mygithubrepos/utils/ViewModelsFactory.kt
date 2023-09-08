package info.sergeikolinichenko.mygithubrepos.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject
import javax.inject.Provider

/** Created by Sergei Kolinichenko on 06.09.2023 at 20:23 (GMT+3) **/

class ViewModelsFactory @Inject constructor(
  private val viewModelProviders:
  @JvmSuppressWildcards Map<Class<out ViewModel>, Provider<ViewModel>>
): ViewModelProvider.Factory {
  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    return viewModelProviders[modelClass]?.get() as T
  }

}