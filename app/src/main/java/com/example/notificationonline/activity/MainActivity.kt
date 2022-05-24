package com.example.notificationonline.activity

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.RemoteInput
import com.example.notificationonline.R
import com.example.notificationonline.broadcast.MyBroadcastReceiver
import com.example.notificationonline.databinding.ActivityMainBinding
import com.example.notificationonline.service.MyService
import java.lang.Exception
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val CHANNEL_ID = "1"
    private lateinit var notificationManager : NotificationManager
    private val notificationId = 1
    private val max = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        initViews()
    }

    private fun initViews() {
        binding.btnNotification.setOnClickListener {
            buildNotification()
        }

        binding.btnClose.setOnClickListener {
            removeNotification()
        }
    }

    private fun removeNotification() {
        notificationManager.cancel(notificationId)
    }

    private fun buildNotification(){

        val pendingIntent : PendingIntent = createPendingIntent()

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_notifications_active_24)
            .setContentTitle("Content Title")
            .setContentText("Content Text")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)// When the notification is onClicked, it is removed by this fun

        //Optional
//        addNotificationLongTextStyle(builder)
//        addNotificationBigPictureStyle(builder)
//        addNotificationInboxStyle(builder)
//        addNotificationProgress(builder)
//        addNotificationDeleteAction(builder)
//        addNotificationSnoozeAction(builder)
        addNotificationReplyAction(builder)
//        addCustomNotification(builder)
//        addSystemWideCategoryNotification(builder)
//        addShowingUrgentMessageNotification(builder)
//        addSettingVisibilityNotification(builder)

        //Required
        createNotificationChannel()
        showNotification(builder)
    }

    private fun addSettingVisibilityNotification(builder: NotificationCompat.Builder) {
        builder.setVisibility(NotificationCompat.VISIBILITY_PRIVATE);
    }

    private fun addShowingUrgentMessageNotification(builder: NotificationCompat.Builder) {
        val fullScreenIntent = Intent(this, NotificationActivity::class.java)
        val fullScreenPendingIntent = PendingIntent.getActivity(this, 0,
            fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        builder.setFullScreenIntent(fullScreenPendingIntent, true)
    }

    private fun addSystemWideCategoryNotification(builder: NotificationCompat.Builder) {
        builder.setCategory(NotificationCompat.CATEGORY_MESSAGE)
    }

    private fun addNotificationReplyAction(builder: NotificationCompat.Builder) {
        val replyIntent = Intent(this, MyService::class.java)
        replyIntent.action = "ACTION_REPLY"
        replyIntent.putExtra("EXTRA_ITEM_ID", notificationId)

        val replyPendingIntent = PendingIntent.getService(this, 0, replyIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        //Remote Input
        val remoteInput = RemoteInput.Builder("EXTRA_TEXT_REPLY")
            .setLabel("Type message")
            .build()
        //Action
        val action = NotificationCompat.Action.Builder(android.R.drawable.ic_menu_send, "Reply", replyPendingIntent)
            .addRemoteInput(remoteInput)
            .build()

        builder.addAction(action)

    }

    private fun addNotificationSnoozeAction(builder: NotificationCompat.Builder) {
        val snoozeIntent = Intent(this, MyBroadcastReceiver::class.java).apply {
            action = "ACTION_SNOOZE"
            putExtra("EXTRA_NOTIFICATION_ID", 0)
        }
        val snoozePendingIntent: PendingIntent =
            PendingIntent.getBroadcast(this, 0, snoozeIntent, 0)

        builder .addAction(R.drawable.ic_baseline_snooze_24, "Snooze", snoozePendingIntent)
    }

    private fun addCustomNotification(builder: NotificationCompat.Builder) {
        val rootPendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val remoteViews = RemoteViews(packageName, R.layout.notification)
        remoteViews.setTextViewText(R.id.textView, "    Message \n Custom notification text")
        remoteViews.setOnClickPendingIntent(R.id.root, rootPendingIntent)
        builder.setContent(remoteViews)
    }

    private fun addNotificationDeleteAction(builder: NotificationCompat.Builder) {
        val deleteIntent = Intent(this, NotificationActivity::class.java)
        val deletePendingIntent = PendingIntent.getActivity(this, 0, deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        builder.addAction(android.R.drawable.ic_delete, "Delete", deletePendingIntent)
    }

    private fun addNotificationInboxStyle(builder: NotificationCompat.Builder) {
            val line1 = "Line1"
            val line2 = "Line2"
            val line3 = "Line3"

        builder.setStyle(NotificationCompat.InboxStyle()
            .addLine(line1)
            .addLine(line2)
            .addLine(line3)
            .setBigContentTitle("Extended title")
            .setSummaryText("+5 more"))
    }

    private fun addNotificationLongTextStyle(builder: NotificationCompat.Builder) {
        val longText = "Notice that the NotificationCompat.Builder constructor requires that you provide a channel ID. This is required for compatibility with Android 8.0 (API level 26) and higher, but is ignored by older versions.By default, the notification's text content is truncated to fit one line. If you want your notification to be longer, you can enable an expandable notification by adding a style template with setStyle(). For example, the following code creates a larger text area:"
        // Long content Text
        builder.setStyle(NotificationCompat.BigTextStyle().bigText(longText))
    }

    private fun addNotificationBigPictureStyle(builder: NotificationCompat.Builder) {
        val options = BitmapFactory.Options()
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.img_noti, options)
        builder.setStyle(NotificationCompat.BigPictureStyle().bigPicture(bitmap))
    }

    private fun addNotificationProgress(builder: NotificationCompat.Builder) {
        builder.setProgress(max, 0, true)
        Thread {
            TimeUnit.MILLISECONDS.sleep(3000)
            var progress = 0

            while (progress <= 100) {
                try {
                    TimeUnit.SECONDS.sleep(1)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                progress += 10
                builder.setProgress(max, progress, false)
                    .setContentText("$progress")
                notificationManager.notify(notificationId, builder.build())
            }

            // show notification without progress
            builder.setProgress(0,10,false)
                .setContentText("Download complete")
            notificationManager.notify(notificationId, builder.build())

        }.start()
    }

    private fun createPendingIntent(): PendingIntent {
        // Create an explicit intent for an Activity in your app
        val intent = Intent(this, NotificationActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun showNotification(builder: NotificationCompat.Builder) {
        with(NotificationManagerCompat.from(this)){
            // notificationId is a unique int for each notification that you must define
            notificationManager.notify(notificationId, builder.build())
        }
    }

    private fun  createNotificationChannel(){
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descText = getString(R.string.channel_desc)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descText
            }
            // Register the channel with the system
            notificationManager.createNotificationChannel(channel)
        }
    }
}
