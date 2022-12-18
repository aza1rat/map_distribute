package model;

public class Layer {
    public String name;
    public boolean isEnabled;
    public int color;
    public String localName;

    public Layer(String name, String localName, boolean isEnabled, int color)
    {
        this.name = name;
        this.localName = localName;
        this.isEnabled = isEnabled;
        this.color = color;
    }
}
