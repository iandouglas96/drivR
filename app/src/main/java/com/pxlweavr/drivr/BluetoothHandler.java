package com.pxlweavr.drivr;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.IOException;
import java.net.Socket;
import java.util.UUID;
import java.io.InputStream;

/**
 * @brief Class to handle reading and parsing data from the OBDIIC&C
 */
public class BluetoothHandler extends Service {
    /**
     * @brief Pointer to bluetooth device we are talking to
     */
    private BluetoothDevice device;

    private Handler handler;

    /**
     * @brief Thread managing receiving bluetooth packets
     */
    Thread btThread = new Thread() {
        @Override
        public void run() {
            InputStream inputStream;
            boolean stopWorker = false;
            int readBufferPosition = 0;
            byte[] readBuffer = new byte[1024];

            //Get the input stream
            try {
                inputStream = getStream();
            } catch (IOException ex) {
                //ABORT!
                inputStream = null;
                Thread.currentThread().interrupt();
            }

            //Loop until the Thread gets killed
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    //Do we have anything new in the buffer?
                    int bytesAvailable = inputStream.available();
                    if (bytesAvailable > 0) {
                        //Read in the buffer
                        byte[] packetBytes = new byte[bytesAvailable];
                        inputStream.read(packetBytes);
                        for (int i = 0; i < bytesAvailable; i++) {
                            //Check if we have a termination character
                            byte b = packetBytes[i];
                            if (b == 13) {
                                //We have a new line, dump the buffer
                                byte[] encodedBytes = new byte[readBufferPosition];
                                System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                final String data = new String(encodedBytes, "US-ASCII");
                                readBufferPosition = 0;

                                Runnable sendData = new Runnable() {
                                    @Override
                                    public void run() {
                                        sendMessage(data);
                                    }
                                };
                                handler.post(sendData);
                            } else {
                                readBuffer[readBufferPosition++] = b;
                            }
                        }
                    }
                } catch (Exception e) {
                    //Interrupt ourselves
                    Thread.currentThread().interrupt();
                }
            }

            //Clean up
            try {
                inputStream.close();
            } catch (IOException ioe) {
                Log.d("", "Cleanup failed");
            }
        }
    };

    /**
     * @brief Required callback for Service, we don't need to do anything
     * @param intent
     * @return
     */
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @brief Helper method to get the input Stream from the BT device
     * @return An InputStream to read serial data from
     * @throws IOException
     */
    private InputStream getStream() throws IOException {
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Standard SerialPortService ID
        BluetoothSocket socket = device.createRfcommSocketToServiceRecord(uuid);
        try {
            socket.connect();
            Log.e("", "Connected");
        } catch (IOException ioe) {
            Log.e("", "Connection Failed");
            Log.e("", ioe.getMessage());
        }
        InputStream inputStream = socket.getInputStream();

        return inputStream;
    }

    /**
     * @brief Called on startup of the Service
     * @param intent Includes data of the device we need to connect to
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        device = (BluetoothDevice)intent.getParcelableExtra("device");

        handler = new Handler();

        btThread.start();

        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * @brief Send parsed data back to the UI layer
     * @param msg The raw string read in from the BT dongle to parse
     */
    private void sendMessage(String msg) {
        Log.d("sender", "Broadcasting message");
        Intent intent = new Intent("OBD_Data");
        // You can also include some extra data.
        intent.putExtra("message", msg);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    /**
     * @brief Called to destroy the Service.  Stop the BT thread.
     */
    @Override
    public void onDestroy() {
        btThread.interrupt();
    }
}
