package ch.hearc.todolist.util;

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat


class AlarmReceiver() : BroadcastReceiver() {

    var NOTIFICATION_ID = "todolist"
    var NOTIFICATION = "notification"

    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = ContextCompat.getSystemService(
            context,
            NotificationManager::class.java
        ) as NotificationManager


        notificationManager.sendNotification("Rappel d'une tache",
            context
        )
    }
}

