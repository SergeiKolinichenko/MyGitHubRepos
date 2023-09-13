package info.sergeikolinichenko.mygithubrepos.usecases

import info.sergeikolinichenko.mygithubrepos.repository.Repository
import javax.inject.Inject

/** Created by Sergei Kolinichenko on 13.09.2023 at 17:34 (GMT+3) **/

class ClearTokenUseCase @Inject constructor(
  private val repository: Repository
) {
  operator fun invoke() {
    repository.clearToken()
  }
}