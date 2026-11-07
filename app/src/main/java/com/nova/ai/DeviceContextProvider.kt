package com.nova.ai

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.BatteryManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.util.DisplayMetrics
import java.util.Locale
import java.util.TimeZone

/**
 * Collects a small, non-sensitive device status snapshot.
 * No contacts, messages, photos, files, call logs, location, or installed-app inventory
 * are read. The caller must obtain user consent before sending this snapshot off-device.
 */
object DeviceContextProvider {
    fun collect(context: Context): String {
        val appContext = context.applicationContext
        val batteryIntent = appContext.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val level = batteryIntent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = batteryIntent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        val battery = if (level >= 0 && scale > 0) "${level * 100 / scale}%" else "غير متاح"
        val charging = when (batteryIntent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1)) {
            BatteryManager.BATTERY_STATUS_CHARGING, BatteryManager.BATTERY_STATUS_FULL -> "متصل بالشاحن"
            BatteryManager.BATTERY_STATUS_DISCHARGING -> "مش متصل بالشاحن"
            else -> "غير معروف"
        }

        val stat = StatFs(Environment.getDataDirectory().absolutePath)
        val freeGb = stat.availableBytes / (1024.0 * 1024 * 1024)
        val totalGb = stat.totalBytes / (1024.0 * 1024 * 1024)
        val memory = appContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo().also(memory::getMemoryInfo)
        val ramFreeGb = memoryInfo.availMem / (1024.0 * 1024 * 1024)
        val ramTotalGb = memoryInfo.totalMem / (1024.0 * 1024 * 1024)

        val connectivity = appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val active = connectivity.activeNetwork
        val caps = active?.let(connectivity::getNetworkCapabilities)
        val network = when {
            caps == null -> "غير متصل أو غير معروف"
            caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "Wi-Fi"
            caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "بيانات الهاتف"
            caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "Ethernet"
            else -> "متصل"
        }

        val dm = appContext.resources.displayMetrics
        val density = dm.density
        val widthDp = (dm.widthPixels / density).toInt()
        val heightDp = (dm.heightPixels / density).toInt()

        return buildString {
            appendLine("ملخص معلومات الجهاز (قراءة محلية بموافقة المستخدم):")
            appendLine("- الشركة: ${Build.MANUFACTURER}")
            appendLine("- الطراز: ${Build.MODEL}")
            appendLine("- إصدار أندرويد: ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})")
            appendLine("- مستوى البطارية: $battery — $charging")
            appendLine("- مساحة التخزين: %.1f من %.1f جيجابايت متاحة".format(Locale.US, freeGb, totalGb))
            appendLine("- الذاكرة العشوائية: %.1f من %.1f جيجابايت متاحة".format(Locale.US, ramFreeGb, ramTotalGb))
            appendLine("- الاتصال الحالي: $network")
            appendLine("- أبعاد الشاشة المنطقية: ${widthDp}×${heightDp} dp")
            appendLine("- اللغة: ${Locale.getDefault().toLanguageTag()}")
            appendLine("- المنطقة الزمنية: ${TimeZone.getDefault().id}")
            appendLine("- اسم التطبيق: NOVA AI")
            append("ملاحظة: ده ملخص تقني محدود، مش فحص لكل محتويات الهاتف.")
        }
    }
}
