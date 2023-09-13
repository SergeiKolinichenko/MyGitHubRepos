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
import info.sergeikolinichenko.mygithubrepos.utils.StateMainActivity.GetAuthoriseGithub
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

  var token: String? = null

  private val component by lazy {
    (application as App).component
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    component.inject(this)
    super.onCreate(savedInstanceState)
    setContentView(binding.root)

    observeViewModel()
    observeStates()
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
            token?.let {
              // Load PullRequests
              viewModel.loadPullRequests(token = it, owner = owner, repo = repo)
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
            token?.let {
              viewModel.loadCommentsPullRequest(
                token = it,
                owner = owner,
                repo = repo.name,
                number
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

  private fun observeViewModel() {

    viewModel.tokenLd.observe(this) { token ->
      if (token.isNotEmpty()) {
        this.token = token
        binding.loadReposButton.isEnabled = true
        showToast("Authentication successful")
      } else {
        showToast("Authentication failed")
      }
    }

    viewModel.reposLD.observe(this) { reposList ->

      if (!reposList.isNullOrEmpty()) {
        binding.repositoriesSpinner.visibility = View.VISIBLE

        val spinnerAdapter = ArrayAdapter(
          this@MainActivity,
          android.R.layout.simple_spinner_dropdown_item,
          reposList
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

    viewModel.pullRequestsLD.observe(this) { listPullRequests ->

      if (!listPullRequests.isNullOrEmpty()) {
        binding.prsSpinner.visibility = View.VISIBLE

        val spinnerAdapter = ArrayAdapter(
          this@MainActivity,
          android.R.layout.simple_spinner_dropdown_item,
          listPullRequests
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

    viewModel.commentsLD.observe(this) { comments ->
      if (!comments.isNullOrEmpty()) {
        binding.commentsSpinner.visibility = View.VISIBLE
        val spinnerAdapter = ArrayAdapter(
          this@MainActivity,
          android.R.layout.simple_spinner_dropdown_item,
          comments
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

    viewModel.postCommentsLD.observe(this) {
      binding.commentEditText.setText("")
      showToast("Comment created")

      val currentPR = binding.prsSpinner.selectedItem as GithubPullRequest
      val owner = currentPR.user?.login
      val repo = binding.repositoriesSpinner.selectedItem as GithubRepo
      val number = currentPR.number
      // Load comments
      token?.let {
        viewModel.loadCommentsPullRequest(
          token = it,
          owner = owner,
          repo = repo.name,
          number
        )
      }
    }

    viewModel.errorLd.observe(this) { message ->
      showToast(message = message)
    }
  }

  private fun showToast(message: String) {
    Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
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
          is GetAuthoriseGithub -> { startActivity(state.intent) }
          is GotToken -> { gotToken(result = state.result) }
          is ShowToast -> {showToast(message = state.message)}
        }
      }
    }
  }

  private suspend fun gotToken(result: Boolean) {
    if (result) {
      binding.loadReposButton.isEnabled = true
    } else {
      viewModel.event(EventMainActivity.ShowToast(
        message = "Don't load GitHub token"
      ))
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
    token?.let {
      viewModel.loadRepositories(it)
    }
  }

  fun onPostComment(view: View) {
    val comment = binding.commentEditText.text.toString()
    if (comment.isNotEmpty()) {
      val currentRepo = binding.repositoriesSpinner.selectedItem as GithubRepo
      val currentPullRequest = binding.prsSpinner.selectedItem as GithubPullRequest
      val content = GithubComment(body = comment, id = null)
      token?.let {
        viewModel.onPostComment(
          token = it,
          repo = currentRepo,
          pullNumber = currentPullRequest.number,
          content = content
        )
      }
    } else showToast("Pleas enter a comment")
  }

}