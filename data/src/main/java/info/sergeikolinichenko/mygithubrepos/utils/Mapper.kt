package info.sergeikolinichenko.mygithubrepos.utils

import info.sergeikolinichenko.mygithubrepos.models.GithubRepo
import info.sergeikolinichenko.mygithubrepos.models.GithubRepoDto
import info.sergeikolinichenko.mygithubrepos.models.GithubUser
import info.sergeikolinichenko.mygithubrepos.models.GithubUserDto
import javax.inject.Inject

/** Created by Sergei Kolinichenko on 13.09.2023 at 18:43 (GMT+3) **/

class Mapper @Inject constructor() {
  fun mapRepoToDto(repo: GithubRepo) = GithubRepoDto(
    name = repo.name,
    url = repo.url,
    owner = GithubUserDto(
      id = repo.owner.id,
      login = repo.owner.login
    )
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