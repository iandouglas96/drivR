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
    private static Integer numStreams = 0;

    private Integer index;
    private LineGraphSeries<DataPoint> points;
    private Double lastEntry;
    private Double divisor;
    private Integer maxValuesStored;
    private String abbrev;
    private Integer id;

    public DataStream(String n, String a, Integer i, Integer f, Integer mvs, Integer idNum) {
        points = new LineGraphSeries<DataPoint>();
        points.setTitle(n);
        abbrev = a;
        index = i;
        setFormat(f);
        maxValuesStored = mvs;
        abbrev = n;
        id = idNum;

        if (id >= numStreams) {
            numStreams = id+1;
        }
    }

    public DataStream() {
        points = new LineGraphSeries<DataPoint>();
        points.setTitle("new");
        index = 0;
        divisor = 1.0;
        maxValuesStored = 1000;
        id = numStreams++;
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

    private Integer parseFormat(Double div) {
        if (div == 1.0) {
            return 0;
        } else if (div == 10.0) {
            return 1;
        } else if (div == 100.0) {
            return 2;
        } else if (div == 1000.0) {
            return 3;
        }
        return -1;
    }

    //Setters
    public void setAbbrev(String a) {
        abbrev = a;
    }

    public void setName(String n) {
        points.setTitle(n);
    }

    public void setIndex(Integer i) {
        index = i;
    }

    public void setFormat(Integer f) {
        switch (f) {
            case 0:
                divisor = 1.0;
                break;
            case 1:
                divisor = 10.0;
                break;
            case 2:
                divisor = 100.0;
                break;
            case 3:
                divisor = 1000.0;
                break;
            default:
                divisor = 1.0;
                break;
        }
    }

    //Getters
    public Integer getFormat() {
        return parseFormat(divisor);
    }

    public Integer getChannel() {
        return index;
    }

    public LineGraphSeries<DataPoint> getData() {
        return points;
    }

    public String getName() {
        return points.getTitle();
    }

    public String getAbbrev() {
        return abbrev;
    }

    public Integer getId() {
        return id;
    }

    public Double getLastEntry() {
        if (lastEntry == null) {
            return 0.0;
        } else {
            return lastEntry;
        }
    }
}
