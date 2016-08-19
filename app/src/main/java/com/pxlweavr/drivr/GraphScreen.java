package com.pxlweavr.drivr;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by IanDMiller on 8/18/16.
 */
public class GraphScreen extends Fragment {
    GraphView graph;
    StreamController streamController;

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
     * Set up the Data display
     * @param inflater Inflater to use to inflate the View
     * @param container The container the Fragment will be in
     * @param savedInstanceState The state of the app
     * @return The inflated view
     */
    @Override
    public android.view.View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_graph_screen, container, false);

        graph = (GraphView) rootView.findViewById(R.id.graph);
        //Show legend
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        //Format x axis as timestamps
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity(), format));
        graph.getGridLabelRenderer().setHumanRounding(false);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(100000);

        //Load up initial screens
        ArrayList<DataStream> streams = streamController.getStreams();
        for (DataStream stream : streams) {
            graph.addSeries(stream.getData());
        }

        return rootView;
    }

    public void addData(DataStream data) {
        graph.addSeries(data.getData());
    }

    public void removeData(DataStream data) {
        graph.removeSeries(data.getData());
    }
}
