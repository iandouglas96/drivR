package com.pxlweavr.drivr;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

public class MainScreen extends Activity {
    private static final int DEVICE_SELECT = 1;

    TextView statusLabel;
    TextView outputLabel;
    TextView timeLabel;

    BluetoothDevice obdDevice = null;

    /**
     * Method called when the app initially starts up.  Configures UI
     * @param savedInstanceState Initial device state
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        //Connect up to our I/O
        Button openButton = (Button) findViewById(R.id.open);
        Button closeButton = (Button) findViewById(R.id.close);
        Button selectDeviceButton = (Button) findViewById(R.id.select_device);
        statusLabel = (TextView) findViewById(R.id.status_label);
        outputLabel = (TextView) findViewById(R.id.output_label);
        timeLabel = (TextView) findViewById(R.id.time_label);

        //Open Button
        openButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    openBT();
                } catch (IOException ex) {
                    Log.d("", "Could not open connection");
                }
            }
        });

        //Close button
        closeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    closeBT();
                } catch (IOException ex) {
                    Log.d("", "Could not close connection");
                }
            }
        });

        //Select device button
        selectDeviceButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDeviceSelect();
            }
        });
    }

    /**
     * Start the Device Select Screen for a result
     */
    private void showDeviceSelect() {
        Intent deviceSelectIntent = new Intent(this, DeviceSelectScreen.class);
        startActivityForResult(deviceSelectIntent, DEVICE_SELECT);
    }

    /**
     * Pause the app by unregistering from our Bluetooth handler
     */
    @Override
    protected void onPause() {
        // Unregister since the activity is paused.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(dataReceiver);
        super.onPause();
    }

    /**
     * When we resume, reconnect to the Bluetooth handler
     */
    @Override
    protected void onResume() {
        //Register to receive Messages from our BluetoothHandler
        LocalBroadcastManager.getInstance(this).registerReceiver(dataReceiver, new IntentFilter("OBD_Data"));
        super.onResume();
    }

    /**
     * Connect to Bluetooth device, and start BluetoothHandler service
     * @throws IOException
     */
    void openBT() throws IOException {
        Intent intent = new Intent(this, BluetoothHandler.class);
        intent.putExtra("device", obdDevice);
        startService(intent);

        statusLabel.setText("Bluetooth Opened");
    }

    /**
     * Stop the BluetoothHandler service
     * @throws IOException
     */
    void closeBT() throws IOException {
        stopService(new Intent(this, BluetoothHandler.class));
        statusLabel.setText("Bluetooth Closed");
    }

    /**
     * The receiver for data coming from bluetooth.
     */
    private BroadcastReceiver dataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("receiver", "Got message");

            //Get data included in the Intent
            ArrayList<Integer> data = intent.getIntegerArrayListExtra("data");
            //Get timestamp
            Long time = intent.getLongExtra("time", -1);

            displayData(data, time);
        }
    };

    /**
     * Display the data to the user in the UI
     * @param data ArrayList of the data from the OBDIIC&C
     * @param time Timestamp in ms from epoch of data
     */
    private void displayData(ArrayList<Integer> data, Long time) {
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

    /**
     * Handle data returned by other activities
     * @param requestCode The activity code we are getting data from
     * @param resultCode The result of the activity
     * @param data Data returned from the activity
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (DEVICE_SELECT): {
                if (resultCode == Activity.RESULT_OK) {
                    //Read in the data
                    try {
                        closeBT();
                    } catch (IOException ioe) {
                        Log.d("", ioe.getMessage());
                    }
                    obdDevice = (BluetoothDevice) data.getParcelableExtra("device");
                    statusLabel.setText(obdDevice.getName() + " Selected");
                } else {
                    statusLabel.setText("No Device Selected");
                }
                break;
            }
        }
    }
}