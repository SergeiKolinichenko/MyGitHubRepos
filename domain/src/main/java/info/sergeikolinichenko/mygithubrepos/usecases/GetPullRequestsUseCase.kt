package info.sergeikolinichenko.mygithubrepos.usecases

import info.sergeikolinichenko.mygithubrepos.repository.Repository
import javax.inject.Inject

/** Created by Sergei Kolinichenko on 10.09.2023 at 17:50 (GMT+3) **/

class GetPullRequestsUseCase @Inject constructor(
  private val repository: Repository
) {
  suspend operator fun invoke(owner: String, repo: String) =
    repository.getPullRequests( owner, repo )
}