package model;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;

public class Settings {
    public static String address;
    public static float offsetX;
    public static float offsetY;
    public static int level;
    public static int lifeValue;
    public static String lifeMes;
    public static ArrayList<Layer> layers = new ArrayList<Layer>();

    public static int getLife()
    {
        switch (lifeMes)
        {
            case "s":return lifeValue;
            case "m":return lifeValue * 60;
            case "d":return lifeValue * 86400;
        }
        return lifeValue;
    }

}
