package com.pxlweavr.drivr;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by IanDMiller on 8/8/16.
 */
public class InstrumentFragment extends Fragment {
    /** @brief Pointer to the data this fragment is displaying */
    DataStream data;
    TextView dataLabel;
    TextView nameLabel;
    RelativeLayout layout;

    private DashboardScreen.StreamController streamController;

    /**
     * Called when the fragment attaches to an Activity
     * @param activity The activity we just attached to
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        streamController = (DashboardScreen.StreamController) activity;
    }

    public void setData(DataStream d) {
        data = d;
    }

    /**
    * Set up the Data display
    * @param inflater Inflater to use to inflate the View
    * @param container The container the Fragment will be in
    * @param savedInstanceState The state of the app
    * @return The inflated view
    */
    @Override
    public android.view.View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View rootView = inflater.inflate(R.layout.instrument_fragment, container, false);

        nameLabel = (TextView) rootView.findViewById(R.id.name_label);
        dataLabel = (TextView) rootView.findViewById(R.id.data_label);
        layout = (RelativeLayout) rootView.findViewById(R.id.background_layout);

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                streamController.selectStream(data);
            }
        });

        //If we have data, load up data
        if (data != null) {
            nameLabel.setText(data.getName());
        }

        //Hacky way of forcing even layout
        //Probably a beter way of doing this
        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        layout.setMinimumWidth((size.x-60)/3);

        return rootView;
    }

    public void select(boolean selected) {
        if (selected) {
            //gray
            layout.setBackgroundColor(0xFFCCCCCC);
        } else {
            //transparent
            layout.setBackgroundColor(0x00CCCCCC);
        }
    }

    public void refresh() {
        dataLabel.setText(data.getLastEntry().toString());
        nameLabel.setText(data.getName());
    }

    public DataStream getData() {
        return data;
    }
}
