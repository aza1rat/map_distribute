package com.example.lab20_mapkashitsin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;

import helper.DB;
import helper.DBHelper;
import model.Settings;

public class MainActivity extends AppCompatActivity {
    MapView mapView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DB.helper = new DBHelper(this, "map.db", null, 1);
        DB.helper.getSettings();
        mapView = findViewById(R.id.mapView);
        mapView.ctx = (Activity) this;
        Tile.mapView = mapView;
        Tile.life = Settings.getLife();
        int level = Settings.level;
        mapView.offsetX = Settings.offsetX;
        mapView.offsetY = Settings.offsetY;

        for (int i = 0; i < mapView.levels.length; i++)
        {
            if (level == mapView.levels[i])
            {
                mapView.currentLevel = i;
                break;
            }
        }
        mapView.invalidate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DB.helper.updateView(mapView.offsetX,mapView.offsetY,mapView.levels[mapView.currentLevel]);
    }

    public void onZoomIn(View v)
    {
        if (mapView.currentLevel == mapView.levels.length - 1)
            return;
        mapView.currentLevel++;
        mapView.offsetX *= 2;
        mapView.offsetY *= 2;
        mapView.offsetX -= mapView.width /2;
        mapView.offsetY -= mapView.height /2;
        mapView.l = new ArrayList<Lines>();
        mapView.invalidate();
    }

    public void onZoomOut(View v)
    {
        if (mapView.currentLevel == 0)
            return;
        mapView.currentLevel--;
        mapView.offsetX += mapView.width /2;
        mapView.offsetY += mapView.height /2;
        mapView.offsetX /= 2;
        mapView.offsetY /= 2;
        mapView.l = new ArrayList<Lines>();
        mapView.invalidate();
    }

    public void onSettingsClick(View v)
    {
        Intent intent = new Intent(this,SettingsActivity.class);
        startActivity(intent);
    }
}