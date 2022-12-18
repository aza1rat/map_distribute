package com.example.lab20_mapkashitsin;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.material.button.MaterialButtonToggleGroup;

import java.util.ArrayList;

import helper.DB;
import model.Layer;
import model.Settings;
import yuku.ambilwarna.AmbilWarnaDialog;

public class LayerAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater inflater;

    public LayerAdapter(Context ctx)
    {
        this.ctx = ctx;
        inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return Settings.layers.size();
    }

    @Override
    public Object getItem(int position) {
        return Settings.layers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null)
            view = inflater.inflate(R.layout.layeritem_layout,parent,false);
        Layer layer = (Layer) getItem(position);
        ((TextView) view.findViewById(R.id.tv_layer)).setText(layer.localName);
        Switch sw = (Switch) view.findViewById(R.id.sw_layer);
        sw.setChecked(layer.isEnabled);
        sw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layer.isEnabled = sw.isChecked();
                DB.helper.updateLayer(layer.name,layer.isEnabled,layer.color);
            }
        });
        Button button = (Button) view.findViewById(R.id.button_layer);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(ctx, layer.color, new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onCancel(AmbilWarnaDialog dialog) {

                    }

                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        layer.color = color;
                        DB.helper.updateLayer(layer.name,layer.isEnabled,layer.color);
                    }
                });
                colorPicker.show();
            }
        });
        return view;
    }


}
