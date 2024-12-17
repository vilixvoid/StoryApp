package com.submission.storyapp.ui.addstory

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import android.Manifest
import android.content.Intent
import android.view.View
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.submission.storyapp.R
import com.submission.storyapp.ViewModelFactory
import com.submission.storyapp.data.Result
import com.submission.storyapp.databinding.ActivityAddStoryBinding
import com.submission.storyapp.getImageUri
import com.submission.storyapp.reduceFileImage
import com.submission.storyapp.ui.main.MainActivity
import com.submission.storyapp.uriToFile
import com.submission.storyapp.viewmodel.AddStoryViewModel

class AddStoryActivity : AppCompatActivity() {

    private var _binding: ActivityAddStoryBinding? = null
    private val binding get() = _binding!!

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, getString(R.string.permissionGranted), Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, getString(R.string.permissionDenied), Toast.LENGTH_LONG).show()
            }
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    private val viewModel by viewModels<AddStoryViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()

        viewModel.currentImageUri.observe(this) { uri ->
            uri?.let {
                binding.previewImageView.setImageURI(it)
            }
        }
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
        supportActionBar?.title = getString(R.string.actionBarAddStory)

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }
    }

    private fun setupAction() {
        binding.buttonGallery.setOnClickListener { startGallery() }
        binding.buttonCamera.setOnClickListener { startCamera() }
        binding.buttonAdd.setOnClickListener { uploadStory() }
    }

    private fun startGallery() {
        launcherGallery.launch(
            PickVisualMediaRequest(
            ActivityResultContracts.PickVisualMedia.ImageOnly
        ))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.setCurrentImageUri(uri)
            showImage()
        } else {
            Toast.makeText(this, "no media selected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startCamera() {
        val imageUri = getImageUri(this)
        viewModel.setCurrentImageUri(imageUri)
        launcherIntentCamera.launch(imageUri)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        } else {
            viewModel.setCurrentImageUri(null)
        }
    }

    private fun uploadStory() {
        viewModel.currentImageUri.value?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            val description = binding.edAddDescription.text.toString()
            viewModel.getSession().observe(this) { sessionUser ->
                if (sessionUser.isLogin) {
                    viewModel.uploadStories(sessionUser.token, imageFile, description).observe(this) { results ->
                        when(results) {
                            is Result.Success -> {
                                binding.progressIndicator.visibility = View.GONE
                                AlertDialog.Builder(this).apply {
                                    setTitle(context.getString(R.string.setTitleSuccess))
                                    setMessage(context.getString(R.string.setMessageSuccessUpload))
                                    setPositiveButton(context.getString(R.string.setPositiveSuccess)) { _, _ ->
                                        val intent = Intent(this@AddStoryActivity, MainActivity::class.java)
                                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        startActivity(intent)
                                        finish()
                                    }
                                    create()
                                    show()
                                }
                            }
                            is Result.Error -> {
                                binding.progressIndicator.visibility = View.GONE
                                AlertDialog.Builder(this).apply {
                                    setTitle(context.getString(R.string.setTitleFailed))
                                    setMessage(context.getString(R.string.setMessageFailedUpload))
                                    setPositiveButton(context.getString(R.string.setPositiveFailed)) { _, _ ->
                                    }
                                    create()
                                    show()
                                }
                            }
                            is Result.Loading -> { binding.progressIndicator.visibility = View.VISIBLE }
                        }
                    }
                }
            }
        }
    }

    private fun showImage() {
        viewModel.currentImageUri.value?.let {
            binding.previewImageView.setImageURI(it)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}