package com.example.animedex.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.animedex.model.AnimeDto
import com.example.animedex.repository.AnimeRepository
import kotlinx.coroutines.CancellationException
import java.io.IOException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class HomeViewModel(
    private val repository: AnimeRepository = AnimeRepository()
) : ViewModel() {
    private var searchJob: Job? = null
    private var requestJob: Job? = null

    // StateFlow menyimpan status Loading, Success, dan Error untuk Home Screen.
    private val _animeState = MutableStateFlow<UiState<List<AnimeDto>>>(UiState.Loading)
    val animeState: StateFlow<UiState<List<AnimeDto>>> = _animeState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        loadAnime()
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500)
            loadAnime(query)
        }
    }

    fun retry() {
        loadAnime(_searchQuery.value)
    }

    private fun loadAnime(query: String = _searchQuery.value) {
        requestJob?.cancel()
        requestJob = viewModelScope.launch {
            _animeState.value = UiState.Loading
            try {
                _animeState.value = UiState.Success(repository.getAnimeList(query))
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _animeState.value = UiState.Error(exception.toUserMessage())
            }
        }
    }

    private fun Exception.toUserMessage(): String = when (this) {
        is IOException -> "Koneksi internet bermasalah. Periksa jaringan lalu coba lagi."
        is HttpException -> when (code()) {
            429 -> "AniList API sedang membatasi request. Tunggu sebentar lalu tekan Retry."
            504 -> "Server AniList sedang sibuk atau timeout. Tunggu sebentar lalu tekan Retry."
            else -> "API gagal merespons. Kode error: ${code()}."
        }
        else -> message ?: "Terjadi kesalahan yang tidak diketahui."
    }
}
