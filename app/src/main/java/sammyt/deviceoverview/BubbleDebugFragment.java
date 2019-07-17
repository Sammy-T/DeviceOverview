package sammyt.deviceoverview;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
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
public class BubbleDebugFragment extends Fragment {

    private final String LOG_TAG = this.getClass().getSimpleName();

//    BubbleChartView chart;
    ModBubbleChartView chart;
    BubbleChartData data;

    boolean calculateViewport = false;

    public BubbleDebugFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_bubble_debug, container, false);
        chart = root.findViewById(R.id.debug_bubble_chart);
        Button debugButton = root.findViewById(R.id.debug_bubble_button);

        //// TODO: The problem starts here
        if(!calculateViewport){
            Viewport viewport = new Viewport(chart.getMaximumViewport());
            viewport.bottom = -20;
            viewport.top = 150;
            viewport.left = -15;
            viewport.right = 15;
            chart.setMaximumViewport(viewport);
            chart.setCurrentViewport(viewport);
            chart.setViewportCalculationEnabled(false);
        }

        debugButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateData();
            }
        });

        generateData();

        return root;
    }

    private void generateData() {

        List<BubbleValue> values = new ArrayList<BubbleValue>();
        for (int i = 0; i < 10; ++i) {
            float x = (20 * (float) Math.random()) - 10;
            float y = (140 * (float) Math.random()) - 10;
            float z = (200 * (float) Math.random()) - 100;
            BubbleValue value = new BubbleValue(x, y, z);
            Log.d(LOG_TAG, "x: " + value.getX() + " y: " + value.getY() + " z: " + value.getZ());
            value.setColor(ChartUtils.pickColor());
            value.setShape(ValueShape.CIRCLE);
            values.add(value);
        }

        boolean hasLabels = true;
        boolean hasLabelForSelected = false;
        boolean hasAxes = true;
        boolean hasAxesNames = true;

        data = new BubbleChartData(values);
        data.setHasLabels(hasLabels);
        data.setHasLabelsOnlyForSelected(hasLabelForSelected);

        if (hasAxes) {
            Axis axisX = new Axis();
            Axis axisY = new Axis().setHasLines(true);
            if (hasAxesNames) {
                axisX.setName("Axis X");
                axisY.setName("Axis Y");
            }
            data.setAxisXBottom(axisX);
            data.setAxisYLeft(axisY);
        } else {
            data.setAxisXBottom(null);
            data.setAxisYLeft(null);
        }

        chart.setBubbleChartData(data);

    }

}
