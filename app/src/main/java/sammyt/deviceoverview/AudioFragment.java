package sammyt.deviceoverview;


import android.Manifest;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Interpolator;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.audiofx.Visualizer;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import ak.sh.ay.musicwave.MusicWave;


/**
 * A simple {@link Fragment} subclass.
 */
public class AudioFragment extends Fragment {

    private final String LOG_TAG = this.getClass().getSimpleName();

    private Boolean mRecordPermissionGranted = false;
    private Boolean mIsRecording = false;
    private Boolean mIsPlaying = false;

    private int duration = 0;
    private SimpleDateFormat formatter = new SimpleDateFormat("mm:ss", Locale.getDefault());

    private String mFileName;
    private MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;
    private Visualizer mVisualizer = null;

    MusicWave musicWave;
    SeekBar seekBar;
    TextView time;
    Button recordButton;
    Button playButton;

    private static final int PERMISSION_REQUEST_RECORD_AUDIO = 61117;

    public AudioFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_audio_video, container, false);

        seekBar = (SeekBar) root.findViewById(R.id.seek_bar);
        time = (TextView) root.findViewById(R.id.media_time);
        recordButton = (Button) root.findViewById(R.id.record_button);
        playButton = (Button) root.findViewById(R.id.play_button);
        Button localMusicButton = (Button) root.findViewById(R.id.local_music);

        // Open a music app on click
        localMusicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PackageManager pm = getContext().getPackageManager();

                Intent musicIntent = new Intent(Intent.ACTION_MAIN);
                musicIntent.addCategory(Intent.CATEGORY_APP_MUSIC);
//                Intent fallbackIntent = new Intent(MediaStore.INTENT_ACTION_MUSIC_PLAYER);

                if(musicIntent.resolveActivity(pm) != null) {
                    Log.d(LOG_TAG, "musicIntent");
                    startActivity(musicIntent);
//                }else if(fallbackIntent.resolveActivity(pm) != null){
//                    Log.d(LOG_TAG, "fallbackIntent");
//                    startActivity(fallbackIntent);
                }else{
                    Log.e(LOG_TAG, "No music player found");
                }
            }
        });

        // Set record file
        mFileName = getContext().getFilesDir().getAbsolutePath();
        mFileName += "/avaudio.wav";
        Log.d(LOG_TAG, mFileName);
        Log.d(LOG_TAG, "File list: " + Arrays.toString(getContext().fileList()));

        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                record();
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                play();
            }
        });

        // Check the record permission
        mRecordPermissionGranted = checkPermission(Manifest.permission.RECORD_AUDIO);
        if(!mRecordPermissionGranted){
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},
                    PERMISSION_REQUEST_RECORD_AUDIO);
        }

        return root;
    }

    @Override
    public void onResume(){
        super.onResume();

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.audio));

        if(checkPermission(Manifest.permission.RECORD_AUDIO)) {
            initializeMusicWave();
        }
    }

    private boolean checkPermission(String permission){
        if(ContextCompat.checkSelfPermission(getContext(), permission) ==
                PackageManager.PERMISSION_GRANTED){
            return true; // Permission granted
        }
        Log.w(LOG_TAG, "Permission not granted: " + permission);
        return false;
    }

    //// TODO: Move permission check
    private void record(){
        if(mRecordPermissionGranted){
            if(!mIsRecording){ // Record
                time.setText("Recording...");
                recordButton.setText("Stop Recording");
                mRecorder = new MediaRecorder();
                mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT); //// TODO: Try other formats
                mRecorder.setOutputFile(mFileName);
                mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT); //// TODO: Try others

                try{
                    mRecorder.prepare();
                }catch(IOException e){
                    Log.e(LOG_TAG, "Prepare failed. IOException: " + e.getMessage());
                }

                mRecorder.start();

            }else{ // Stop Recording
                time.setText("...");
                recordButton.setText("Record");
                mRecorder.stop();
                mRecorder.reset();
                mRecorder.release();
                mRecorder = null;
            }

            mIsRecording = !mIsRecording;
        }else{
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},
                    PERMISSION_REQUEST_RECORD_AUDIO);
        }
    }

    private void play(){
        if(!mIsPlaying){ // Play
            mediaPlay();
        }else{ // Stop playing
            mediaStop();
        }
    }

    private void mediaPlay(){
        if(mRecordPermissionGranted) {

            mPlayer = new MediaPlayer();
            try {
                FileInputStream inputStream = new FileInputStream(mFileName);

                mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mPlayer.setDataSource(inputStream.getFD());
                inputStream.close();
                mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() { //// TODO: Eh
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        duration = mPlayer.getDuration();
                        seekBar.setProgress(0);
                        seekBar.setMax(duration);
                        refreshVisualizer();
                    }
                });
                mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        mediaStop();
                        updateProgress();
                    }
                });
                mPlayer.prepare();
                mPlayer.start();

                playButton.setText("Stop");
                mIsPlaying = !mIsPlaying;

            } catch (IOException e) {
                Log.e(LOG_TAG, "Prepare failed. IOException: " + e.getMessage());

                if(e.getClass().toString().equals("class java.io.FileNotFoundException")) {
                    Toast.makeText(getContext(), "No recorded audio found", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        }else{
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},
                    PERMISSION_REQUEST_RECORD_AUDIO);
        }
    }

    private void mediaStop(){
        mPlayer.release();
        mPlayer = null;

        mVisualizer.setEnabled(false);
        mVisualizer.release();
        mVisualizer = null;

        playButton.setText("Play");

        mIsPlaying = !mIsPlaying;
    }

    private void initializeMusicWave(){
        // getView() only works after onCreateView()
        musicWave = (MusicWave) getView().findViewById(R.id.music_wave);
    }

    //
    private void refreshVisualizer(){
        if(mRecordPermissionGranted) {
            mVisualizer = new Visualizer(mPlayer.getAudioSessionId());
            mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
            mVisualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
                @Override
                public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes, int i) {
                    updateProgress();
                    if (musicWave != null) {
                        musicWave.updateVisualizer(bytes);
                    }
                }

                @Override
                public void onFftDataCapture(Visualizer visualizer, byte[] bytes, int i) {
                }
            }, Visualizer.getMaxCaptureRate() / 2, true, false);

            mVisualizer.setEnabled(true);
        }else{
            Log.e(LOG_TAG, "Can't visualize. Record permission not granted.");
        }
    }

    private void updateProgress(){
        String durationStr;
        String progressStr;
        if(mPlayer != null) {
            durationStr = formatter.format(new Date(duration));
            progressStr = formatter.format(new Date(mPlayer.getCurrentPosition()));

            seekBar.setProgress(mPlayer.getCurrentPosition());
            time.setText(progressStr + "/" + durationStr + "\n"
                    + mPlayer.getCurrentPosition() + "/" + duration);
        }else{
            durationStr = formatter.format(new Date(duration));
            progressStr = formatter.format(new Date(duration));

            seekBar.setProgress(duration);
            time.setText(progressStr + "/" + durationStr + "\n"
                    + duration + "/" + duration);
        }
    }

    // Request Permission Callback
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permission[],
                                           @NonNull int[] grantResults){
        Log.d(LOG_TAG, "permission result: " + requestCode + Arrays.toString(grantResults));

        mRecordPermissionGranted = false;
        switch(requestCode){
            case PERMISSION_REQUEST_RECORD_AUDIO:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mRecordPermissionGranted = true;
                }
                break;
        }

        if(!mRecordPermissionGranted){
            Toast.makeText(getContext(), "Record permission required", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPause(){
        if(mRecorder != null){
            mRecorder.release();
            mRecorder = null;
        }

        if(mPlayer != null){
            mPlayer.release();
            mPlayer = null;
        }
        super.onPause();
    }

}
