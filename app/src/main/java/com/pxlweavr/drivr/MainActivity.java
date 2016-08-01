package com.pxlweavr.drivr;

import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements DeviceSelectScreen.BluetoothController {
    DeviceSelectScreen deviceSelectFragment;
    DashboardScreen dashboardFragment;

    private TextView statusLabel;

    private TabsPagerAdapter tabAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    /**
     * Method called when the app initially starts up.  Configures UI
     * @param savedInstanceState Initial device state
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        statusLabel = (TextView) findViewById(R.id.status_label);

        viewPager = (ViewPager) findViewById(R.id.pager);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);

        deviceSelectFragment = new DeviceSelectScreen();
        dashboardFragment = new DashboardScreen();

        tabAdapter = new TabsPagerAdapter(getSupportFragmentManager(), deviceSelectFragment, dashboardFragment);

        viewPager.setAdapter(tabAdapter);
        tabLayout.setupWithViewPager(viewPager);
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
        //Register to receive Messages from our BluetoothService
        LocalBroadcastManager.getInstance(this).registerReceiver(dataReceiver, new IntentFilter("OBD_Data"));
        super.onResume();
    }

    /**
     * Connect to Bluetooth device, and start BluetoothService service
     */
    public void openBT(BluetoothDevice bd) {
        Intent intent = new Intent(this, BluetoothService.class);
        intent.putExtra("device", bd);
        startService(intent);

        statusLabel.setText("Bluetooth Opened");
    }

    /**
     * Stop the BluetoothService service
     */
    public void closeBT() {
        stopService(new Intent(this, BluetoothService.class));
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

        dashboardFragment.displayData(data, time);
        }
    };
}