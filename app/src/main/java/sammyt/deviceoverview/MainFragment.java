package sammyt.deviceoverview;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.hardware.display.DisplayManagerCompat;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {

    private final String LOG_TAG = this.getClass().getSimpleName();

    private LinearLayout displayInfoContainer;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_main, container, false);

        TextView versionTextView = (TextView) root.findViewById(R.id.android_version_text);
        TextView securityTextView = (TextView) root.findViewById(R.id.security_patch_text);
        TextView brandTextView = (TextView) root.findViewById(R.id.brand_text);
        TextView manuTextView = (TextView) root.findViewById(R.id.manufacturer_text);
        TextView modelTextView = (TextView) root.findViewById(R.id.model_text);
        TextView cpuTextView = (TextView) root.findViewById(R.id.cpu_abi_text);
        TextView cpu2TextView = (TextView) root.findViewById(R.id.cpu_abi2_text);

        displayInfoContainer = (LinearLayout) root.findViewById(R.id.display_info_container);

        TextView sensorsTextView = (TextView) root.findViewById(R.id.sensors_text_view);

        // Get Android version and build overview
        String versionText = "Android " + Build.VERSION.RELEASE + " " +
                getReleaseName(Build.VERSION.SDK_INT);

        versionTextView.setText(versionText);

        if(Build.VERSION.SDK_INT >= 23){
            securityTextView.setText(Build.VERSION.SECURITY_PATCH);
        }

        brandTextView.setText(Build.BRAND);
        manuTextView.setText(Build.MANUFACTURER);
        modelTextView.setText(Build.MODEL);
        cpuTextView.setText(Build.CPU_ABI);
        cpu2TextView.setText(Build.CPU_ABI2);

        // Get sensor info
        SensorManager sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);

        String sensorText = "";
        for(Sensor sensor: sensors){
            sensorText += sensor.getName() + "\n";
        }

        sensorsTextView.setText(sensorText);

        return root;
    }

    private String getDisplayStateStr(int displayState){
        String stateString = "";
        switch(displayState){
            case Display.STATE_OFF:
                stateString = "Off";
                break;
            case Display.STATE_ON:
                stateString = "On";
                break;
            case Display.STATE_DOZE:
                stateString = "Doze";
                break;
            case Display.STATE_DOZE_SUSPEND:
                stateString = "Doze Suspend";
                break;
            case Display.STATE_UNKNOWN:
                stateString = "Unknown";
                break;
        }
        return stateString;
    }

    @Override
    public void onResume(){
        super.onResume();

        NavigationView navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_home);

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));

        // The number of displays may change, so get the info on resume
        getDisplayInfo();
    }

    private void getDisplayInfo(){

        DisplayManagerCompat displayManager = DisplayManagerCompat.getInstance(getContext());
        Display[] displays = displayManager.getDisplays();
        Log.d(LOG_TAG, Arrays.toString(displays));

        displayInfoContainer.removeAllViews();

        for(Display display: displays){

            LinearLayout displayInfoView = (LinearLayout) LayoutInflater.from(getContext())
                    .inflate(R.layout.display_info_layout, displayInfoContainer, false);

            LinearLayout realDimenLinLayout = (LinearLayout) displayInfoView.findViewById(R.id.real_dimension_ll);
            LinearLayout stateLinLayout = (LinearLayout) displayInfoView.findViewById(R.id.state_ll);

            TextView displayIdView = (TextView) displayInfoView.findViewById(R.id.display_id);
            TextView displayDimenView = (TextView) displayInfoView.findViewById(R.id.display_dimensions);
            TextView displayRealDimenView = (TextView) displayInfoView.findViewById(R.id.display_real_dimensions);
            TextView displayDensView = (TextView) displayInfoView.findViewById(R.id.display_density);
            TextView displayRefreshView = (TextView) displayInfoView.findViewById(R.id.display_refresh_rate);
            TextView displayStateView = (TextView) displayInfoView.findViewById(R.id.display_state);

            DisplayMetrics appDisplayMetrics = new DisplayMetrics();
            display.getMetrics(appDisplayMetrics);

            String idText = Integer.toString(display.getDisplayId());
            String dimenText = appDisplayMetrics.widthPixels + "x" + appDisplayMetrics.heightPixels;

            displayIdView.setText(idText);
            displayDimenView.setText(dimenText);

            if(Build.VERSION.SDK_INT >= 17){
                realDimenLinLayout.setVisibility(View.VISIBLE);

                DisplayMetrics realDisplayMetrics = new DisplayMetrics();
                display.getRealMetrics(realDisplayMetrics);

                String realDimenText =  realDisplayMetrics.widthPixels + "x"
                        + realDisplayMetrics.heightPixels;

                displayRealDimenView.setText(realDimenText);
            }

            String densityText = appDisplayMetrics.densityDpi + " dpi";
            String refreshText = display.getRefreshRate() + " fps";

            displayDensView.setText(densityText);
            displayRefreshView.setText(refreshText);

            if(Build.VERSION.SDK_INT >= 20){
                stateLinLayout.setVisibility(View.VISIBLE);

                String stateString = getDisplayStateStr(display.getState());

                displayStateView.setText(stateString);
            }

            displayInfoContainer.addView(displayInfoView);
        }
    }

    private String getReleaseName(int releaseVersion){
        String releaseName;

        switch(releaseVersion){
            case Build.VERSION_CODES.BASE:
                releaseName = "Base";
                break;
            case Build.VERSION_CODES.BASE_1_1:
                releaseName = "Base 1.1";
                break;
            case Build.VERSION_CODES.CUPCAKE:
                releaseName = "Cupcake";
                break;
            case Build.VERSION_CODES.CUR_DEVELOPMENT:
                releaseName = "Cur Development";
                break;
            case Build.VERSION_CODES.DONUT:
                releaseName = "Donut";
                break;
            case Build.VERSION_CODES.ECLAIR:
                releaseName = "Eclair";
                break;
            case Build.VERSION_CODES.ECLAIR_0_1:
                releaseName = "Eclair 0.1";
                break;
            case Build.VERSION_CODES.ECLAIR_MR1:
                releaseName = "Eclair MR1";
                break;
            case Build.VERSION_CODES.FROYO:
                releaseName = "Froyo";
                break;
            case Build.VERSION_CODES.GINGERBREAD:
                releaseName = "Gingerbread";
                break;
            case Build.VERSION_CODES.GINGERBREAD_MR1:
                releaseName = "Gingerbread MR1";
                break;
            case Build.VERSION_CODES.HONEYCOMB:
                releaseName = "Honeycomb";
                break;
            case Build.VERSION_CODES.HONEYCOMB_MR1:
                releaseName = "Honeycomb MR1";
                break;
            case Build.VERSION_CODES.HONEYCOMB_MR2:
                releaseName = "Honeycomb MR2";
                break;
            case Build.VERSION_CODES.ICE_CREAM_SANDWICH:
                releaseName = "Ice Cream Sandwich";
                break;
            case Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1:
                releaseName = "Ice Cream Sandwich MR1";
                break;
            case Build.VERSION_CODES.JELLY_BEAN:
                releaseName = "Jelly Bean";
                break;
            case Build.VERSION_CODES.JELLY_BEAN_MR1:
                releaseName = "Jelly Bean MR1";
                break;
            case Build.VERSION_CODES.JELLY_BEAN_MR2:
                releaseName = "Jelly Bean MR2";
                break;
            case Build.VERSION_CODES.KITKAT:
                releaseName = "KitKat";
                break;
            case Build.VERSION_CODES.KITKAT_WATCH:
                releaseName = "KitKat Watch";
                break;
            case Build.VERSION_CODES.LOLLIPOP:
                releaseName = "Lollipop";
                break;
            case Build.VERSION_CODES.LOLLIPOP_MR1:
                releaseName = "Lollipop MR1";
                break;
            case Build.VERSION_CODES.M:
                releaseName = "Marshmallow";
                break;
            case Build.VERSION_CODES.N:
                releaseName = "Nougat";
                break;
            case Build.VERSION_CODES.N_MR1:
                releaseName = "Nougat MR1";
                break;
            case Build.VERSION_CODES.O:
                releaseName = "Oreo";
                break;
            case Build.VERSION_CODES.O_MR1:
                releaseName = "Oreo MR1";
                break;
            default:
                releaseName = "Unknown Android version";
                break;
        }
        return releaseName;
    }

}
