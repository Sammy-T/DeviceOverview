package sammyt.deviceoverview;

import android.hardware.Sensor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class SingleSensorAdapter extends RecyclerView.Adapter<SingleSensorAdapter.ViewHolder>{

    private final String LOG_TAG = this.getClass().getSimpleName();

    private ArrayList<Sensor> mSensorList;
    private ArrayList<Float> mValueList;

    private DecimalFormat df;

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView sensorNameText;
        public TextView sensorValueText;

        public ViewHolder(View view){
            super(view);
            sensorNameText = (TextView) view.findViewById(R.id.sensor_name);
            sensorValueText = (TextView) view.findViewById(R.id.sensor_value);
        }
    }

    public SingleSensorAdapter(ArrayList<Sensor> sensorList){
        mSensorList = sensorList;
        mValueList = new ArrayList<>();

        for (Sensor sensor: mSensorList) {
            mValueList.add((float) 0);
        }

        df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
    }

    // Create new views (invoked by Layout Manager)
    @Override
    @NonNull
    public SingleSensorAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        // set the view's size, margins, paddings and layout parameters here if needed

        int layout = R.layout.sensor_item;

        View view = LayoutInflater.from(parent.getContext())
                .inflate(layout, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return  viewHolder;
    }

    // Replace contents of view (invoked by Layout Manager)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position){

        String sensorName = mSensorList.get(position).getName();
        float sensorValue = mValueList.get(position);
        String valueText = getValueText(mSensorList.get(position), sensorValue);

        holder.sensorNameText.setText(sensorName);
        holder.sensorValueText.setText(valueText);
    }

    // Return the size of the dataset (invoked by Layout Manager)
    @Override
    public int getItemCount(){
        return mSensorList.size();
    }

    // Update the Sensor value
    public void updateItem(Sensor sensor, float value){
        int position = mSensorList.indexOf(sensor);

        if(position >= 0){
            mValueList.set(position, value);
            notifyDataSetChanged();
        }else{
            Log.e(LOG_TAG, "Sensor not found. Cannot update data set.");
        }
    }

    // Helper function to get the unit for each sensor
    private String getValueText(Sensor sensor, float value){
        String valueText = df.format(value);

        switch(sensor.getType()){
            case Sensor.TYPE_STEP_COUNTER:
                valueText += " steps";
                break;
            case Sensor.TYPE_PROXIMITY:
                valueText += " cm";
                break;
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                valueText += " °C";
                break;
            case Sensor.TYPE_LIGHT:
                valueText += " lx";
                break;
            case Sensor.TYPE_PRESSURE:
                valueText += " hPa";
                break;
            case Sensor.TYPE_RELATIVE_HUMIDITY:
                valueText += " %";
                break;
        }

        return valueText;
    }
}
