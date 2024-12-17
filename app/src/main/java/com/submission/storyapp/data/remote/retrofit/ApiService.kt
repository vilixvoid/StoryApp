package com.submission.storyapp.data.remote.retrofit

import com.submission.storyapp.data.remote.response.DetailStoryResponse
import com.submission.storyapp.data.remote.response.FileUploadResponse
import com.submission.storyapp.data.remote.response.LoginResponse
import com.submission.storyapp.data.remote.response.RegisterResponse
import com.submission.storyapp.data.remote.response.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") username: String,
        @Field("password") password: String
    ) : RegisterResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ) : LoginResponse

    @Multipart
    @POST("stories")
    suspend fun uploadStories(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ) : FileUploadResponse

    @GET("stories")
    suspend fun getStories(
        @Header("Authorization") token: String,
    ) : StoryResponse

    @GET("stories/{id}")
    suspend fun getDetailStories(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ) : DetailStoryResponse
}