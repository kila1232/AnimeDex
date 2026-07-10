package com.example.animedex.network

import com.example.animedex.model.TranslationResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface TranslationApiService {
    // Network Request untuk menerjemahkan sinopsis dari Bahasa Inggris ke Bahasa Indonesia.
    @GET("get")
    suspend fun translateToIndonesian(
        @Query("q") text: String,
        @Query("langpair") languagePair: String = "en|id"
    ): TranslationResponse
}
