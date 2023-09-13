package info.sergeikolinichenko.mygithubrepos.utils

import android.content.Intent
import info.sergeikolinichenko.mygithubrepos.models.GithubPullRequest
import info.sergeikolinichenko.mygithubrepos.models.GithubRepo

/** Created by Sergei Kolinichenko on 12.09.2023 at 17:09 (GMT+3) **/

sealed class StateMainActivity {
  data object Init : StateMainActivity()
  class GotToken(val result: Boolean) : StateMainActivity()
  class GetAuthoriseGithub(val intent: Intent) : StateMainActivity()
  class ShowToast(val message: String) : StateMainActivity()
  class GotListRepos(val list: List<GithubRepo>): StateMainActivity()
  class GotListPullRequests(val list: List<GithubPullRequest>): StateMainActivity()
}
