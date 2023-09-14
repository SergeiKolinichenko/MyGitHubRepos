package info.sergeikolinichenko.mygithubrepos.usecases

import info.sergeikolinichenko.mygithubrepos.models.GithubComment
import info.sergeikolinichenko.mygithubrepos.models.GithubRepo
import info.sergeikolinichenko.mygithubrepos.repository.Repository
import javax.inject.Inject

/** Created by Sergei Kolinichenko on 14.09.2023 at 18:00 (GMT+3) **/

class PostCommentUseCase @Inject constructor(
  private val repository: Repository
) {

  suspend operator fun invoke(
    repo: GithubRepo,
    pullNumber: String,
    content: GithubComment
  ) = repository.postComment(repo, pullNumber, content)
}