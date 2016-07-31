package com.pxlweavr.drivr;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class MainScreen extends Activity {
    TextView myLabel;
    EditText myTextbox;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothDevice mmDevice;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        Button openButton = (Button) findViewById(R.id.open);
        Button closeButton = (Button) findViewById(R.id.close);
        myLabel = (TextView) findViewById(R.id.label);
        myTextbox = (EditText) findViewById(R.id.entry);

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

    @Override
    protected void onPause() {
        // Unregister since the activity is paused.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
                mMessageReceiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        // Register to receive messages.
        // We are registering an observer (mMessageReceiver) to receive Intents
        // with actions named "custom-event-name".
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("custom-event-name"));
        super.onResume();
    }

    void findBT() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            myLabel.setText("No bluetooth adapter available");
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 0);
        }

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                if (device.getName().equals("IANMBP")) {
                    mmDevice = device;
                    myLabel.setText("Bluetooth Device Found");
                    break;
                }
            }
            if (myLabel.getText() != "Bluetooth Device Found") {
                myLabel.setText("IANMBP not found");
            }
        } else {
            myLabel.setText("No Devices Paired");
        }
    }

    void openBT() throws IOException {
        Intent intent = new Intent(this, BluetoothHandler.class);
        intent.putExtra("device", mmDevice);
        startService(intent);

        myLabel.setText("Bluetooth Opened");
    }

    void closeBT() throws IOException {
        stopService(new Intent(this, BluetoothHandler.class));
        myLabel.setText("Bluetooth Closed");
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("message");
            Log.d("receiver", "Got message: " + message);
        }
    };
}