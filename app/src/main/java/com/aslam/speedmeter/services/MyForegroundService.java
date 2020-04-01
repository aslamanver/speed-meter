package com.aslam.speedmeter.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.aslam.speedmeter.MainActivity;
import com.aslam.speedmeter.R;

public class MyForegroundService extends Service {

    private static final String TAG = "MyForegroundService";

    private static final int NOTIFICATION_ID = 2999;
    private static final String CHANNEL_ID = "MyForegroundService_ID";
    private static final CharSequence CHANNEL_NAME = "MyForegroundService Channel";
    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        public MyForegroundService getService() {
            return MyForegroundService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate ");
        Toast.makeText(this, "The service is running", Toast.LENGTH_SHORT).show();
        startForeground(NOTIFICATION_ID, createNotification("The service is running"));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        return Service.START_STICKY;
    }

    private Notification createNotification(String message) {

        // Get the layouts to use in the custom notification
        RemoteViews notificationLayout = new RemoteViews(getPackageName(), R.layout.notification_main);
        notificationLayout.setTextViewText(R.id.txtTitle, message);

        NotificationManager mNotificationManager;
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 125, notificationIntent, 0);

        Bitmap payableLogo = BitmapFactory.decodeResource(getResources(), R.drawable.ic_notification);

        mBuilder.setContentTitle("My Service")
                .setContentText(message)
                .setPriority(Notification.PRIORITY_HIGH)
                .setLargeIcon(payableLogo)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentIntent(pendingIntent)
                .setAutoCancel(false)

                .setCustomBigContentView(notificationLayout);

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = CHANNEL_ID;
            NotificationChannel channel = new NotificationChannel(channelId, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            mNotificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId(channelId);
        }

        return mBuilder.build();
    }

    private void showNotification(String message) {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_ID, createNotification(message));
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }
}
