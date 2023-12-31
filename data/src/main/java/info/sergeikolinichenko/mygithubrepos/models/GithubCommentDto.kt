package info.sergeikolinichenko.mygithubrepos.models

/** Created by Sergei Kolinichenko on 10.09.2023 at 20:48 (GMT+3) **/

data class GithubCommentDto(
  val body: String?,
  val id: String?
) {
  override fun toString() = "$body - $id"
}
