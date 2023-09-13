package info.sergeikolinichenko.mygithubrepos.usecases

import info.sergeikolinichenko.mygithubrepos.repository.Repository
import javax.inject.Inject

/** Created by Sergei Kolinichenko on 10.09.2023 at 17:48 (GMT+3) **/

class GetAuthoriseUseCase @Inject constructor(
  private val repository: Repository
) {
  operator fun <T> invoke() = repository.onAuthenticate<T>()

}