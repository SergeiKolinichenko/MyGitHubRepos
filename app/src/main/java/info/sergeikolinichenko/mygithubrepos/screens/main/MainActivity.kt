package info.sergeikolinichenko.mygithubrepos.screens.main

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import info.sergeikolinichenko.mygithubrepos.databinding.ActivityMainBinding
import info.sergeikolinichenko.mygithubrepos.models.GithubComment
import info.sergeikolinichenko.mygithubrepos.models.GithubPullRequest
import info.sergeikolinichenko.mygithubrepos.models.GithubRepo
import info.sergeikolinichenko.mygithubrepos.utils.App
import info.sergeikolinichenko.mygithubrepos.utils.EventMainActivity
import info.sergeikolinichenko.mygithubrepos.utils.StateMainActivity
import info.sergeikolinichenko.mygithubrepos.utils.StateMainActivity.GetAuthoriseGithub
import info.sergeikolinichenko.mygithubrepos.utils.StateMainActivity.GotListPullRequests
import info.sergeikolinichenko.mygithubrepos.utils.StateMainActivity.GotListRepos
import info.sergeikolinichenko.mygithubrepos.utils.StateMainActivity.GotToken
import info.sergeikolinichenko.mygithubrepos.utils.StateMainActivity.Init
import info.sergeikolinichenko.mygithubrepos.utils.StateMainActivity.ShowToast
import info.sergeikolinichenko.mygithubrepos.utils.ViewModelsFactory
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

  private val binding by lazy {
    ActivityMainBinding.inflate(layoutInflater)
  }

  @Inject
  lateinit var viewModelFactory: ViewModelsFactory
  private val viewModel by lazy {
    ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
  }

  private val component by lazy {
    (application as App).component
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    component.inject(this)
    super.onCreate(savedInstanceState)
    setContentView(binding.root)
    observeStates()
  }

  private fun observeStates() {
    lifecycleScope.launch {
      viewModel.state.collect { state ->
        when (state) {
          Init -> {
            initialReposSpinner()
            initialPrsSpinner()
            initialCommentsSpinner()
          }

          is GetAuthoriseGithub -> {
            startActivity(state.intent)
          }

          is GotToken -> {
            gotToken(result = state.result)
          }

          is ShowToast -> {
            showToast(message = state.message)
          }

          is GotListRepos -> {
            gotListRepos(list = state.list)
          }

          is GotListPullRequests -> {
            gotListPullRequests(list = state.list)
          }

          is StateMainActivity.GotListComments -> {
            gotListComments(list = state.list)
          }

          StateMainActivity.PostCommentSucceeds -> {
            getComments()
          }
        }
      }
    }
  }

  private fun initialReposSpinner() {
    binding.repositoriesSpinner.isEnabled = false
    binding.repositoriesSpinner.adapter = ArrayAdapter(
      this,
      android.R.layout.simple_spinner_dropdown_item,
      arrayListOf("No repositories available")
    )
    binding.repositoriesSpinner.onItemSelectedListener =
      object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(p0: AdapterView<*>?) {
        }

        override fun onItemSelected(
          parent: AdapterView<*>?,
          view: View?,
          position: Int,
          id: Long
        ) {
          if (parent?.selectedItem is GithubRepo) {
            val currentRepo = parent.selectedItem as GithubRepo
            val owner = currentRepo.owner.login
            val repo = currentRepo.name
            // Load PullRequests
            lifecycleScope.launch {
              viewModel.event(
                EventMainActivity.GetPullRequests(
                  owner = owner,
                  repo = repo
                )
              )
            }
          }
        }
      }
  }

  private fun initialPrsSpinner() {
    binding.prsSpinner.isEnabled = false
    binding.prsSpinner.adapter = ArrayAdapter(
      this,
      android.R.layout.simple_spinner_dropdown_item,
      arrayListOf("Please select repository")
    )
    binding.prsSpinner.onItemSelectedListener =
      object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(p0: AdapterView<*>?) {
        }

        override fun onItemSelected(
          parent: AdapterView<*>?,
          view: View?,
          position: Int,
          id: Long
        ) {
          if (parent?.selectedItem is GithubPullRequest) {
            val currentPR = parent.selectedItem as GithubPullRequest
            val owner = currentPR.user?.login
            val repo = binding.repositoriesSpinner.selectedItem as GithubRepo
            val number = currentPR.number
            // Load comments
            lifecycleScope.launch {
              viewModel.event(
                EventMainActivity.GetComments(
                  owner = owner,
                  repo = repo.name,
                  pullNumber = number
                )
              )
            }
          }
        }
      }
  }

  private fun initialCommentsSpinner() {
    binding.commentsSpinner.isEnabled = false
    binding.commentsSpinner.adapter = ArrayAdapter(
      this,
      android.R.layout.simple_spinner_dropdown_item,
      arrayListOf("Please select PR")
    )
  }

  private suspend fun getComments() {
    val currentPR = binding.prsSpinner.selectedItem as GithubPullRequest
    val owner = currentPR.user?.login
    val repo = binding.repositoriesSpinner.selectedItem as GithubRepo
    val number = currentPR.number
    viewModel.event(
      EventMainActivity.GetComments(
        owner = owner,
        repo = repo.name,
        pullNumber = number
      )
    )
  }

  private fun showToast(message: String) {
    Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
  }

  private fun gotListComments(list: List<GithubComment>) {
    if (list.isNotEmpty()) {

      val spinnerAdapter = ArrayAdapter(
        this@MainActivity,
        android.R.layout.simple_spinner_dropdown_item,
        list
      )
      binding.commentsSpinner.adapter = spinnerAdapter
      binding.commentsSpinner.isEnabled = true
      binding.commentEditText.isEnabled = true
      binding.postCommentButton.isEnabled = true

    } else {
      val spinnerAdapter = ArrayAdapter(
        this@MainActivity,
        android.R.layout.simple_spinner_dropdown_item,
        arrayListOf("User has not comments")
      )
      binding.commentsSpinner.adapter = spinnerAdapter
      binding.commentsSpinner.isEnabled = false
    }
  }

  private fun gotListRepos(list: List<GithubRepo>) {
    if (list.isNotEmpty()) {

      val spinnerAdapter = ArrayAdapter(
        this@MainActivity,
        android.R.layout.simple_spinner_dropdown_item,
        list
      )
      binding.repositoriesSpinner.adapter = spinnerAdapter
      binding.repositoriesSpinner.isEnabled = true
    } else {
      val spinnerAdapter = ArrayAdapter(
        this@MainActivity,
        android.R.layout.simple_spinner_dropdown_item,
        arrayListOf("User has not repositories")
      )
      binding.repositoriesSpinner.adapter = spinnerAdapter
      binding.repositoriesSpinner.isEnabled = false
    }
  }

  private fun gotListPullRequests(list: List<GithubPullRequest>) {
    if (list.isNotEmpty()) {

      val spinnerAdapter = ArrayAdapter(
        this@MainActivity,
        android.R.layout.simple_spinner_dropdown_item,
        list
      )
      binding.prsSpinner.adapter = spinnerAdapter
      binding.prsSpinner.isEnabled = true
    } else {
      val spinnerAdapter = ArrayAdapter(
        this@MainActivity,
        android.R.layout.simple_spinner_dropdown_item,
        arrayListOf("User has not pull requests")
      )
      binding.prsSpinner.adapter = spinnerAdapter
      binding.prsSpinner.isEnabled = false
    }
  }

  private suspend fun gotToken(result: Boolean) {
    if (result) {
      binding.loadReposButton.isEnabled = true
      viewModel.event(
        EventMainActivity.ShowToast(
          message = "Authentication successful"
        )
      )
    } else {
      viewModel.event(
        EventMainActivity.ShowToast(
          message = "Authentication failed"
        )
      )
    }
  }

  fun onAuthenticate(view: View) {
    lifecycleScope.launch {
      viewModel.event(EventMainActivity.GetAuthoriseGithub)
    }
  }


  override fun onResume() {
    super.onResume()
    val uri = intent.data
    if (uri != null) {
      lifecycleScope.launch {
        viewModel.event(EventMainActivity.GetToken(uri = uri.toString()))
      }
    }
  }

  fun onLoadRepos(view: View) {
    lifecycleScope.launch {
      viewModel.event(EventMainActivity.GetListRepos)
    }
  }

  fun onPostComment(view: View) {
    val comment = binding.commentEditText.text.toString()
    if (comment.isNotEmpty()) {
      val currentRepo = binding.repositoriesSpinner.selectedItem as GithubRepo
      val currentPullRequest = binding.prsSpinner.selectedItem as GithubPullRequest
      val content = GithubComment(body = comment, id = null)

      lifecycleScope.launch {
        viewModel.event(
          EventMainActivity.PostComment(
            repo = currentRepo,
            pullNumber = currentPullRequest.number,
            content = content
          )
        )
      }
    } else {
      lifecycleScope.launch {
        viewModel.event(EventMainActivity.ShowToast(message = "Pleas enter a comment"))
      }
    }
  }

}