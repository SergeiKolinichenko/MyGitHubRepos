package info.sergeikolinichenko.mygithubrepos.utils

import info.sergeikolinichenko.mygithubrepos.models.GithubComment
import info.sergeikolinichenko.mygithubrepos.models.GithubRepo

/** Created by Sergei Kolinichenko on 12.09.2023 at 17:10 (GMT+3) **/

sealed class EventMainActivity {
  data object Init: EventMainActivity()
  data object GetAuthoriseGithub: EventMainActivity()
  data object GetListRepos: EventMainActivity()
  class GetToken(val uri: String): EventMainActivity()
  class ShowToast(val message: String): EventMainActivity()
  class GetPullRequests(val owner: String?, val repo: String?): EventMainActivity()
  class GetComments(
    val owner: String?,
    val repo: String?,
    val pullNumber: String?
  ): EventMainActivity()

  class PostComment(
    val repo: GithubRepo,
    val pullNumber: String?,
    val content: GithubComment
  ): EventMainActivity()
}
