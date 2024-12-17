package com.submission.storyapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.submission.storyapp.data.pref.UserModel
import com.submission.storyapp.data.pref.UserPreference
import com.submission.storyapp.data.remote.response.ListStoryItem
import com.submission.storyapp.data.remote.retrofit.ApiService
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class StoryRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference,
    ){

    fun authRegister(name: String, email: String, password: String) = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.register(name, email, password)
            emit(Result.Success(response))
        } catch (exc: Exception) {
            emit(Result.Error("${exc.message}"))
        }
    }

    fun authLogin(email: String, password: String) = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.login(email, password)
            emit(Result.Success(response))
        } catch (exc: Exception) {
            emit(Result.Error("${exc.message}"))
        }
    }

    fun uploadStories(token: String, imageFile: File, description: String) = liveData {
        emit(Result.Loading)
        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )
        try {
            val response = apiService.uploadStories(token, multipartBody, requestBody)
            emit(Result.Success(response))
        } catch (exc: Exception) {
            emit(Result.Error("${exc.message}"))
        }
    }

    fun getStories(token: String) : LiveData<Result<List<ListStoryItem>>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getStories(token)
            emit(Result.Success(response.listStory))
        } catch (exc: Exception) {
            emit(Result.Error("${exc.message}"))
        }
    }

    fun getDetailEvent(token: String, id: String) = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getDetailStories(token,id)
            emit(Result.Success(response.story))
        } catch (exc: Exception) {
            emit(Result.Error("${exc.message}"))
        }
    }

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            apiService: ApiService,
            userPreference: UserPreference
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService, userPreference)
            }.also { instance = it }
    }
}