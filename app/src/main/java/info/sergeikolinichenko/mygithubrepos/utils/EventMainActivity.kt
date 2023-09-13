package info.sergeikolinichenko.mygithubrepos.utils

/** Created by Sergei Kolinichenko on 12.09.2023 at 17:10 (GMT+3) **/

sealed class EventMainActivity {
  data object Init: EventMainActivity()
  data object GetAuthoriseGithub: EventMainActivity()
  class GetToken(val uri: String): EventMainActivity()
  class ShowToast(val message: String): EventMainActivity()
}
