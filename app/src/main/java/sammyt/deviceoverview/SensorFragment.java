package sammyt.deviceoverview;


import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

    private ModBubbleChartView mSensorChartView;
    private CustomVertBar mSensorBarZ;
    private RecyclerView mSensorGridView;

    private SingleSensorAdapter mSingleAdapter;
    private ArrayList<Sensor> mSensorList;

    private float[] mSensorVals;
    private String[] mSensorText;
    private BubbleChartData mSensorChartData;
    private ValueShape mShape = ValueShape.CIRCLE;

    public SensorFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_sensor, container, false);
        mSensorChartView = root.findViewById(R.id.chart);
        mSensorGridView = (RecyclerView) root.findViewById(R.id.sensor_grid);

        Viewport viewport = new Viewport(mSensorChartView.getMaximumViewport());
        viewport.bottom = -15;
        viewport.top = 15;
        viewport.left = -15;
        viewport.right = 15;
        mSensorChartView.setMaximumViewport(viewport);
        mSensorChartView.setCurrentViewport(viewport);
        mSensorChartView.setViewportCalculationEnabled(false);
//        mAccelChartView.setZoomEnabled(false);

        mSensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);

        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        List<Sensor> sensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);

        // Build the array list with the single value sensors
        mSensorList = new ArrayList<>();

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
                mSensorList.add(sensor);
            }
        }

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        mSensorGridView.setLayoutManager(layoutManager);

        mSingleAdapter = new SingleSensorAdapter(mSensorList);
        mSensorGridView.setAdapter(mSingleAdapter);

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
            case Sensor.TYPE_ACCELEROMETER:
                if(mSensorVals != null && Arrays.equals(mSensorVals, event.values)){
                    break;
                }
                mSensorVals = new float[]{event.values[0], event.values[1], event.values[2]};
//                mAccelText = new String[]{
//                        "x: " + (event.values[0] * 10),
//                        "y: " + (event.values[1] * 10),
//                        "z: " + (event.values[2] * 10)};
//                mAccelBarX.setProgress(mAccelVals[0]*10, mAccelText[0]);
//                mAccelBarY.setProgress(mAccelVals[1]*10, mAccelText[1]);

//                mSensorBarZ.setProgress(mSensorVals[2]*10, df.format(event.values[2]), "Z");
                refreshChart();
                break;
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
        }
    }

    private void refreshChart(){
        List<BubbleValue> bubbleValues = new ArrayList<>();
        bubbleValues.add(new BubbleValue(0,0,10));

        BubbleValue value = new BubbleValue(mSensorVals[0], mSensorVals[1], mSensorVals[2]);
        Log.d(LOG_TAG, "val: " + value.getX() + " " + value.getY() + " " + value.getZ());
//        value.setColor(ChartUtils.pickColor());
        value.setColor(Color.BLUE);
        value.setShape(mShape);
        bubbleValues.add(value);

        mSensorChartData = new BubbleChartData(bubbleValues);

        Axis axisX = new Axis();
        Axis axisY = new Axis().setHasLines(true);
        axisX.setName("Axis X");
        axisY.setName("Axis Y");
        mSensorChartData.setAxisXBottom(axisX);
        mSensorChartData.setAxisYLeft(axisY);

        mSensorChartView.setBubbleChartData(mSensorChartData);
    }

    @Override
    public void onResume(){
        super.onResume();

        registerSensor(mAccelerometer);
        for(Sensor sensor: mSensorList){
            registerSensor(sensor);
        }
    }

    @Override
    public void onPause(){
        unregisterSensor(mAccelerometer);
        for(Sensor sensor: mSensorList){
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
