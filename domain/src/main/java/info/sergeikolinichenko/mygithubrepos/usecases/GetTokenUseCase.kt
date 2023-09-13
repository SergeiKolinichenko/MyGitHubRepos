package info.sergeikolinichenko.mygithubrepos.usecases

import info.sergeikolinichenko.mygithubrepos.repository.Repository
import javax.inject.Inject

/** Created by Sergei Kolinichenko on 12.09.2023 at 19:47 (GMT+3) **/

class GetTokenUseCase @Inject constructor(
  private val repository: Repository
) {
  suspend operator fun invoke(uri: String) = repository.getToken(uri)

}