package com.submission.storyapp.ui.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.submission.storyapp.R
import com.submission.storyapp.ViewModelFactory
import com.submission.storyapp.data.Result
import com.submission.storyapp.databinding.ActivitySignupBinding
import com.submission.storyapp.ui.login.LoginActivity
import com.submission.storyapp.viewmodel.AuthenticationViewModel
import kotlin.math.log

class SignupActivity : AppCompatActivity() {

    private var _binding: ActivitySignupBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthenticationViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        playAnimation()
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
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.signupButton.setOnClickListener { registerAccount() }
    }

    private fun registerAccount() {
        val name = binding.edRegisterName.text.toString()
        val email = binding.edRegisterEmail.text.toString()
        val password = binding.edRegisterPassword.text.toString()

        viewModel.authRegister(name, email, password).observe(this) { register ->
            when(register) {
                is Result.Success -> {
                    binding.progressIndicator.visibility = View.GONE
                    Log.d("Success","Berhasil Register")
                    AlertDialog.Builder(this).apply {
                        setTitle(context.getString(R.string.setTitleSuccess))
                        setMessage(context.getString(R.string.setMessageSuccessSignup))
                        setPositiveButton(context.getString(R.string.setPositiveSuccess)) { _, _ ->
                            val intent = Intent(this@SignupActivity, LoginActivity::class.java)
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
                    Log.d("Error", "Gagal membuat akun !")
                    AlertDialog.Builder(this).apply {
                        setTitle(context.getString(R.string.setTitleFailed))
                        setMessage(context.getString(R.string.setMessageErrorSignup))
                        setPositiveButton(context.getString(R.string.setPositiveFailed)) { _, _ ->
                        }
                        create()
                        show()
                    }
                }
                is Result.Loading -> {
                    binding.progressIndicator.visibility = View.VISIBLE
                }
            }
        }

    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -20f, 20f).apply {
            duration = 5000
            repeatCount = ObjectAnimator.INFINITE
            repeatCount = ObjectAnimator.REVERSE
        }.start()

        val titleRegister = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(300)
        val tvName = ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(300)
        val edRegisterName = ObjectAnimator.ofFloat(binding.edtNameLayout, View.ALPHA, 1f).setDuration(300)
        val tvEmail = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(300)
        val edRegisterEmail = ObjectAnimator.ofFloat(binding.edtEmailLayout, View.ALPHA, 1f).setDuration(300)
        val tvPassword = ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(300)
        val edRegisterPassword = ObjectAnimator.ofFloat(binding.edtPasswordLayout, View.ALPHA, 1f).setDuration(300)
        val btnSignup = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(300)

        AnimatorSet().apply {
            playSequentially(
                titleRegister,
                tvName,
                edRegisterName,
                tvEmail,
                edRegisterEmail,
                tvPassword,
                edRegisterPassword,
                btnSignup
            )
            start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}