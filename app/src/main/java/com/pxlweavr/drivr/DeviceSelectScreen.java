package com.pxlweavr.drivr;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Set;

public class DeviceSelectScreen extends AppCompatActivity {
    private ListView deviceSelector;
    private TextView statusLabel;
    ArrayList<BluetoothDevice> devicesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_select_screen);

        deviceSelector = (ListView) findViewById(R.id.device_selector);
        statusLabel = (TextView) findViewById(R.id.status_label);

        Button cancel = (Button) findViewById(R.id.cancel);
        Button select = (Button) findViewById(R.id.select);

        //Quit immediately
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });

        //Select and return the chosen device
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Is anything even selected?
                if (deviceSelector.getCheckedItemCount() == 1) {
                    int devicePos = deviceSelector.getCheckedItemPosition();
                    BluetoothDevice device = devicesList.get(devicePos);

                    //Return the selected device
                    Intent returnDeviceIntent = new Intent();
                    returnDeviceIntent.putExtra("device", device);
                    setResult(Activity.RESULT_OK, returnDeviceIntent);
                    finish();
                } else {
                    showError("No Device Selected");
                }
            }
        });

        scanForDevices();
    }

    /**
     * Display simple error message to the user
     * @param msg The string to show to the user
     */
    private void showError(String msg) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Error");
        alertDialog.setMessage(msg);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    /**
     * Scan for all paired Bluetooth Devices and show them on the device list
     */
    private void scanForDevices() {
        //Adapter we'll be scanning with
        BluetoothAdapter obdAdapter = BluetoothAdapter.getDefaultAdapter();

        //If we don't have any bluetooth, we are sad :(
        if (obdAdapter == null) {
            statusLabel.setText("ERROR: No Bluetooth Available");
        }

        //Enable bluetooth if it wasn't on already
        if (!obdAdapter.isEnabled()) {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 0);
        }

        //Get list of devices
        Set<BluetoothDevice> devicesSet = obdAdapter.getBondedDevices();
        //COnvert to arraylist for display
        devicesList = new ArrayList<BluetoothDevice>();
        devicesList.addAll(devicesSet);

        BluetoothDeviceAdapter arrayAdapter = new BluetoothDeviceAdapter(this, devicesList);
        deviceSelector.setAdapter(arrayAdapter);
    }
}
