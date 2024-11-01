package com.example.mapster

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class QRScannerViewModel : ViewModel() {
    private val _scannedResult = MutableStateFlow<String?>(null)
    val scannedResult = _scannedResult.asStateFlow()

    fun onQRCodeScanned(result: String) {
        _scannedResult.value = result
    }

    fun clearResult() {
        _scannedResult.value = null
    }

    private val _destination = MutableStateFlow<String?>(null)
    val destination = _destination.asStateFlow()

    fun onLocationSearchResult(result: String) {
        _destination.value = result
    }

    private val _calculatedPath = MutableStateFlow<String?>("")
    val calculatedPath = _calculatedPath.asStateFlow()

    fun onPathCalculation(result: String) {
        _calculatedPath.value = result
    }

}