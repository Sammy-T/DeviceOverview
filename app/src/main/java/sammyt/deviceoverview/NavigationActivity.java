package sammyt.deviceoverview;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class NavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final String LOG_TAG = this.getClass().getSimpleName();

    private static final String MAIN_FRAGMENT = "main_fragment";
    private static final String BATTERY_FRAGMENT = "battery_fragment";
    private static final String AUDIO_VIDEO_FRAGMENT = "audio_video_fragment";
    private static final String SENSOR_FRAGMENT = "sensor_fragment";
    private static final String VIBRATE_FRAGMENT = "vibrate_fragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if(savedInstanceState == null){
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_container, new MainFragment(), MAIN_FRAGMENT)
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        FragmentManager fragmentManager = getSupportFragmentManager();

        switch(id){
            case R.id.nav_home:
                MainFragment mainFragment = (MainFragment) fragmentManager
                        .findFragmentByTag(MAIN_FRAGMENT);

                if(mainFragment != null && mainFragment.isVisible()){
                    break; // Break early
                }else if(mainFragment == null){
                    mainFragment = new MainFragment();
                }

                fragmentManager.beginTransaction()
                        .replace(R.id.content_container, mainFragment, MAIN_FRAGMENT)
                        .addToBackStack(MAIN_FRAGMENT)
                        .commit();
                break;

            case R.id.nav_camera:
                Intent photoIntent = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);

                if(photoIntent.resolveActivity(getPackageManager()) != null){
                    startActivity(photoIntent);
                }
                break;

            case R.id.nav_battery:
                BatteryFragment batteryFragment = (BatteryFragment) fragmentManager
                        .findFragmentByTag(BATTERY_FRAGMENT);

                if(batteryFragment != null && batteryFragment.isVisible()){
                    break; // Break early
                }else if(batteryFragment == null){
                    batteryFragment = new BatteryFragment();
                }

                fragmentManager.beginTransaction()
                        .replace(R.id.content_container, batteryFragment, BATTERY_FRAGMENT)
                        .addToBackStack(BATTERY_FRAGMENT)
                        .commit();
                break;

            case R.id.nav_audio_video:
                AudioFragment avFragment = (AudioFragment) fragmentManager
                        .findFragmentByTag(AUDIO_VIDEO_FRAGMENT);

                if(avFragment != null && avFragment.isVisible()){
                    break; // Break early
                }else if(avFragment == null){
                    avFragment = new AudioFragment();
                }

                fragmentManager.beginTransaction()
                        .replace(R.id.content_container, avFragment, AUDIO_VIDEO_FRAGMENT)
                        .addToBackStack(AUDIO_VIDEO_FRAGMENT)
                        .commit();
                break;

            case R.id.nav_sensor:
                SensorFragment sensorFragment = (SensorFragment) fragmentManager
                        .findFragmentByTag(SENSOR_FRAGMENT);

                if(sensorFragment != null && sensorFragment.isVisible()){
                    break; // Break early
                }else if(sensorFragment == null){
                    sensorFragment = new SensorFragment();
                }

                fragmentManager.beginTransaction()
                        .replace(R.id.content_container, sensorFragment, SENSOR_FRAGMENT)
                        .addToBackStack(SENSOR_FRAGMENT)
                        .commit();
                break;

            case R.id.nav_vibration:
                VibrationFragment vibrateFragment = (VibrationFragment) fragmentManager
                        .findFragmentByTag(VIBRATE_FRAGMENT);

                if(vibrateFragment != null && vibrateFragment.isVisible()){
                    break; // Break early
                }else if(vibrateFragment == null){
                    vibrateFragment = new VibrationFragment();
                }

                fragmentManager.beginTransaction()
                        .replace(R.id.content_container, vibrateFragment, VIBRATE_FRAGMENT)
                        .addToBackStack(VIBRATE_FRAGMENT)
                        .commit();
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
