package sammyt.deviceoverview;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.squareup.picasso.Picasso;

import java.io.IOException;

public class NotifyWithPicassoReceiver extends BroadcastReceiver {

    private final String LOG_TAG = this.getClass().getSimpleName();

    private final String CHANNEL_ID = "sammyt.deviceoverview.notification";

    public static final String NOTIFY_TITLE = "NOTIFICATION_TITLE";
    public static final String NOTIFY_MESSAGE = "NOTIFICATION_MESSAGE";
    public static final String NOTIFY_ICON = "NOTIFICATION_ICON";
    public static final String NOTIFY_IMAGE = "NOTIFICATION_IMAGE";

    private NotificationManager mNotifyManager;

    private String notificationTitle;
    private String notificationMessage;
    private String notificationIconUri;
    private String notificationImageUri;
    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent){
        mContext = context;
        notificationTitle = intent.getStringExtra(NOTIFY_TITLE);
        notificationMessage = intent.getStringExtra(NOTIFY_MESSAGE);
        notificationIconUri = null;
        notificationImageUri = null;

        if(intent.hasExtra(NOTIFY_ICON)){
            notificationIconUri = intent.getStringExtra(NOTIFY_ICON);
        }
        if(intent.hasExtra(NOTIFY_IMAGE)){
            notificationImageUri = intent.getStringExtra(NOTIFY_IMAGE);
        }

        // Retrieving Bitmaps from Picasso can't be performed on the main thread
        // Performing all the work on a new thread is a simple solution
        new Thread(new Runnable() {
            @Override
            public void run() {
                scheduledNotify(mContext, notificationTitle, notificationMessage, notificationIconUri,
                        notificationImageUri);
            }
        }).start();
    }

    private void scheduledNotify(Context context, String title, String message, String iconUri,
                                 String imageUri){
        // Get an instance of the Notification Manager
        mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create a Notification Channel for applicable API levels
        createNotificationChannel("Manually Scheduled Notification",
                "Personalized, user-built notification.");

        // Build the notification
        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(context, CHANNEL_ID);
        notifyBuilder.setSmallIcon(R.drawable.bell)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary));

        // Set the icon
        if(iconUri != null){
            try {
                Uri uri = Uri.parse(iconUri);

                // Use picasso to retrieve & scale the bitmap
                Bitmap bitmap = Picasso.get()
                        .load(uri)
                        .resize(1000, 1000)
                        .onlyScaleDown()
                        .centerInside()
                        .get();

                notifyBuilder.setLargeIcon(bitmap);

            }catch(IOException exception){
                Log.e(LOG_TAG, exception.toString(), exception);
            }
        }

        // Set big picture image
        if(imageUri != null){
            try{
                Uri uri = Uri.parse(imageUri);

                // Use picasso to retrieve & scale the bitmap
                Bitmap bigPicture = Picasso.get()
                        .load(uri)
                        .resize(1000, 1000)
                        .onlyScaleDown()
                        .centerInside()
                        .get();

                notifyBuilder.setStyle(new android.support.v4.app.NotificationCompat.BigPictureStyle()
                        .bigPicture(bigPicture)
                        .setSummaryText(message));

            }catch(IOException exception){
                Log.e(LOG_TAG, exception.toString(), exception);
            }
        }

        // Define the notification's action
        Intent actionIntent = new Intent(context, NavigationActivity.class);
        int ACTION_REQUEST = 110;

        PendingIntent actionPendingIntent =
                PendingIntent.getActivity(
                        context,
                        ACTION_REQUEST,
                        actionIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        // Set the notification's click behavior
        notifyBuilder.setContentIntent(actionPendingIntent);

        int notificationId = 113; // Set an ID for the notification

        // Build the notification and issue it
        mNotifyManager.notify(notificationId, notifyBuilder.build());
    }

    private void createNotificationChannel(String channelName, String channelDescription){
        // Create Notification Channel on API 26+
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channelName, importance);
            channel.setDescription(channelDescription);
            channel.enableVibration(true);
            channel.enableLights(true);
            channel.setLightColor(R.color.colorPrimary);

            // Register the channels with the system
            // importance & notification behaviors can't be changed after this
            mNotifyManager.createNotificationChannel(channel);
        }
    }
}
