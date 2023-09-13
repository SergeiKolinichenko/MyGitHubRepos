package info.sergeikolinichenko.mygithubrepos.models

import com.google.gson.annotations.SerializedName

/** Created by Sergei Kolinichenko on 08.09.2023 at 17:34 (GMT+3) **/

data class GithubTokenDto(

  @SerializedName("access_token")
  val accessToken: String?,

  @SerializedName("token_type")
  val tokenType: String?

)
