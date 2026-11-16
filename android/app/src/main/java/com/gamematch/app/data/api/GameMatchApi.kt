package com.gamematch.app.data.api

import com.gamematch.app.data.model.Game
import com.gamematch.app.data.model.PreferencesRequest
import com.gamematch.app.data.model.Profile
import com.gamematch.app.data.model.Recommendation
import com.gamematch.app.data.model.UpdateProfileRequest
import com.gamematch.app.data.model.UpdateRecommendationRequest
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

/** Endpoints da API GameMatch. */
interface GameMatchApi {

    @GET("games/{id}")
    suspend fun getGame(@Path("id") id: Long): Game

    @POST("recommendations")
    suspend fun createRecommendation(@Body preferences: PreferencesRequest): Recommendation

    @GET("recommendations")
    suspend fun getRecommendations(): List<Recommendation>

    @GET("recommendations/{id}")
    suspend fun getRecommendation(@Path("id") id: Long): Recommendation

    @PUT("recommendations/{id}")
    suspend fun updateRecommendation(
        @Path("id") id: Long,
        @Body changes: UpdateRecommendationRequest,
    ): Recommendation

    @DELETE("recommendations/{id}")
    suspend fun deleteRecommendation(@Path("id") id: Long): Response<Unit>

    @GET("profiles/{userName}")
    suspend fun getProfile(@Path("userName") userName: String): Profile

    @PUT("profiles/{userName}")
    suspend fun updateProfile(
        @Path("userName") userName: String,
        @Body changes: UpdateProfileRequest,
    ): Profile

    @Multipart
    @POST("profiles/{userName}/photo")
    suspend fun uploadPhoto(
        @Path("userName") userName: String,
        @Part photo: MultipartBody.Part,
    ): Profile

    @PUT("profiles/{userName}/likes/{gameId}")
    suspend fun likeGame(@Path("userName") userName: String, @Path("gameId") gameId: Long): Profile

    @DELETE("profiles/{userName}/likes/{gameId}")
    suspend fun unlikeGame(@Path("userName") userName: String, @Path("gameId") gameId: Long): Profile
}
