package com.pxlweavr.drivr;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.Series;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by IanDMiller on 8/8/16.
 */
public class DataStream {
    private Integer index;
    private LineGraphSeries<DataPoint> points;
    private Double lastEntry;
    private Double divisor;
    private Integer maxValuesStored;

    public DataStream(String n, Integer i, Double d, Integer mvs) {
        points = new LineGraphSeries<DataPoint>();
        points.setTitle(n);
        index = i;
        divisor = d;
        maxValuesStored = mvs;
    }

    public DataStream() {
        points = new LineGraphSeries<DataPoint>();
        points.setTitle("new");
        index = 0;
        divisor = 1.0;
        maxValuesStored = 1000;
    }

    /**
     * Add new data to this DataStream
     * @param rawData Array of data from the OBDIIC&C
     * @param timePoint Time data was collected (epoch)
     */
    public void addData(ArrayList<Integer> rawData, Long timePoint) {
        //Parse the date
        Date date = new Date(timePoint);
        Double parsedData = (rawData.get(index)/divisor);
        lastEntry = parsedData;
        //Add new data point to the plot or what have you
        points.appendData(new DataPoint(date, parsedData), true, maxValuesStored);
    }

    //Getters
    public LineGraphSeries<DataPoint> getData() {
        return points;
    }

    public String getName() {
        return points.getTitle();
    }

    public Double getLastEntry() {
        if (lastEntry == null) {
            return 0.0;
        } else {
            return lastEntry;
        }
    }
}
