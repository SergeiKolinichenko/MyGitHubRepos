package info.sergeikolinichenko.mygithubrepos.models

/** Created by Sergei Kolinichenko on 09.09.2023 at 19:53 (GMT+3) **/

data class GithubRepoDto(
  val name: String?,
  val url: String?,
  val owner: GithubUserDto
) {
  override fun toString() = "$name - $url"
}
