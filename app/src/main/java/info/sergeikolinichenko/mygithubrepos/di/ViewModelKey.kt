package info.sergeikolinichenko.mygithubrepos.di

import androidx.lifecycle.ViewModel
import dagger.MapKey
import kotlin.reflect.KClass

/** Created by Sergei Kolinichenko on 06.09.2023 at 20:27 (GMT+3) **/
@MapKey
@Retention(AnnotationRetention.RUNTIME)
annotation class ViewModelKey(val value: KClass<out ViewModel>)
