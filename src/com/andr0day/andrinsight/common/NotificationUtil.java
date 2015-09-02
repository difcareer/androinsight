package com.andr0day.andrinsight.common;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import com.andr0day.andrinsight.R;

public class NotificationUtil {

    private NotificationManager notificationManager;

    private Notification notification;

    private int notifyId = 7288;

    private static NotificationUtil instance;

    public static NotificationUtil getInstance(Service service) {
        if (instance == null) {
            instance = new NotificationUtil(service);
        }
        return instance;
    }

    private NotificationUtil(Service service) {
        notificationManager = (NotificationManager) service.getSystemService(Context.NOTIFICATION_SERVICE);
        notification = new Notification(R.drawable.icon, "后台运行中", System.currentTimeMillis());
    }

    public void sendNotify(Service service, Intent intent, String title, String content) {
        PendingIntent pendingIntent = PendingIntent.getActivity(service, 0, intent, 0);
        notification.setLatestEventInfo(service, title, content, pendingIntent);
        service.startForeground(notifyId, notification);
        notificationManager.notify(notifyId, notification);
    }


}
