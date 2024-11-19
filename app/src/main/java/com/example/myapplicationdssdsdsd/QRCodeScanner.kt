import android.app.Activity
import android.content.Intent
import android.widget.Toast
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult

class QRCodeScanner(private val activity: Activity) {

    private var scannedQRCode: String? = null

    fun startScan() {
        val integrator = IntentIntegrator(activity)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        integrator.setPrompt("Scan a QR code")
        integrator.setCameraId(0) // Use a specific camera of the device
        integrator.setBeepEnabled(true)
        integrator.setBarcodeImageEnabled(true)
        integrator.setOrientationLocked(true)
        integrator.initiateScan()
    }

    fun handleResult(requestCode: Int, resultCode: Int, data: Intent?): String? {
        val result: IntentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        return if (result.contents != null) {
            scannedQRCode = result.contents
            // Save the scanned QR code to a variable // Show the scanned QR code using a Toast
            Toast.makeText(activity, "Scanned: ${result.contents}", Toast.LENGTH_LONG).show()
            result.contents
        } else {
            null
        }
    }
}
