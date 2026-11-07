package com.nova.ai

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager

data class LaunchableApp(val label: String, val packageName: String)

object DeviceAppManager {
    /**
     * Lists only apps that expose a launcher activity. Android package visibility
     * rules apply; this is not a complete inventory of every installed package.
     */
    @Suppress("DEPRECATION")
    fun getLaunchableApps(context: Context): List<LaunchableApp> {
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        return context.packageManager.queryIntentActivities(intent, 0)
            .mapNotNull { info ->
                val activity = info.activityInfo ?: return@mapNotNull null
                val packageName = activity.packageName ?: return@mapNotNull null
                val label = info.loadLabel(context.packageManager)?.toString()
                    ?.takeIf { it.isNotBlank() } ?: packageName
                LaunchableApp(label, packageName)
            }
            .distinctBy { it.packageName }
            .sortedBy { it.label.lowercase() }
    }

    fun launch(context: Context, packageName: String): Boolean {
        val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName) ?: return false
        return try {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(launchIntent)
            true
        } catch (_: Exception) {
            false
        }
    }
}
