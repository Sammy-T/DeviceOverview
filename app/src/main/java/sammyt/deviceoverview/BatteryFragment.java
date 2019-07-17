package sammyt.deviceoverview;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.hardware.display.DisplayManagerCompat;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dinuscxj.progressbar.CircleProgressBar;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.Arrays;


/**
 * A simple {@link Fragment} subclass.
 */
public class BatteryFragment extends Fragment {

    private final String LOG_TAG = this.getClass().getSimpleName();

    BatteryReceiver batteryReceiver;

    CircleProgressBar batteryLvlProgress;
    TextView statusView;
    TextView pluggedView;
    TextView levelScaleView;
    TextView healthView;
    TextView tempView;
    TextView voltView;
    TextView techView;

    public BatteryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_battery, container, false);

        batteryLvlProgress = (CircleProgressBar) root.findViewById(R.id.battery_level_progress);
        statusView = (TextView) root.findViewById(R.id.battery_status);
        pluggedView = (TextView) root.findViewById(R.id.battery_plugged);
        levelScaleView = (TextView) root.findViewById(R.id.battery_level_scale);
        healthView = (TextView) root.findViewById(R.id.battery_health);
        tempView = (TextView) root.findViewById(R.id.battery_temp);
        voltView = (TextView) root.findViewById(R.id.battery_voltage);
        techView = (TextView) root.findViewById(R.id.battery_tech);


        return root;
    }

    @Override
    public void onResume(){
        super.onResume();

        NavigationView navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_battery);

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.battery));

        batteryReceiver = new BatteryReceiver();

        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = getContext().registerReceiver(batteryReceiver, filter);
    }

    @Override
    public void onPause(){
        Log.d(LOG_TAG, "onPause");
        getContext().unregisterReceiver(batteryReceiver);

        super.onPause();
    }

    public class BatteryReceiver extends BroadcastReceiver{

        private final String LOG_TAG = this.getClass().getSimpleName();

        private int status;
        private int level;
        private int scale;
        private int plugged;
        private int health;
        private int temperature;
        private int voltage;
        private String tech;

        public BatteryReceiver(){}

        @Override
        public void onReceive(Context context, Intent intent){
            status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
            health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);
            temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
            voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
            tech = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);

            String batteryStatus = "status: " + status +" " +getStatus(status) + "\n"
                    + "level: " + level + "\n"
                    + "scale: " + scale  + "\n"
                    + "percent: " + getPercentage(level, scale) + "\n"
                    + "plugged: " + plugged + " " + getPlugged(plugged) + "\n"
                    + "health: " + health + " " + getHealth(health) + "\n"
                    + "temp: " + temperature + "\n"
                    + "volt: " + voltage + "\n"
                    + "tech: " + tech;

//            Log.d(LOG_TAG, batteryStatus);

            updateUI();
        }

        private void updateUI(){
            //// TODO: Animate progress?
            batteryLvlProgress.setProgress((int) getPercentage(level, scale));
            statusView.setText(getStatus(status));

            String levelScaleText = level + "/" + scale;
            levelScaleView.setText(levelScaleText);

            pluggedView.setText(getPlugged(plugged));
            healthView.setText(getHealth(health));
            tempView.setText(getTemp(temperature));
            voltView.setText(getVolts(voltage));
            techView.setText(tech);
        }

        private String getStatus(int status){
            String statusString = "";
            switch(status){
                case BatteryManager.BATTERY_STATUS_CHARGING:
                    statusString = "Charging";
                    break;
                case BatteryManager.BATTERY_STATUS_DISCHARGING:
                    statusString = "Discharging";
                    break;
                case BatteryManager.BATTERY_STATUS_FULL:
                    statusString = "Full";
                    break;
                case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                    statusString = "Not charging";
                    break;
                case BatteryManager.BATTERY_STATUS_UNKNOWN:
                    statusString = "Unknown";
                    break;
            }
            return statusString;
        }

        private float getPercentage(int level, int scale){
            return level / (float)scale * 100;
        }

        private String getPlugged(int plugged){
            String pluggedString = "";
            switch(plugged){
                case 0:
                    pluggedString = "On Battery";
                    break;
                case BatteryManager.BATTERY_PLUGGED_AC:
                    pluggedString = "Plugged AC";
                    break;
                case BatteryManager.BATTERY_PLUGGED_USB:
                    pluggedString = "Plugged USB";
                    break;
                case BatteryManager.BATTERY_PLUGGED_WIRELESS:
                    pluggedString = "Plugged Wireless";
                    break;
            }
            return pluggedString;
        }

        private String getHealth(int health){
            String healthString = "";
            switch(health){
                case BatteryManager.BATTERY_HEALTH_COLD:
                    healthString = "Cold";
                    break;
                case BatteryManager.BATTERY_HEALTH_DEAD:
                    healthString = "Dead";
                    break;
                case BatteryManager.BATTERY_HEALTH_GOOD:
                    healthString = "Good";
                    break;
                case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                    healthString = "Over Voltage";
                    break;
                case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                    healthString = "Overheat";
                    break;
                case BatteryManager.BATTERY_HEALTH_UNKNOWN:
                    healthString = "Unknown";
                    break;
                case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                    healthString = "Unspecified Failure";
                    break;
            }
            return healthString;
        }

        private String getTemp(int temp){
            DecimalFormat df = new DecimalFormat("#.##");
            double celsius = (double)temp / 10;
            double fahren = celsius * 1.8 + 32;
            return df.format(celsius) + "\u00B0C / " + df.format(fahren) + "\u00B0F";
        }

        private String getVolts(int milliVolts){
            DecimalFormat df = new DecimalFormat("#.##");
            double volts = (double)milliVolts / 1000;
            return df.format(volts) + "V";
        }
    }
}
