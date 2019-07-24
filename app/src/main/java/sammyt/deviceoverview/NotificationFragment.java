package sammyt.deviceoverview;


import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment {

    private final String LOG_TAG = this.getClass().getSimpleName();

    private EditText mTitleView;
    private EditText mMessageView;
    private ImageButton mIconButton;
    private ImageButton mImageButton;
    private Spinner mHourSelect;
    private Spinner mMinSelect;

    private int[] mHourVals;
    private int[] mMinuteVals;

    private String mIconUri;
    private String mImageUri;

    private static final int REQUEST_ICON = 919;
    private static final int REQUEST_IMAGE = 920;

    private static final int REQUEST_ALARM = 719;

    public NotificationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_notification, container, false);

        mTitleView = root.findViewById(R.id.notify_title);
        mMessageView = root.findViewById(R.id.notify_message);
        mIconButton = root.findViewById(R.id.notify_add_icon);
        mImageButton = root.findViewById(R.id.notify_add_image);
        mHourSelect = root.findViewById(R.id.notify_hour_spinner);
        mMinSelect = root.findViewById(R.id.notify_min_spinner);
        Button scheduleButton = root.findViewById(R.id.notify_schedule_button);
        Button clearButton = root.findViewById(R.id.notify_clear_button);

        mHourVals = getResources().getIntArray(R.array.hour_values);
        mMinuteVals = getResources().getIntArray(R.array.minute_values);

        mIconButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPhotoUri(REQUEST_ICON);
            }
        });

        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPhotoUri(REQUEST_IMAGE);
            }
        });

        scheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = mTitleView.getText().toString();
                String message = mMessageView.getText().toString();

                // Use the selected spinner item to get the corresponding value
                int hours = mHourVals[mHourSelect.getSelectedItemPosition()];
                int minutes = mMinuteVals[mMinSelect.getSelectedItemPosition()];

                int delay = getNotificationDelay(hours, minutes);

                scheduleNotification(title, message, delay);
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFields();
            }
        });

        clearButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                clearImages();
                return true;
            }
        });

        return root;
    }

    @Override
    public void onResume(){
        super.onResume();

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.notification);
    }

    private void requestPhotoUri(int requestCode){
        Intent photoIntent = new Intent(Intent.ACTION_GET_CONTENT);
        photoIntent.setType("image/*");
        if(photoIntent.resolveActivity(getActivity().getPackageManager()) != null){
            startActivityForResult(photoIntent, requestCode);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == REQUEST_ICON && resultCode == Activity.RESULT_OK){
            mIconUri = data.getDataString();
            setImageButton(mIconUri, mIconButton);

        }else if(requestCode == REQUEST_IMAGE && resultCode == Activity.RESULT_OK){
            mImageUri = data.getDataString();
            setImageButton(mImageUri, mImageButton);
        }
    }

    // Sets the image in the Image Button resized to the view's dimensions
    private void setImageButton(final String imageUri, ImageButton imageButton){
        int width = imageButton.getWidth();
        int height = imageButton.getHeight();

        Log.d(LOG_TAG, "width " + width + " height " + height);

        Picasso.get()
                .load(imageUri)
                .resize(width, height)
                .onlyScaleDown()
                .centerInside()
                .error(android.R.drawable.stat_notify_error)
                .into(imageButton, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d(LOG_TAG, "Picasso successfully loaded " + imageUri);
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e(LOG_TAG, "Picasso error " + imageUri, e);
                    }
                });
    }

    // Resets all fields to empty and images back to the default
    private void clearFields(){
        mTitleView.setText("");
        mMessageView.setText("");

        mHourSelect.setSelection(0);
        mMinSelect.setSelection(0);

        clearImages();
    }

    private void clearImages(){
        mIconUri = null;
        mImageUri = null;

        mIconButton.setImageResource(R.drawable.plus_circle_outline);
        mImageButton.setImageResource(R.drawable.plus_circle_outline);
    }

    private void scheduleNotification(String title, String message, int delay){
        // Build the Notification intent
        Intent notifyIntent = new Intent(getContext(), ScheduledNotifyReceiver.class);
        notifyIntent.putExtra(ScheduledNotifyReceiver.NOTIFY_TITLE, title);
        notifyIntent.putExtra(ScheduledNotifyReceiver.NOTIFY_MESSAGE, message);

        // Add the images if they're available
        if(mIconUri != null){
            notifyIntent.putExtra(ScheduledNotifyReceiver.NOTIFY_ICON, mIconUri);
        }
        if(mImageUri != null){
            notifyIntent.putExtra(ScheduledNotifyReceiver.NOTIFY_IMAGE, mImageUri);
        }

        // Set up the alarm to trigger the notification
        PendingIntent alarmIntent = PendingIntent.getBroadcast(getContext(), REQUEST_ALARM,
                notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        long futureInMillis = SystemClock.elapsedRealtime() + delay;

        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, alarmIntent);

        Toast.makeText(getContext(), "Notification Scheduled", Toast.LENGTH_SHORT).show();
    }

    // Calculates the delay in milliseconds
    private int getNotificationDelay(int hrs, int mins){
        int selectedDelay;

        int hours = (60 * 1000) * 60 * hrs;
        int minutes = (60 * 1000) * mins;
        selectedDelay = hours + minutes;

        return selectedDelay;
    }
}
