package info.sergeikolinichenko.mygithubrepos.models

import com.google.gson.annotations.SerializedName

/** Created by Sergei Kolinichenko on 10.09.2023 at 19:38 (GMT+3) **/

data class GithubPullRequest(
  val id: String?,
  val title: String?,
  val number: String?,
  val user: GithubUser?,

  @SerializedName("comments_url")
  val commentsUrl: String?

) {
  override fun toString() = "$title - $id"
}
