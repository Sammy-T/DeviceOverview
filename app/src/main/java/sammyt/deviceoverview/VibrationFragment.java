package sammyt.deviceoverview;


import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class VibrationFragment extends Fragment {

    private final String LOG_TAG = this.getClass().getSimpleName();

    public VibrationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_vibration, container, false);

        ListView vibrateSelectView = root.findViewById(R.id.vibrate_selection);

        ArrayList<String> vibrateOptions = new ArrayList<>();
        vibrateOptions.add("Short");
        vibrateOptions.add("Short Pattern");
        vibrateOptions.add("Long");
        vibrateOptions.add("Long Pattern");
        vibrateOptions.add("Wumbo Pattern");

        final ArrayList<long[]> vibratePatterns = new ArrayList<>();
        vibratePatterns.add(new long[]{100, 200});
        vibratePatterns.add(new long[]{100, 200, 100, 200});
        vibratePatterns.add(new long[]{100, 400});
        vibratePatterns.add(new long[]{100, 400, 100, 400});
        vibratePatterns.add(new long[]{100, 200, 50, 200, 400, 100, 100, 500, 1000, 500, 100, 300,
                750, 200, 50, 200, 400, 100, 100, 500, 1000, 500, 100, 300});

        ArrayAdapter<String> vibrateAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_list_item_1, vibrateOptions);
        vibrateSelectView.setAdapter(vibrateAdapter);

        final Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);

        vibrateSelectView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(vibrator.hasVibrator()){
                    vibrator.vibrate(vibratePatterns.get(position), -1);
                }else{
                    Toast.makeText(getContext(), "Hardware not available", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return root;
    }

}
