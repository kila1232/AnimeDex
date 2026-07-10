package com.example.animedex.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val ANILIST_BASE_URL = "https://graphql.anilist.co/"
    private const val TRANSLATION_BASE_URL = "https://api.mymemory.translated.net/"

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    // Retrofit dibuat satu kali dengan lazy agar hemat resource dan mudah dipakai Repository.
    val apiService: AniListApiService by lazy {
        Retrofit.Builder()
            .baseUrl(ANILIST_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AniListApiService::class.java)
    }

    // Retrofit terpisah untuk translate agar base URL AniList tetap fokus pada GraphQL.
    val translationService: TranslationApiService by lazy {
        Retrofit.Builder()
            .baseUrl(TRANSLATION_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TranslationApiService::class.java)
    }
}
