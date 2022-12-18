package com.example.lab20_mapkashitsin;

import android.graphics.Paint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import model.Line;

public class Lines {
    ArrayList<ArrayList> lines = new ArrayList<ArrayList>();
    ArrayList<Line> line = new ArrayList<Line>();
    Paint p;

    public void collectLines(JSONArray res) throws JSONException {
        for (int i = 0; i < res.length(); i++)
        {
            line = new ArrayList<Line>();
            for(int j = 0; j < res.getJSONArray(i).length(); j++)
            {
                Line xy = new Line(res.getJSONArray(i).getJSONObject(j).getDouble("x"),
                        res.getJSONArray(i).getJSONObject(j).getDouble("y"));
                line.add(xy);

            }
            lines.add(line);
        }
    }
}
