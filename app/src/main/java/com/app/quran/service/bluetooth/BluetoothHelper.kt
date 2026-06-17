package com.app.quran.service.bluetooth

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BluetoothHelper @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferencesManager: com.app.quran.data.local.preferences.PreferencesManager
) {
    private val bluetoothManager: BluetoothManager? =
        context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager

    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager?.adapter

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "bluetooth_car_prefs"
        private const val KEY_LAST_CAR_DEVICE = "last_car_device"
        private const val KEY_LAST_PLAYED_SURAH = "last_played_surah"
        private const val KEY_LAST_POSITION = "last_played_position"
        private const val KEY_AUTO_RESUME = "auto_resume_enabled"

        // Extended car keywords (50+)
        val CAR_KEYWORDS = setOf(
            // Generic
            "car", "vehicle", "automobile", "auto", "cars", "motor",

            // Major Brands
            "toyota", "honda", "bmw", "mercedes", "audi", "ford",
            "chevrolet", "gmc", "nissan", "hyundai", "kia", "lexus",
            "porsche", "volvo", "tesla", "jaguar", "land rover",
            "vw", "volkswagen", "mazda", "subaru", "suzuki",
            "mitsubishi", "jeep", "dodge", "chrysler", "buick",
            "cadillac", "lincoln", "infiniti", "acura", "genesis",

            // Car Systems
            "carduo", "car multimedia", "car audio", "car kit",
            "carplay", "android auto", "car host", "car mode",
            "handsfree", "hc-", "parrot", "carkit", "motorola",

            // Additional
            "carl", "carla", "vehicle headunit", "car stereo",
            "car nav", "car gps", "car entertainment", "carlife",
            "my car", "car link", "smartcar"
        )

        // Bluetooth device classes that indicate car systems
        val CAR_DEVICE_CLASSES = setOf(
            0x02_04_00, // Major + Hands-free AG
            0x02_04_04, // Hands-free AG
            0x02_04_08, // Headset
            0x02_04_0C, // Hands-free + Headset
            0x04_00_00, // Major Audio
            0x04_04_00  // Car Audio
        )
    }

    val isBluetoothSupported: Boolean
        get() = bluetoothAdapter != null

    val isBluetoothEnabled: Boolean
        get() = bluetoothAdapter?.isEnabled == true

    fun hasBluetoothPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun isCarDevice(device: BluetoothDevice): Boolean {
        val deviceName = device.name?.lowercase() ?: return false
        val deviceAddress = device.address ?: return false

        // Check custom keywords from preferences
        val customKeywords = preferencesManager.carKeywords
        val allKeywords = CAR_KEYWORDS + customKeywords

        return allKeywords.any { keyword ->
            deviceName.contains(keyword) || deviceName.contains(keyword.replace(" ", ""))
        } || isLastConnectedCarDevice(deviceAddress)
    }

    fun isLastConnectedCarDevice(deviceAddress: String): Boolean {
        return prefs.getString(KEY_LAST_CAR_DEVICE, null) == deviceAddress
    }

    fun saveLastCarDevice(device: BluetoothDevice) {
        prefs.edit()
            .putString(KEY_LAST_CAR_DEVICE, device.address)
            .apply()
    }

    fun saveLastPlayedPosition(surahId: Int, positionMs: Long) {
        prefs.edit()
            .putInt(KEY_LAST_PLAYED_SURAH, surahId)
            .putLong(KEY_LAST_POSITION, positionMs)
            .apply()
    }

    fun getLastPlayedPosition(): Pair<Int, Long>? {
        val surahId = prefs.getInt(KEY_LAST_PLAYED_SURAH, -1)
        val position = prefs.getLong(KEY_LAST_POSITION, -1L)
        return if (surahId != -1 && position != -1L) {
            Pair(surahId, position)
        } else null
    }

    fun isAutoResumeEnabled(): Boolean {
        return prefs.getBoolean(KEY_AUTO_RESUME, true)
    }

    fun setAutoResumeEnabled(enabled: Boolean) {
        prefs.edit()
            .putBoolean(KEY_AUTO_RESUME, enabled)
            .apply()
    }

    fun clearLastCarDevice() {
        prefs.edit()
            .remove(KEY_LAST_CAR_DEVICE)
            .apply()
    }

    fun getPairedDevices(): Set<BluetoothDevice>? {
        if (!hasBluetoothPermissions()) return emptySet()
        return bluetoothAdapter?.bondedDevices
    }

    fun getPairedCarDevices(): Set<BluetoothDevice> {
        return getPairedDevices()?.filter { isCarDevice(it) }?.toSet() ?: emptySet()
    }

    fun getRequiredPermissions(): Array<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else {
            arrayOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
    }

    fun getDeviceInfo(device: BluetoothDevice): CarDeviceInfo {
        return CarDeviceInfo(
            name = device.name ?: "Unknown Device",
            address = device.address,
            isCarDevice = isCarDevice(device),
            lastConnected = isLastConnectedCarDevice(device.address)
        )
    }
}

data class CarDeviceInfo(
    val name: String,
    val address: String,
    val isCarDevice: Boolean,
    val lastConnected: Boolean
)