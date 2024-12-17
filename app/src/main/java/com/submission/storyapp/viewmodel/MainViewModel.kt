package com.submission.storyapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.submission.storyapp.data.StoryRepository
import com.submission.storyapp.data.pref.UserModel
import kotlinx.coroutines.launch

class MainViewModel(private val repository: StoryRepository): ViewModel() {

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun getStories(token: String) = repository.getStories(token)

    fun getDetailStories(token: String,id: String) = repository.getDetailEvent(token, id)

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}