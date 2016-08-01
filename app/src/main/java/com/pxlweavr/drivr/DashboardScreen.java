package com.pxlweavr.drivr;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

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
        outputLabel = (TextView) rootView.findViewById(R.id.output_label);
        timeLabel = (TextView) rootView.findViewById(R.id.time_label);

        return rootView;
    }

    /**
     * Display the data to the user in the UI
     * @param data ArrayList of the data from the OBDIIC&C
     * @param time Timestamp in ms from epoch of data
     */
    public void displayData(ArrayList<Integer> data, Long time) {
        String outputString = "";
        //Format data
        for (Integer num : data) {
            outputString += (num.toString() + ", ");
        }

        if (data.size() > 0) {
            outputLabel.setText(outputString);
        } else {
            outputLabel.setText("Invalid Data Format");
        }

        //Format time
        Date date = new Date(time);
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss:SSS");
        String dateFormatted = formatter.format(date);

        timeLabel.setText(dateFormatted);
    }
}
