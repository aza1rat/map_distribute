package com.example.lab20_mapkashitsin;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import org.json.JSONObject;

import helper.ApiHelper;
import helper.DB;
import model.Settings;

public class Tile {
    int scale;
    int x;
    int y;
    Bitmap bmp;
    public static MapView mapView;
    public static int life = 200;

    public Tile(int x, int y, int scale, Activity ctx)
    {
        this.x = x;
        this.y = y;
        this.scale = scale;
        this.bmp = null;
        byte[] image = DB.helper.getTileImage(x,y,scale);
        if (image != null)
        {
            bmp = BitmapFactory.decodeByteArray(image,0,image.length);
            return;
        }

        ApiHelper req = new ApiHelper(ctx)
        {
            @Override
            public void onReady(String res) {
                try {
                    JSONObject obj = new JSONObject(res);
                    String b64 = obj.getString("data");
                    DB.helper.addTile(x,y,scale,b64, System.currentTimeMillis() / 1000L + life);
                    byte[] jpeg = Base64.decode(b64, Base64.DEFAULT);
                    bmp = BitmapFactory.decodeByteArray(jpeg,0,jpeg.length);
                    mapView.invalidate();
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        };
        String request = Settings.address + "/raster/" + scale + "/" + x + "-" + y;
        req.send(request);
    }
}
