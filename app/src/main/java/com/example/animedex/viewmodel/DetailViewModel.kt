package com.example.animedex.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.animedex.model.AnimeDto
import com.example.animedex.repository.AnimeRepository
import kotlinx.coroutines.CancellationException
import java.io.IOException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class DetailViewModel(
    private val repository: AnimeRepository = AnimeRepository()
) : ViewModel() {
    private var loadedAnimeId: Int? = null
    private var requestJob: Job? = null

    // StateFlow dipakai agar UI Detail otomatis bereaksi saat data berubah.
    private val _detailState = MutableStateFlow<UiState<AnimeDto>>(UiState.Loading)
    val detailState: StateFlow<UiState<AnimeDto>> = _detailState.asStateFlow()

    fun loadAnimeDetail(animeId: Int, forceRefresh: Boolean = false) {
        if (!forceRefresh && loadedAnimeId == animeId && _detailState.value is UiState.Success) return

        loadedAnimeId = animeId
        requestJob?.cancel()
        requestJob = viewModelScope.launch {
            _detailState.value = UiState.Loading
            try {
                _detailState.value = UiState.Success(repository.getAnimeDetail(animeId))
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _detailState.value = UiState.Error(exception.toUserMessage())
            }
        }
    }

    fun retry() {
        loadedAnimeId?.let { loadAnimeDetail(it, forceRefresh = true) }
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
