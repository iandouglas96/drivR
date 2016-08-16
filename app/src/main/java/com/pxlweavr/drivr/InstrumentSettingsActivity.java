package com.pxlweavr.drivr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by IanDMiller on 8/15/16.
 */
public class InstrumentSettingsActivity extends Activity {
    EditText instrumentName;
    EditText instrumentAbbrev;
    Spinner instrumentFormat;
    Spinner instrumentChannel;

    /**
     * Method called when the app initially starts up.  Configures UI
     * @param savedInstanceState Initial device state
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.instrument_settings);

        instrumentName = (EditText) findViewById(R.id.instrument_name);
        instrumentAbbrev = (EditText) findViewById(R.id.instrument_abbrev);

        instrumentFormat = (Spinner) findViewById(R.id.instrument_format);
        ArrayAdapter<CharSequence> formatAdapter = ArrayAdapter.createFromResource(this, R.array.formatting_array, android.R.layout.simple_spinner_item);
        formatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        instrumentFormat.setAdapter(formatAdapter);

        instrumentChannel = (Spinner) findViewById(R.id.instrument_channel);
        ArrayAdapter<CharSequence> channelAdapter = ArrayAdapter.createFromResource(this, R.array.channel_array, android.R.layout.simple_spinner_item);
        channelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        instrumentChannel.setAdapter(channelAdapter);

        Intent i = getIntent();
        instrumentName.setText(i.getStringExtra("name"));
        instrumentAbbrev.setText(i.getStringExtra("abbrev"));
        instrumentFormat.setSelection(i.getIntExtra("format", 0));
        instrumentChannel.setSelection(i.getIntExtra("channel", 0));

        Button ok = (Button) findViewById(R.id.ok_button);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.putExtra("name", instrumentName.getText().toString());
                i.putExtra("abbrev", instrumentAbbrev.getText().toString());
                i.putExtra("format", instrumentFormat.getSelectedItemPosition());
                i.putExtra("channel", instrumentChannel.getSelectedItemPosition());

                setResult(RESULT_OK, i);
                finish();
            }
        });
    }
}
