package com.hightech.foodguard.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hightech.foodguard.analysis.AnalysisResult
import com.hightech.foodguard.analysis.FoodAnalyzer
import com.hightech.foodguard.data.FoodRepository
import com.hightech.foodguard.network.NetworkModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ScanUiState {
    data object Idle : ScanUiState()
    data object Loading : ScanUiState()
    data class Success(val result: AnalysisResult) : ScanUiState()
    data class Error(val message: String) : ScanUiState()
}

class ScanViewModel(private val repository: FoodRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<ScanUiState>(ScanUiState.Idle)
    val uiState: StateFlow<ScanUiState> = _uiState

    private var lastBarcode: String? = null

    fun onBarcodeScanned(barcode: String) {
        if (barcode == lastBarcode) return // evita ri-analisi dello stesso frame
        lastBarcode = barcode
        _uiState.value = ScanUiState.Loading

        viewModelScope.launch {
            try {
                val response = NetworkModule.api.getProduct(barcode)
                val product = response.product
                if (response.status != 1 || product == null) {
                    _uiState.value = ScanUiState.Error("Prodotto non trovato nel database. Codice: $barcode")
                    return@launch
                }
                val forbiddenList = repository.getForbidden()
                val result = FoodAnalyzer.analyze(product, forbiddenList)
                _uiState.value = ScanUiState.Success(result)
            } catch (e: Exception) {
                _uiState.value = ScanUiState.Error("Errore di rete: ${e.message}")
            }
        }
    }

    fun reset() {
        lastBarcode = null
        _uiState.value = ScanUiState.Idle
    }
}
