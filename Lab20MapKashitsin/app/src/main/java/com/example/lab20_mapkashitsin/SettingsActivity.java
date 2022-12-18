package com.example.lab20_mapkashitsin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Set;

import helper.DB;
import model.Settings;

public class SettingsActivity extends AppCompatActivity {
    Spinner lifeMes;
    EditText lifeText;
    EditText address;
    ListView layers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        lifeMes = findViewById(R.id.spin_life);
        lifeText = findViewById(R.id.input_life);
        address = findViewById(R.id.input_address);
        layers = findViewById(R.id.list_layers);
        ArrayList<String> measurements = new ArrayList<String>();
        measurements.add("s");
        measurements.add("m");
        measurements.add("d");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, measurements);
        lifeMes.setAdapter(adapter);
        for (int i = 0; i < measurements.size(); i++)
        {
            if (measurements.get(i) == Settings.lifeMes)
                lifeMes.setSelection(i);
        }
        lifeMes.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (lifeMes.getSelectedItem().toString())
                {
                    case "s":Settings.lifeMes="s";break;
                    case "m":Settings.lifeMes="m";break;
                    case "d":Settings.lifeMes="d";break;
                    default:Settings.lifeMes="s";break;
                }
                return false;
            }
        });
        lifeText.setText(String.valueOf(Settings.lifeValue));
        address.setText(Settings.address);
        layers.setAdapter(new LayerAdapter(this));

    }

    public void onOK(View v)
    {
        int life = 0;
        try {
            life = Integer.parseInt(lifeText.getText().toString());
            Settings.lifeValue = life;
            Settings.lifeMes = lifeMes.getSelectedItem().toString();
        }
        catch (Exception ex)
        {
            return;
        }
        Tile.life = Settings.getLife();
        DB.helper.setAddress(address.getText().toString());
        Settings.address = address.getText().toString();
        DB.helper.setLife(Settings.lifeValue,Settings.lifeMes);

    }

    public void onClearCache(View v)
    {
        DB.helper.clearCache();
    }
}