package sammyt.deviceoverview;


import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.BubbleChartData;
import lecho.lib.hellocharts.model.BubbleValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.BubbleChartView;
import sammyt.deviceoverview.hellocharts.extension.ModBubbleChartView;


/**
 * A simple {@link Fragment} subclass.
 */
public class SensorFragment extends Fragment implements SensorEventListener{

    private final String LOG_TAG = this.getClass().getSimpleName();

    private ModBubbleChartView mSensorChartView;
    private TextView mSensorValuesText;

    private SensorManager mSensorManager;

    private ArrayList<Sensor> mMultipleSensorList;
    private Sensor mSelectedSensor;

    private SingleSensorAdapter mSingleAdapter;
    private ArrayList<Sensor> mSingleSensorList;

    private float[] mSensorVals;
    private float mMaxZ = 10;

    public SensorFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_sensor, container, false);
        mSensorChartView = root.findViewById(R.id.chart);
        mSensorValuesText = root.findViewById(R.id.sensor_values_text);
        Spinner sensorMultSpinner = root.findViewById(R.id.sensor_spinner);
        RecyclerView sensorGridView = root.findViewById(R.id.sensor_grid);

        // Set fixed viewport dimensions
        setViewPort(20);

        mSensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);

        // Build the array lists with multiple value sensors
        mMultipleSensorList = new ArrayList<>();
        ArrayList<String> multSensorNamesList = new ArrayList<>();

        for(Sensor multSensor: sensors){
            boolean addMultToList = false;

            switch(multSensor.getType()){
                case Sensor.TYPE_ACCELEROMETER:
                    addMultToList = true;
                    break;
                case Sensor.TYPE_GRAVITY:
                    addMultToList = true;
                    break;
                case Sensor.TYPE_GYROSCOPE:
                    addMultToList = true;
                    break;
                case Sensor.TYPE_LINEAR_ACCELERATION:
                    addMultToList = true;
                    break;
                case Sensor.TYPE_ROTATION_VECTOR: // 4 value type
                    addMultToList = false; //// TODO: Do I want to add this one?
                    break;
                case Sensor.TYPE_GAME_ROTATION_VECTOR:
                    addMultToList = true;
                    break;
                case Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR:
                    addMultToList = true;
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    addMultToList = true;
                    break;
            }

            if(addMultToList){
                mMultipleSensorList.add(multSensor);
                multSensorNamesList.add(multSensor.getName());
            }
        }

        // Set up the sensor spinner
        ArrayAdapter sensorMultAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, multSensorNamesList);
        sensorMultSpinner.setAdapter(sensorMultAdapter);

        sensorMultSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(LOG_TAG, "item selected " + mMultipleSensorList.get(position).getName());

                unregisterSensor(mSelectedSensor); // Unregister previously selected sensor

                int sensorType = mMultipleSensorList.get(position).getType();
                mSelectedSensor = mSensorManager.getDefaultSensor(sensorType);

                registerSensor(mSelectedSensor); // Register currently selected sensor

                // Set Z value and Viewport size to accommodate the values returned from the selected sensor
                if(mSelectedSensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
                    setViewPort(100);
                    mMaxZ = 100;
                }else{
                    setViewPort(20);
                    mMaxZ = 20;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d(LOG_TAG, "nothing selected");
            }
        });

        // Set the initial sensor
        mSelectedSensor = mSensorManager.getDefaultSensor(mMultipleSensorList.get(0).getType());

        // Build the array list with the single value sensors
        mSingleSensorList = new ArrayList<>();

        for(Sensor sensor: sensors){
            boolean addToList = false;

            switch(sensor.getType()){
                case Sensor.TYPE_STEP_COUNTER:
                    addToList = true;
                    break;
                case Sensor.TYPE_PROXIMITY:
                    addToList = true;
                    break;
                case Sensor.TYPE_AMBIENT_TEMPERATURE:
                    addToList = true;
                    break;
                case Sensor.TYPE_LIGHT:
                    addToList = true;
                    break;
                case Sensor.TYPE_PRESSURE:
                    addToList = true;
                    break;
                case Sensor.TYPE_RELATIVE_HUMIDITY:
                    addToList = true;
                    break;
            }

            if(addToList){
                mSingleSensorList.add(sensor);
            }
        }

        // Create a grid layout with 2 columns
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        sensorGridView.setLayoutManager(layoutManager);

        mSingleAdapter = new SingleSensorAdapter(mSingleSensorList);
        sensorGridView.setAdapter(mSingleAdapter);

        return root;
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        int sensorType = event.sensor.getType();

        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);

        switch(sensorType){
            case Sensor.TYPE_STEP_COUNTER:
                mSingleAdapter.updateItem(event.sensor, event.values[0]);
                break;
            case Sensor.TYPE_PROXIMITY:
                mSingleAdapter.updateItem(event.sensor, event.values[0]);
                break;
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                mSingleAdapter.updateItem(event.sensor, event.values[0]);
                break;
            case Sensor.TYPE_LIGHT:
                mSingleAdapter.updateItem(event.sensor, event.values[0]);
                break;
            case Sensor.TYPE_PRESSURE:
                mSingleAdapter.updateItem(event.sensor, event.values[0]);
                break;
            case Sensor.TYPE_RELATIVE_HUMIDITY:
                mSingleAdapter.updateItem(event.sensor, event.values[0]);
                break;
                default:
                    if(event.values.length == 3){ //// TODO: Switch this to >= for the 4 value sensor?
                        if(mSensorVals != null && Arrays.equals(mSensorVals, event.values)){
                            break; // Break early if it's a duplicate value
                        }
                        mSensorVals = new float[]{event.values[0], event.values[1], event.values[2]};

                        // Set the text value of the sensor data
                        StringBuilder sensorValues = new StringBuilder();
                        for(float sensorVal: event.values){
                            sensorValues.append(df.format(sensorVal)).append(" \t ");
                        }
                        mSensorValuesText.setText(sensorValues);

                        refreshChart();
                    }
                    break;
        }
    }

    private void refreshChart(){
        List<BubbleValue> bubbleValues = new ArrayList<>();

        // Since we're only plotting one value,
        // Add a base value equal to the max Z size to scale against
        bubbleValues.add(new BubbleValue(0,0, mMaxZ));

        // Add the sensor's value
        BubbleValue value = new BubbleValue(mSensorVals[0], mSensorVals[1], mSensorVals[2]);
        if(value.getZ() >= 0) { // Change the color if it's negative or positive
            value.setColor(Color.BLUE);
        }else{
            value.setColor(Color.RED);
        }
        value.setShape(ValueShape.CIRCLE);
        bubbleValues.add(value);

        Log.d(LOG_TAG, "val: " + value.getX() + " " + value.getY() + " " + value.getZ());

        // Create the chart data from the values
        BubbleChartData sensorChartData = new BubbleChartData(bubbleValues);

        // Set the axis attributes
        Axis axisX = new Axis();
        Axis axisY = new Axis().setHasLines(true);
//        axisX.setName("Axis X");
//        axisY.setName("Axis Y");
        sensorChartData.setAxisXBottom(axisX);
        sensorChartData.setAxisYLeft(axisY);

        mSensorChartView.setBubbleChartData(sensorChartData);
    }

    // Sets the viewport dimensions
    private void setViewPort(int size){
        Viewport viewport = new Viewport(mSensorChartView.getMaximumViewport());
        viewport.bottom = -1 * size;
        viewport.top = size;
        viewport.left = -1 * size;
        viewport.right = size;
        mSensorChartView.setMaximumViewport(viewport);
        mSensorChartView.setCurrentViewport(viewport);
        mSensorChartView.setViewportCalculationEnabled(false);
    }

    @Override
    public void onResume(){
        super.onResume();

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.sensors));

        registerSensor(mSelectedSensor);
        for(Sensor sensor: mSingleSensorList){
            registerSensor(sensor);
        }
    }

    @Override
    public void onPause(){
        unregisterSensor(mSelectedSensor);
        for(Sensor sensor: mSingleSensorList){
            unregisterSensor(sensor);
        }

        super.onPause();
    }

    private void registerSensor(Sensor sensor){
        mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void unregisterSensor(Sensor sensor){
        mSensorManager.unregisterListener(this, sensor);
    }

}
