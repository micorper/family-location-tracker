package com.tufamilia.location.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
            Intent.ACTION_PACKAGE_REPLACED -> {
                // Reiniciar servicios después del reinicio del dispositivo
                restartLocationService(context)
            }
        }
    }
    
    private fun restartLocationService(context: Context) {
        val serviceIntent = Intent(context, com.tufamilia.location.service.LocationService::class.java)
        context.startService(serviceIntent)
    }
}