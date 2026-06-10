package com.app.quran.service.bluetooth

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.app.quran.service.audio.QuranPlayerService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BluetoothCarReceiver : BroadcastReceiver() {

    @Inject
    lateinit var bluetoothHelper: BluetoothHelper

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            BluetoothDevice.ACTION_ACL_CONNECTED -> {
                handleConnection(context, intent)
            }
            BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                handleDisconnection(context, intent)
            }
        }
    }

    private fun handleConnection(context: Context, intent: Intent) {
        val device: BluetoothDevice? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
        }

        device?.let {
            if (bluetoothHelper.isCarDevice(it)) {
                // Save as last car device
                bluetoothHelper.saveLastCarDevice(it)

                // Check if auto-resume is enabled and there's saved position
                val lastPosition = if (bluetoothHelper.isAutoResumeEnabled()) {
                    bluetoothHelper.getLastPlayedPosition()
                } else null

                // Start Quran player with optional resume position
                QuranPlayerService.startWithResume(context, lastPosition?.first, lastPosition?.second)
            }
        }
    }

    private fun handleDisconnection(context: Context, intent: Intent) {
        val device: BluetoothDevice? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
        }

        device?.let {
            if (bluetoothHelper.isCarDevice(it)) {
                // Save current position before pausing
                QuranPlayerService.savePositionAndPause(context)
            }
        }
    }
}