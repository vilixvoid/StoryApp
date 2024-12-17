package com.submission.storyapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.submission.storyapp.data.StoryRepository
import com.submission.storyapp.data.pref.UserModel
import kotlinx.coroutines.launch

class AuthenticationViewModel(private val repository: StoryRepository): ViewModel() {

    fun authRegister(name: String, email: String, password: String) =
        repository.authRegister(name, email, password)

    fun authLogin(email: String, password: String) =
        repository.authLogin(email, password)

    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }

}