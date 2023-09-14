package info.sergeikolinichenko.mygithubrepos.usecases

import info.sergeikolinichenko.mygithubrepos.repository.Repository
import javax.inject.Inject

/** Created by Sergei Kolinichenko on 14.09.2023 at 17:15 (GMT+3) **/

class GetCommentsUseCase @Inject constructor(
  private val repository: Repository
) {
  suspend operator fun invoke(ownerName: String, repoName: String, numberReq: String) =
    repository.getComments(ownerName, repoName, numberReq)
}