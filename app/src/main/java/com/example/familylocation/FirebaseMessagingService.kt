package com.example.familylocation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseMessagingService : FirebaseMessagingService() {
    
    private val TAG = "FirebaseMessagingService"
    
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        
        // Verificar que el mensaje contenga datos
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
            handleMessageData(remoteMessage.data)
        }
        
        // Verificar que el mensaje contenga una notificación
        remoteMessage.notification?.let { notification ->
            Log.d(TAG, "Message Notification Body: ${notification.body}")
            showNotification(
                title = notification.title ?: "Family Location Tracker",
                message = notification.body ?: "Nueva notificación",
                data = remoteMessage.data
            )
        }
    }
    
    private fun handleMessageData(data: Map<String, String>) {
        try {
            val type = data["type"] ?: "general"
            val title = data["title"] ?: "Family Location Tracker"
            val message = data["message"] ?: "Nueva notificación"
            val userId = data["userId"]
            val familyId = data["familyId"]
            
            when (type) {
                "location_update" -> {
                    val userName = data["userName"] ?: "Miembro familiar"
                    val locationMessage = "$userName compartió su ubicación"
                    showLocationNotification(title, locationMessage, data)
                }
                
                "family_invitation" -> {
                    val inviterName = data["inviterName"] ?: "Un miembro"
                    val invitationMessage = "$inviterName te invitó a unirte a su familia"
                    showFamilyInvitationNotification(title, invitationMessage, data)
                }
                
                "location_alert" -> {
                    val alertMessage = data["alertMessage"] ?: "Alerta de ubicación"
                    showAlertNotification(title, alertMessage, data)
                }
                
                else -> {
                    showNotification(title, message, data)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error handling message data: ${e.message}")
            showNotification("Family Location Tracker", "Nueva notificación", emptyMap())
        }
    }
    
    private fun showNotification(title: String, message: String, data: Map<String, String>) {
        try {
            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                data?.let { putExtra("notification_data", it.toString()) }
            }
            
            val pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            
            // Crear canal de notificación (Android 8.0+)
            createNotificationChannel()
            
            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .build()
            
            notificationManager.notify(getNotificationId(), notification)
        } catch (e: Exception) {
            Log.e(TAG, "Error showing notification: ${e.message}")
        }
    }
    
    private fun showLocationNotification(title: String, message: String, data: Map<String, String>) {
        try {
            val intent = Intent(this, MapActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra("show_location", true)
                data["userId"]?.let { putExtra("userId", it) }
            }
            
            val pendingIntent = PendingIntent.getActivity(
                this,
                1,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            createNotificationChannel()
            
            val notification = NotificationCompat.Builder(this, LOCATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_location)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .build()
            
            notificationManager.notify(getNotificationId(), notification)
        } catch (e: Exception) {
            Log.e(TAG, "Error showing location notification: ${e.message}")
        }
    }
    
    private fun showFamilyInvitationNotification(title: String, message: String, data: Map<String, String>) {
        try {
            val intent = Intent(this, QRActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra("accept_invitation", true)
                data["familyId"]?.let { putExtra("familyId", it) }
                data["inviterName"]?.let { putExtra("inviterName", it) }
            }
            
            val pendingIntent = PendingIntent.getActivity(
                this,
                2,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            createNotificationChannel()
            
            val notification = NotificationCompat.Builder(this, FAMILY_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_family)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .build()
            
            notificationManager.notify(getNotificationId(), notification)
        } catch (e: Exception) {
            Log.e(TAG, "Error showing family invitation notification: ${e.message}")
        }
    }
    
    private fun showAlertNotification(title: String, message: String, data: Map<String, String>) {
        try {
            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra("show_alert", true)
                data["alertType"]?.let { putExtra("alertType", it) }
            }
            
            val pendingIntent = PendingIntent.getActivity(
                this,
                3,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            createNotificationChannel()
            
            val notification = NotificationCompat.Builder(this, ALERT_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_alert)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .build()
            
            notificationManager.notify(getNotificationId(), notification)
        } catch (e: Exception) {
            Log.e(TAG, "Error showing alert notification: ${e.message}")
        }
    }
    
    private fun createNotificationChannel() {
        // Crear canales de notificación solo para Android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            
            // Canal general
            val generalChannel = NotificationChannel(
                CHANNEL_ID,
                "Notificaciones Generales",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notificaciones generales de Family Location Tracker"
            }
            
            // Canal de ubicación
            val locationChannel = NotificationChannel(
                LOCATION_CHANNEL_ID,
                "Notificaciones de Ubicación",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones sobre ubicaciones familiares"
                enableVibration(true)
            }
            
            // Canal de familia
            val familyChannel = NotificationChannel(
                FAMILY_CHANNEL_ID,
                "Notificaciones de Familia",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Invitaciones y notificaciones familiares"
            }
            
            // Canal de alertas
            val alertChannel = NotificationChannel(
                ALERT_CHANNEL_ID,
                "Alertas de Seguridad",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alertas importantes de seguridad"
                enableVibration(true)
                enableLights(true)
            }
            
            notificationManager.createNotificationChannel(generalChannel)
            notificationManager.createNotificationChannel(locationChannel)
            notificationManager.createNotificationChannel(familyChannel)
            notificationManager.createNotificationChannel(alertChannel)
        }
    }
    
    private fun getNotificationId(): Int {
        return System.currentTimeMillis().toInt() % 100000
    }
    
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        
        Log.d(TAG, "Refreshed token: $token")
        
        // Enviar el nuevo token a tu servidor si es necesario
        sendRegistrationToServer(token)
    }
    
    private fun sendRegistrationToServer(token: String?) {
        try {
            // Aquí implementarías el envío del token a tu servidor
            // Por ejemplo, usando Firebase Database o una API propia
            Log.d(TAG, "Token sent to server: $token")
        } catch (e: Exception) {
            Log.e(TAG, "Error sending token to server: ${e.message}")
        }
    }
    
    companion object {
        private const val CHANNEL_ID = "general_notifications"
        private const val LOCATION_CHANNEL_ID = "location_notifications"
        private const val FAMILY_CHANNEL_ID = "family_notifications"
        private const val ALERT_CHANNEL_ID = "alert_notifications"
    }
}
