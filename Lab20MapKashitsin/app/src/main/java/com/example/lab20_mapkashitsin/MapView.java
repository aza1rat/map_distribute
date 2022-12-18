package com.example.lab20_mapkashitsin;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import helper.ApiHelper;
import helper.DB;
import model.Layer;
import model.Line;
import model.Settings;

public class MapView extends SurfaceView {
    ArrayList <Tile> tiles = new ArrayList<Tile>();
    float lastX;
    float lastY;
    int currentLevel = 0;
    int[] levels = new int[] {16,8,4,2,1};
    int[] xTiles = new int[] {54,108,216,432,864};
    int[] yTiles = new int[] {27,54,108,216,432};
    double[] dpp = {
            360.0 / (86400 / 16),
            360.0 / (86400 / 8),
            360.0 / (86400 / 4),
            360.0 / (86400 / 2),
            360.0 / (86400 / 1)
    };
    int tileWidth = 100;
    int tileHeight = 100;
    float offsetX = 0.0f;
    float offsetY = 0.0f;
    double lat0, lon0;
    double lat1, lon1;
    Paint p;
    int width;
    int height;
    public Activity ctx;
    public ArrayList<Lines> l = new ArrayList<Lines>();

    public MapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        p = new Paint();
        p.setStyle(Paint.Style.STROKE);
        p.setColor(Color.BLACK);
        setWillNotDraw(false);
    }

    Tile getTitle(int x, int y, int scale)
    {
        for (int i = 0; i < tiles.size(); i++)
        {
            Tile t = tiles.get(i);
            if (t.x == x && t.y == y && t.scale == scale)
                return t;
        }
        Tile nt = new Tile(x,y,scale,ctx);
        tiles.add(nt);
        return nt;
    }

    public void update_viewport()
    {
        lat0 = -offsetX * dpp[currentLevel] - 180.0;
        lon0 = 90.0 + offsetY * dpp[currentLevel];
        lat1 = lat0 + width * dpp[currentLevel];
        lon1 = lon0 - height * dpp[currentLevel];
    }

    public double map(double x,  double x0, double x1, double a, double b)
    {
        double t = (x - x0) / (x1 - x0);
        return a + (b - a) * t;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int act = event.getAction();
        switch (act)
        {
            case MotionEvent.ACTION_DOWN:
                lastX = event.getX();
                lastY = event.getY();
                return true;
            case MotionEvent.ACTION_MOVE:
                DB.helper.deleteTile();
                float x = event.getX();
                float y = event.getY();
                float dx = x - lastX;
                float dy = y - lastY;
                offsetX += dx;
                offsetY += dy;
                update_viewport();
                invalidate();

                lastX = x;
                lastY = y;
                return true;
            case MotionEvent.ACTION_UP:
                l.clear();
                for (Layer layer: Settings.layers) {
                    if (layer.isEnabled)
                    {
                        Lines line = new Lines();
                        line.p = new Paint();
                        line.p.setStyle(Paint.Style.STROKE);
                        line.p.setColor(layer.color);
                        l.add(line);
                        getLine(line,layer.color,layer.name);
                    }
                }

                return true;
        }
        return false;
    }

    void getLine(Lines line, int color, String name)
    {
        ApiHelper req = new ApiHelper(ctx){
            @Override
            public void onReady(String res) {
                try {
                    JSONArray array = new JSONArray(res);
                    line.collectLines(array);
                    invalidate();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        String s = Settings.address + "/" +name+"/"+levels[currentLevel] + "?lat0="+lat0+"&lon0="+lon0+"&lat1="+lat1
                +"&lon1="+lon1;
        req.send(s);

    }

    boolean rectIntersectsRect
    (float ax0, float ay0, float ax1, float ay1,
    float bx0, float by0, float bx1, float by1)
    {
        if (ax1 < bx0) return false;
        if (ax0 > bx1) return false;
        if (ay1 < by0) return false;
        if (ay0 > by1) return false;
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.WHITE);
        width = canvas.getWidth();
        height = canvas.getHeight();
        for (int y = 0; y < yTiles[currentLevel]; y++)
             for (int x = 0; x < xTiles[currentLevel]; x++)
              {
               int x0 = x * tileWidth + (int) offsetX;
               int y0 = y * tileHeight + (int) offsetY;
               int x1 = x0 + tileWidth;
               int y1 = y0 + tileHeight;
               if (!(rectIntersectsRect(0,0,canvas.getWidth() - 1,canvas.getHeight() - 1,x0,y0,x1,y1)))
                   continue;
               Tile t = getTitle(x,y,levels[currentLevel]);
               if (t.bmp != null) canvas.drawBitmap(t.bmp, x0, y0, p);
              }
        try {
            for (Lines lines:l) {
                for (int i = 0; i < lines.lines.size(); i++) {
                    float px0 = 0;
                    float py0 = 0;
                    for (int j = 0; j < lines.lines.get(i).size(); j++) {
                        Line line = (Line) lines.lines.get(i).get(j);
                        float px1 = (float) map(line.x, lat0, lat1, 0, width);
                        float py1 = (float) map(line.y, lon0, lon1, 0, height);
                        if (j == 0) {
                        } else
                            canvas.drawLine(px0, py0, px1, py1, lines.p);
                        px0 = px1;
                        py0 = py1;
                    }
                }
            }

        }
        catch (Exception e)
        {
            return;
        }

    }
}
