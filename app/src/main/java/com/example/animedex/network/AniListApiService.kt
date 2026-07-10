package com.example.animedex.network

import com.example.animedex.model.AniListDetailResponse
import com.example.animedex.model.AniListGraphQlRequest
import com.example.animedex.model.AniListListResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AniListApiService {
    // Network Request ke AniList GraphQL untuk mengambil daftar anime.
    @POST(".")
    suspend fun getAnimeList(
        @Body request: AniListGraphQlRequest
    ): AniListListResponse

    // Network Request detail anime berdasarkan ID AniList yang dikirim lewat Navigation.
    @POST(".")
    suspend fun getAnimeDetail(
        @Body request: AniListGraphQlRequest
    ): AniListDetailResponse
}
