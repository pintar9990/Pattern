import android.app.Activity
import android.content.Intent
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult

class QRCodeScanner(private val activity: Activity) {

    fun startScan() {
        val integrator = IntentIntegrator(activity)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        integrator.setPrompt("Scan a QR code")
        integrator.setCameraId(0) // Use a specific camera of the device
        integrator.setBeepEnabled(true)
        integrator.setBarcodeImageEnabled(true)
        integrator.initiateScan()
    }

    fun handleResult(requestCode: Int, resultCode: Int, data: Intent?): String? {
        val result: IntentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        return if (result.contents != null) {
            result.contents
        } else {
            null
        }
    }
}
