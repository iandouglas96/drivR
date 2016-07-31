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
import java.util.Set;

public class MainScreen extends Activity {
    TextView statusLabel;
    TextView outputLabel;

    BluetoothAdapter obdAdapter;
    BluetoothDevice obdDevice;

    /**
     * @brief Method called when the app initially starts up.  Configures UI
     * @param savedInstanceState Initial device state
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        //Connect up to our I/O
        Button openButton = (Button) findViewById(R.id.open);
        Button closeButton = (Button) findViewById(R.id.close);
        statusLabel = (TextView) findViewById(R.id.status_label);
        outputLabel = (TextView) findViewById(R.id.output_label);

        //Open Button
        openButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    findBT();
                    openBT();
                } catch (IOException ex) {
                }
            }
        });

        //Close button
        closeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    closeBT();
                } catch (IOException ex) {
                }
            }
        });
    }

    /**
     * @brief Pause the app by unregistering from our Bluetooth handler
     */
    @Override
    protected void onPause() {
        // Unregister since the activity is paused.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(dataReceiver);
        super.onPause();
    }

    /**
     * @brief When we resume, reconnect to the Bluetooth handler
     */
    @Override
    protected void onResume() {
        //Register to receive Messages from our BluetoothHandler
        LocalBroadcastManager.getInstance(this).registerReceiver(dataReceiver, new IntentFilter("OBD_Data"));
        super.onResume();
    }

    /**
     * @brief Search through the paired devices and connect to the appropriate one
     */
    void findBT() {
        obdAdapter = BluetoothAdapter.getDefaultAdapter();
        if (obdAdapter == null) {
            statusLabel.setText("No bluetooth adapter available");
        }

        if (!obdAdapter.isEnabled()) {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 0);
        }

        Set<BluetoothDevice> pairedDevices = obdAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                if (device.getName().equals("IANMBP")) {
                    obdDevice = device;
                    statusLabel.setText("Bluetooth Device Found");
                    break;
                }
            }
            if (statusLabel.getText() != "Bluetooth Device Found") {
                statusLabel.setText("IANMBP not found");
            }
        } else {
            statusLabel.setText("No Devices Paired");
        }
    }

    /**
     * @brief Connect to Bluetooth device, and start BluetoothHandler service
     * @throws IOException
     */
    void openBT() throws IOException {
        Intent intent = new Intent(this, BluetoothHandler.class);
        intent.putExtra("device", obdDevice);
        startService(intent);

        statusLabel.setText("Bluetooth Opened");
    }

    /**
     * @brief Stop the BluetoothHandler service
     * @throws IOException
     */
    void closeBT() throws IOException {
        stopService(new Intent(this, BluetoothHandler.class));
        statusLabel.setText("Bluetooth Closed");
    }

    /**
     * @brief The receiver for data coming from bluetooth.
     */
    private BroadcastReceiver dataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("message");
            Log.d("receiver", "Got message: " + message);
        }
    };
}