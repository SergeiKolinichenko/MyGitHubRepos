package info.sergeikolinichenko.mygithubrepos.utils

import info.sergeikolinichenko.mygithubrepos.models.GithubPullRequest
import info.sergeikolinichenko.mygithubrepos.models.GithubPullRequestDto
import info.sergeikolinichenko.mygithubrepos.models.GithubRepo
import info.sergeikolinichenko.mygithubrepos.models.GithubRepoDto
import info.sergeikolinichenko.mygithubrepos.models.GithubUser
import javax.inject.Inject

/** Created by Sergei Kolinichenko on 13.09.2023 at 18:43 (GMT+3) **/

class Mapper @Inject constructor() {
  fun mapDtoToPullRequest(pullRequest: GithubPullRequestDto) = GithubPullRequest(
    id = pullRequest.id,
    title = pullRequest.title,
    number = pullRequest.number,
    commentsUrl = pullRequest.commentsUrl,
    user = pullRequest.user?.let {
      GithubUser(
        id = pullRequest.user.id,
        login = it.login
      )
    }
  )

  fun mapDtoToRepo(dto: GithubRepoDto) = GithubRepo(
    name = dto.name,
    url = dto.url,
    owner = GithubUser(
      id = dto.owner.id,
      login = dto.owner.login
    )
  )
}