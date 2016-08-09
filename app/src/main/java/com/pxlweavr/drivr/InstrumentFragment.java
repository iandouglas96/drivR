package com.pxlweavr.drivr;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

        rootView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                layout.setBackgroundColor(0xFFCCCCCC);

                return true;
            }
        });

        return rootView;
    }

    public void refresh() {
        dataLabel.setText(data.getLastEntry().toString());
        nameLabel.setText(data.getName());
    }
}
