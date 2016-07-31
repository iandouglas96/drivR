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

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        device = (BluetoothDevice)intent.getParcelableExtra("device");

        handler = new Handler();

        Thread btThread = new Thread() {
            @Override
            public void run() {
                InputStream inputStream;
                boolean stopWorker = false;
                int readBufferPosition = 0;
                byte[] readBuffer = new byte[1024];

                try {
                    inputStream = getStream();
                } catch (IOException ex) {
                    //ABORT!
                    inputStream = null;
                    Thread.currentThread().interrupt();
                }

                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        int bytesAvailable = inputStream.available();
                        if (bytesAvailable > 0) {
                            byte[] packetBytes = new byte[bytesAvailable];
                            inputStream.read(packetBytes);
                            for (int i = 0; i < bytesAvailable; i++) {
                                byte b = packetBytes[i];
                                if (b == 13) {
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
                        Thread.currentThread().interrupt();
                    }
                }
            }
        };

        btThread.start();

        return super.onStartCommand(intent, flags, startId);
    }

    private void sendMessage(String msg) {
        Log.d("sender", "Broadcasting message");
        Intent intent = new Intent("custom-event-name");
        // You can also include some extra data.
        intent.putExtra("message", msg);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
