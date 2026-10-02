package com.hightech.foodguard.scanner

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

/**
 * Analyzer passato a CameraX: ogni frame viene esaminato da ML Kit,
 * appena trova un codice a barre valido invoca [onBarcodeDetected] e si ferma
 * (per evitare scansioni multiple dello stesso frame).
 */
class BarcodeAnalyzer(
    private val onBarcodeDetected: (String) -> Unit
) : androidx.camera.core.ImageAnalysis.Analyzer {

    private val scanner = BarcodeScanning.getClient()
    @Volatile private var hasDetected = false

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage == null || hasDetected) {
            imageProxy.close()
            return
        }

        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                val value = barcodes.firstOrNull { it.rawValue != null }?.rawValue
                if (value != null && !hasDetected) {
                    hasDetected = true
                    onBarcodeDetected(value)
                }
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }

    fun reset() {
        hasDetected = false
    }
}
