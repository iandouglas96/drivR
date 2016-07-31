package com.pxlweavr.drivr;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by IanDMiller on 7/31/16.
 */
public class BluetoothDeviceAdapter extends ArrayAdapter<BluetoothDevice> {
    /**
     * Default constructor
     * @param context The context to create the adapter in
     * @param devices The array of devices we are going to display
     */
    public BluetoothDeviceAdapter(Context context, ArrayList<BluetoothDevice> devices) {
        super(context, 0, devices);
    }

    /**
     * Get the view for a particular BluetoothDevice
     * @param position The index of the ArrayList we are rendering
     * @param convertView The View we are going to render
     * @param parent The parent of the View we are rendering
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        BluetoothDevice device = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_device, parent, false);
        }
        // Lookup view for data population
        TextView deviceName = (TextView) convertView.findViewById(R.id.device_name);
        TextView deviceAddr = (TextView) convertView.findViewById(R.id.device_addr);
        // Populate the data into the template view using the data object
        deviceName.setText(device.getName());
        deviceAddr.setText(device.getAddress());
        // Return the completed view to render on screen
        return convertView;
    }
}
