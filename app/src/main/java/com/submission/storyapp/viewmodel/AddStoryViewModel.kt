package com.submission.storyapp.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.submission.storyapp.data.StoryRepository
import com.submission.storyapp.data.pref.UserModel
import java.io.File

class AddStoryViewModel(private val repository: StoryRepository): ViewModel() {

    private val _currentImageUri = MutableLiveData<Uri?>()
    val currentImageUri: LiveData<Uri?> get() = _currentImageUri

    fun setCurrentImageUri(uri: Uri?) {
        _currentImageUri.value = uri
    }

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun uploadStories(token: String, imageFile: File, description: String) =
        repository.uploadStories(token, imageFile, description)

}