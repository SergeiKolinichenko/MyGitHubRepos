package info.sergeikolinichenko.mygithubrepos.screens.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import info.sergeikolinichenko.mygithubrepos.R
import info.sergeikolinichenko.mygithubrepos.databinding.ActivityMainBinding
import info.sergeikolinichenko.mygithubrepos.models.GithubPullRequest
import info.sergeikolinichenko.mygithubrepos.models.GithubRepo
import info.sergeikolinichenko.mygithubrepos.utils.App
import info.sergeikolinichenko.mygithubrepos.utils.ViewModelsFactory
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

    initialReposSpinner()
    initialPrsSpinner()
    initialCommentsSpinner()

    observeViewModel()
  }

  override fun onResume() {
    super.onResume()

    val url = intent.data
    val callbackUrl = getString(R.string.callbackUrl)
    if (url != null && url.toString().startsWith(callbackUrl)) {
      val code = url.getQueryParameter("code")

      code?.let {
        val clientId = getString(R.string.clientId)
        val clientSecret = getString(R.string.clientSecret)
        viewModel.getToken(clientID = clientId, clientSecret = clientSecret, code = code)
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

    viewModel.errorLd.observe(this) { message ->
      showToast(message = message)
    }
  }

  private fun showToast(message: String) {
    Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
  }

  fun onAuthenticate(view: View) {

    val oauthUrl = getString(R.string.oauthUrl)
    val clientId = getString(R.string.clientId)
    val callbackUrl = getString(R.string.callbackUrl)

    val intent = Intent(
      Intent.ACTION_VIEW,
      Uri.parse("$oauthUrl?client_id=$clientId&scope=repo&redirect_uri=$callbackUrl")
    )
    startActivity(intent)
  }

  fun onLoadRepos(view: View) {
    token?.let {
      viewModel.loadRepositories(it)
    }
  }

  fun onPostComment(view: View) {

  }

}