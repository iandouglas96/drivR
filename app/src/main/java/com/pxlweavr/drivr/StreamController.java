package com.pxlweavr.drivr;

import java.util.ArrayList;

/**
 * Created by IanDMiller on 8/18/16.
 */
public interface StreamController {
    public DataStream createStream();
    public ArrayList<DataStream> getStreams();
    public void updateStream(DataStream ds);
    public void selectStream(DataStream ds);
    public void deleteStream(DataStream ds);
}