package com.example.prak9.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prak9.repositori.RepositoriSiswa
import com.example.prak9.room.Siswa
import com.example.prak9.uicontroller.DestinasiDetailSiswa
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class DetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val repositoriSiswa: RepositoriSiswa
) : ViewModel() {

    private val idSiswa: Int = checkNotNull(savedStateHandle[DestinasiDetailSiswa.itemIdArg])

    val uiDetailState: StateFlow<DetailSiswaUiState> =
        repositoriSiswa.getSiswaStream(idSiswa)
            .filterNotNull()
            .map { siswa ->
                DetailSiswaUiState(detailSiswa = siswa.toDetailsSiswa())
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = DetailSiswaUiState()
            )

    suspend fun deleteSiswa() {
        try {
            val siswaToDelete: Siswa = repositoriSiswa.getSiswaStream(idSiswa)
                .filterNotNull()
                .first()
            repositoriSiswa.deleteSiswa(siswaToDelete)
        } catch (e: Exception) {
            Log.e("DetailViewModel", "Gagal menghapus siswa id=$idSiswa", e)
        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class DetailSiswaUiState(
    val detailSiswa: DetailsSiswa = DetailsSiswa()
)

data class DetailsSiswa(
    val id: Int = 0,
    val nama: String = "",
    val alamat: String = "",
    val telpon: String = ""
)

fun Siswa.toDetailsSiswa(): DetailsSiswa = DetailsSiswa(
    id = id,
    nama = nama,
    alamat = alamat,
    telpon = telpon
)

fun DetailsSiswa.toSiswa(): Siswa = Siswa(
    id = id,
    nama = nama,
    alamat = alamat,
    telpon = telpon
)