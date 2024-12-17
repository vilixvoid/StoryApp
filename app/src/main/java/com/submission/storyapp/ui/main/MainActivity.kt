package com.submission.storyapp.ui.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.submission.storyapp.viewmodel.MainViewModel
import com.submission.storyapp.R
import com.submission.storyapp.ViewModelFactory
import com.submission.storyapp.adapter.StoriesAdapter
import com.submission.storyapp.data.Result
import com.submission.storyapp.databinding.ActivityMainBinding
import com.submission.storyapp.ui.addstory.AddStoryActivity
import com.submission.storyapp.ui.detail.DetailActivity
import com.submission.storyapp.ui.welcome.WelcomeActivity

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var storiesAdapter: StoriesAdapter

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.title = getString(R.string.actionBarHome)
    }

    private fun setupAction() {
        viewModel.getSession().observe(this) { sessionUser ->
            if (!sessionUser.isLogin) {
                val intent = Intent(this@MainActivity, WelcomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            } else {
                viewModel.getStories(sessionUser.token).observe(this) { results ->
                    when (results) {
                        is Result.Success -> {
                            binding.progressBar.visibility = View.GONE
                            storiesAdapter.submitList(results.data)
                        }
                        is Result.Error -> {
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(this, results.message, Toast.LENGTH_SHORT).show()
                        }
                        is Result.Loading -> { binding.progressBar.visibility = View.VISIBLE }
                    }
                }
                storiesAdapter = StoriesAdapter { storyItem ->
                    val intent = Intent(this@MainActivity, DetailActivity::class.java)
                    intent.putExtra(DetailActivity.EXTRA_STORIES_ID, storyItem.id.toString())
                    startActivity(intent)
                }
                binding.rvStory.apply {
                    layoutManager = LinearLayoutManager(context)
                    hasFixedSize()
                    adapter = storiesAdapter
                }
            }

            binding.fabAddStory.setOnClickListener {
                val intent = Intent(this, AddStoryActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(Settings.ACTION_LOCALE_SETTINGS)
                startActivity(intent)
                true
            }
            R.id.action_logout -> {
                viewModel.logout()
                Toast.makeText(this, getString(R.string.setMessageLogout), Toast.LENGTH_SHORT).show()
                val intent = Intent(this, WelcomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}