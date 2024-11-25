package com.example.myapplicationdssdsdsd

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavHostController
import com.google.zxing.BarcodeFormat
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.journeyapps.barcodescanner.DefaultDecoderFactory

class QrScannerFragment(navController: NavHostController) : Fragment() {

    private lateinit var barcodeView: DecoratedBarcodeView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_qr_scanner, container, false)
        barcodeView = view.findViewById(R.id.barcode_scanner_view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initScanner()
    }

    private fun initScanner() {
        barcodeView.barcodeView.decoderFactory = DefaultDecoderFactory(listOf(BarcodeFormat.QR_CODE))
        barcodeView.decodeContinuous(object : BarcodeCallback {
            override fun barcodeResult(result: BarcodeResult?) {
                result?.let {
                    // Toast.makeText(context, "Código QR escaneado: ${it.text}", Toast.LENGTH_LONG).show()
                    // Aquí puedes mostrar el código QR en un frame distinto
                    showQrCodeInFrame(it.text, GlobalVariables.navController)
                }
            }

            override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>?) {
                // Puedes implementar lógica adicional si es necesario
            }
        })
        barcodeView.resume()
    }

    private fun showQrCodeInFrame(qrCode: String, navController: NavHostController) {
        // Implementa la lógica para mostrar el código QR en un frame distinto
        GlobalVariables.qrCode = qrCode
        onDestroyView()
        navController.navigate("QrResultFragment")
    }

    override fun onResume() {
        super.onResume()
        barcodeView.resume()
    }

    override fun onPause() {
        super.onPause()
        barcodeView.pause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        barcodeView.pause()
    }
}
