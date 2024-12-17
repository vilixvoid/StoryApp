package com.submission.storyapp.ui.detail

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.submission.storyapp.R
import com.submission.storyapp.viewmodel.MainViewModel
import com.submission.storyapp.ViewModelFactory
import com.submission.storyapp.data.Result
import com.submission.storyapp.data.remote.response.Story
import com.submission.storyapp.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private var _binding: ActivityDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailBinding.inflate(layoutInflater)
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
        supportActionBar?.title = getString(R.string.actionBarDetail)
    }

    private fun setupAction() {
        val storiesId = intent.getStringExtra(EXTRA_STORIES_ID) ?: ""
        if (storiesId.isNotEmpty()) {
            viewModel.getSession().observe(this) { sessionUser ->
                if (sessionUser.token.isNotEmpty()) {
                    viewModel.getDetailStories(sessionUser.token, storiesId).observe(this) { detail ->
                        when(detail) {
                            is Result.Success -> {
                                binding.progressBarDetail.visibility = View.GONE
                                showDetailData(detail.data)
                            }
                            is Result.Error -> {
                                binding.progressBarDetail.visibility = View.GONE
                                showToast("Failed to Fetch Data ${detail.message}")
                            }
                            is Result.Loading -> { binding.progressBarDetail.visibility = View.VISIBLE }
                        }
                    }
                }
            }
        }
    }

    private fun showDetailData(story: Story?){
        Glide.with(this@DetailActivity)
            .load(story?.photoUrl)
            .into(binding.ivDetailPhoto)

        binding.tvDetailName.text = story?.name
        binding.tvDetailDescription.text = story?.description
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val EXTRA_STORIES_ID = "extra_stories_id"
    }
}