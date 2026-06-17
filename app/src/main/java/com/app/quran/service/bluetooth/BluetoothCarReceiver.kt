package com.app.quran.service.bluetooth

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.app.quran.service.audio.QuranPlayerService

class BluetoothCarReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "BluetoothCarReceiver"
        
        private val CAR_KEYWORDS = setOf(
            "car", "vehicle", "automobile", "auto", "cars",
            "toyota", "honda", "bmw", "mercedes", "audi", "ford",
            "chevrolet", "gmc", "nissan", "hyundai", "kia", "lexus",
            "porsche", "volvo", "tesla", "jaguar", "land rover",
            "vw", "volkswagen", "mazda", "subaru", "suzuki",
            "mitsubishi", "jeep", "dodge", "chrysler", "buick",
            "cadillac", "lincoln", "infiniti", "acura", "genesis",
            "carduo", "car multimedia", "car audio", "car kit",
            "carplay", "android auto", "car host", "car mode",
            "handsfree", "parrot", "carkit", "motorola"
        )
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        when (intent.action) {
            BluetoothDevice.ACTION_ACL_CONNECTED -> {
                val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                device?.let {
                    if (isCarDevice(it)) {
                        Log.d(TAG, "Car Bluetooth connected: ${it.name}")
                        startPlayerService(context)
                    }
                }
            }
            BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                Log.d(TAG, "Bluetooth disconnected")
            }
        }
    }

    private fun isCarDevice(device: BluetoothDevice): Boolean {
        val deviceName = device.name?.lowercase() ?: return false
        return CAR_KEYWORDS.any { keyword ->
            deviceName.contains(keyword)
        }
    }

    private fun startPlayerService(context: Context) {
        val intent = Intent(context, QuranPlayerService::class.java)
        intent.action = QuranPlayerService.ACTION_START_PLAYING
        try {
            context.startService(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start player service", e)
        }
    }
}
