package helper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import android.graphics.Color;
import android.util.Base64;

import java.util.Set;

import model.Layer;
import model.Settings;

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sqlCreate = "CREATE TABLE tile (" +
                "id INT NOT NULL," +
                "level INT NOT NULL," +
                "x INT NOT NULL," +
                "y INT NOT NULL," +
                "data BLOB NOT NULL," +
                "life TIMESTAMP NOT NULL);";
        sqLiteDatabase.execSQL(sqlCreate);
        sqlCreate = "CREATE TABLE settings (" +
                "id INT NOT NULL," +
                "address VARCHAR(60) NOT NULL," +
                "offsetX REAL NOT NULL," +
                "offsetY REAL NOT NULL," +
                "level INT NOT NULL," +
                "lifevalue INT NOT NULL," +
                "lifemes VARCHAR(2) NOT NULL);";
        sqLiteDatabase.execSQL(sqlCreate);
        sqlCreate = "CREATE UNIQUE INDEX xy ON tile (x,y,level);";
        sqLiteDatabase.execSQL(sqlCreate);
        String sqlInsert = "INSERT INTO settings VALUES (" +
                "1,'http://tilemap.spbcoit.ru:7000',0.0,0.0,16,200,'s');";
        sqLiteDatabase.execSQL(sqlInsert);
        sqlCreate = "CREATE TABLE layer (" +
                "id INT NOT NULL," +
                "localName VARCHAR(20) NOT NULL," +
                "name VARCHAR(40) NOT NULL," +
                "isEnabled INT NOT NULL," +
                "color INT NOT NULL);";
        sqLiteDatabase.execSQL(sqlCreate);
        sqlInsert = "INSERT INTO layer VALUES (1,'Б.линии','coastline',0,"+ Color.BLACK+");";
        sqLiteDatabase.execSQL(sqlInsert);
        sqlInsert = "INSERT INTO layer VALUES (2,'Реки','river',0,"+ Color.BLACK+");";
        sqLiteDatabase.execSQL(sqlInsert);
        sqlInsert = "INSERT INTO layer VALUES (3,'Дороги','road',0,"+ Color.BLACK+");";
        sqLiteDatabase.execSQL(sqlInsert);
        sqlInsert = "INSERT INTO layer VALUES (4,'Ж.дороги','railroad',0,"+ Color.BLACK+");";
        sqLiteDatabase.execSQL(sqlInsert);
    }

    public void updateLayer(String name, boolean isChecked, int color)
    {
        SQLiteDatabase sqlDB = getWritableDatabase();
        int enabled = 0;
        if (isChecked)
            enabled = 1;
        String sql = "UPDATE layer " +
                "SET isEnabled = " + enabled + "," +
                "color = "+color +
                " WHERE name = '" + name + "';";
        sqlDB.execSQL(sql);
    }

    public void updateView(float x, float y, int level)
    {
        SQLiteDatabase sqlDB = getWritableDatabase();
        String sql = "UPDATE settings " +
                "SET offsetX = " + x + "," +
                "offsetY = " + y + "," +
                "level = " + level + " WHERE id = 1;";
        sqlDB.execSQL(sql);
    }

    public void setAddress(String address)
    {
        SQLiteDatabase sqlDB = getWritableDatabase();
        String sql = "UPDATE settings " +
                "SET address = '" + address + "' " +
                "WHERE id = 1;";
        sqlDB.execSQL(sql);
    }

    public void setLife(int value, String mes)
    {
        SQLiteDatabase sqlDB = getWritableDatabase();
        String sql = "UPDATE settings " +
                "SET lifemes = '" + mes + "'," +
                "lifevalue = " + value +
                " WHERE id = 1;";
        sqlDB.execSQL(sql);
    }

    public void getSettings()
    {
        String sql = "SELECT * FROM settings WHERE id = 1;";
        SQLiteDatabase sqlDB = getReadableDatabase();
        Cursor cursor = sqlDB.rawQuery(sql,null);
        if (cursor.moveToFirst())
        {
            Settings.address = cursor.getString(1);
            Settings.offsetX = cursor.getFloat(2);
            Settings.offsetY = cursor.getFloat(3);
            Settings.level = cursor.getInt(4);
            Settings.lifeValue = cursor.getInt(5);
            Settings.lifeMes = cursor.getString(6);
        }
        sql = "SELECT * FROM layer;";
        cursor = sqlDB.rawQuery(sql,null);
        if (cursor.moveToFirst())
        {
            do {
                int en = cursor.getInt(3);
                boolean enabled = false;
                if (en == 1)
                    enabled = true;
                Settings.layers.add(new Layer(
                   cursor.getString(2
                   ), cursor.getString(1), enabled, cursor.getInt(4)
                ));
            }
            while (cursor.moveToNext());
        }
    }

    public byte[] getTileImage(int x, int y, int level)
    {
        byte[] image = null;
        SQLiteDatabase sqlDB = getReadableDatabase();
        String sql = "SELECT data FROM tile WHERE level = " + level + " AND x = " + x + " AND y = " + y + ";";
        Cursor cursor = sqlDB.rawQuery(sql,null);
        if (cursor.moveToFirst())
        {
            image = Base64.decode(cursor.getBlob(0), Base64.DEFAULT);
        }
        return image;
    }

    public int getMaxId(String table)
    {
        String sql = "SELECT MAX(id) FROM " + table + ";";
        SQLiteDatabase sqlDB = getReadableDatabase();
        Cursor cursor = sqlDB.rawQuery(sql,null);
        if (cursor.moveToFirst())
            return cursor.getInt(0);
        return 0;
    }

    public void addTile(int x, int y, int level, String image, long life)
    {
        SQLiteDatabase sqlDB = getWritableDatabase();
        int id = getMaxId("tile") + 1;
        String sql = "INSERT INTO tile VALUES ("+id+","+level+","+x+","+y+",'"+image.substring(2,image.length()-3)+"',"+life+");";
        sqlDB.execSQL(sql);
    }

    public void deleteTile()
    {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase sqlDB = getWritableDatabase();
                String sql = "DELETE FROM tile WHERE life <= " + System.currentTimeMillis() / 1000L + ";";
                sqlDB.execSQL(sql);
            }
        };
        Thread t = new Thread(runnable);
        t.start();
    }

    public void clearCache()
    {
        SQLiteDatabase sqlDB = getWritableDatabase();
        String sql = "DELETE FROM tile;";
        sqlDB.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}

