package ge.geolab.bookswap.network.cloudMessaging;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GcmListenerService;

import ge.geolab.bookswap.R;
import ge.geolab.bookswap.activities.OffersActivity;

/**
 * Created by dalkh on 28-Jan-16.
 */
public class GcmMessageHandler extends GcmListenerService {
    public static final int MESSAGE_NOTIFICATION_ID = 435345;

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");

        createNotification(from, message);
    }

    // Creates notification based on title and body received
    private void createNotification(String title, String body) {
        Context context = getBaseContext();
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Intent notificationIntent = new Intent(context, OffersActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(body)
                .setSound(defaultSoundUri)
                .setContentIntent(contentIntent)
                .setAutoCancel(true);
        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(MESSAGE_NOTIFICATION_ID, mBuilder.build());
    }

}