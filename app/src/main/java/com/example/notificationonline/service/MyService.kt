package com.example.notificationonline.service

import android.app.NotificationManager
import android.app.RemoteInput
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.notificationonline.R

class MyService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //Get reply text
        if(intent?.action == "ACTION_REPLY"){
            //Get reply text
            val resultsFromIntent = RemoteInput.getResultsFromIntent(intent)
            if(resultsFromIntent != null){
                val str = resultsFromIntent.getCharSequence("EXTRA_TEXT_REPLY")
                Toast.makeText(this, "$str", Toast.LENGTH_SHORT).show()
            }

        // Get itemId
            val itemId = intent.getIntExtra("EXTRA_ITEM_ID", 0)
            //Perform operations with replyText and itemId


            //Create new notification
            val repliedNotification = NotificationCompat.Builder(this, "1")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentText("Replied")
                .build()

            //Update notification
            val notificationManager : NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(itemId, repliedNotification)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}