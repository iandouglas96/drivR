package com.pxlweavr.drivr;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by IanDMiller on 8/1/16.
 */
public class DashboardScreen extends Fragment {
    private TextView outputLabel;
    private TextView timeLabel;
    private StreamController streamController;

    private InstrumentFragment selectedInstrument;

    private GridLayout layout;
    private ArrayList<InstrumentFragment> instruments = new ArrayList<InstrumentFragment>();

    public interface StreamController {
        public DataStream createStream();
        public void selectStream(DataStream ds);
        public void deleteStream(DataStream ds);
    }

    /**
     * Called when the fragment attaches to an Activity
     * @param activity The activity we just attached to
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        streamController = (StreamController) activity;
    }

    /**
     * Set up the Dashbord display
     * @param inflater Inflater to use to inflate the View
     * @param container The container the Fragment will be in
     * @param savedInstanceState The state of the app
     * @return The inflated view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_dashboard_screen, container, false);

        //Connect up to our I/O
        layout = (GridLayout) rootView.findViewById(R.id.dash_layout);

        Button add = (Button) rootView.findViewById(R.id.add_instrument);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = getChildFragmentManager().beginTransaction();

                InstrumentFragment instrument = new InstrumentFragment();
                instruments.add(instrument);
                ft.add(layout.getId(), instrument);

                //Create data for the instrument to display
                instrument.setData(streamController.createStream());

                ft.commit();
            }
        });

        Button delete = (Button) rootView.findViewById(R.id.delete_instrument);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Make sure we have selected something
                if (selectedInstrument != null) {
                    //Delete associated data stream
                    streamController.deleteStream(selectedInstrument.getData());

                    FragmentTransaction ft = getChildFragmentManager().beginTransaction();

                    instruments.remove(selectedInstrument);
                    ft.remove(selectedInstrument);

                    ft.commit();
                }
            }
        });

        return rootView;
    }

    public void selectStream(DataStream ds) {
        for (InstrumentFragment i : instruments) {
            if (i.getData() == ds) {
                selectedInstrument = i;
                i.select(true);
            } else {
                i.select(false);
            }
        }
    }

    public void refreshInstruments() {
        for (InstrumentFragment i : instruments) {
            i.refresh();
        }
    }
}
