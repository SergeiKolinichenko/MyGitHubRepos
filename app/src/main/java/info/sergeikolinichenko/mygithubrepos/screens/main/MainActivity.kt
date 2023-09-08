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
import info.sergeikolinichenko.mygithubrepos.utils.App
import info.sergeikolinichenko.mygithubrepos.utils.ViewModelsFactory
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

  private val binding by lazy {
    ActivityMainBinding.inflate(layoutInflater)
  }

  private val component by lazy {
    (application as App).component
  }

  @Inject
  lateinit var viewModelFactory: ViewModelsFactory
  private val viewModel by lazy {
    ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
  }

  var token: String? = null

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

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
          // Load PullRequests
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

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
          // Load comments
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

    viewModel.tokenId.observe(this) { token ->
      if (token.isNotEmpty()) {
        this.token = token
        binding.loadReposButton.isEnabled = true
        showToast("Authentication successful")
      } else {
        showToast("Authentication failed")
      }
    }

    viewModel.errorId.observe(this) { message ->
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

    val intent = Intent(Intent.ACTION_VIEW,
      Uri.parse("$oauthUrl?client_id=$clientId&scope=repo&redirect_uri=$callbackUrl"))
    startActivity(intent)
  }

  fun onLoadRepos(view: View) {

  }

  fun onPostComment(view: View) {

  }

}