package ksg.common;

/**
 @author MK
 */

// Klasa pomocnicza reprezentująca kolor.
public class Colour
{
    public float red;
    public float green;
    public float blue;
    public float alpha;
    
    public Colour(float red, float green, float blue, float alpha)
    {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }
    
    public Colour (Colour col)
    {
        red = col.red;
        green = col.green;
        blue = col.blue;
        alpha = col.alpha;
    }
    
    public Colour()
    {
        this(0, 0, 0, 0);
    }
    
    public void set(Colour col)
    {
        red = col.red;
        green = col.green;
        blue = col.blue;
        alpha = col.alpha;
    }
    
    public float[] toFloatArray()
    {
        float[] vals = new float[4];
        vals[0] = red;
        vals[1] = green;
        vals[2] = blue;
        vals[3] = alpha;
        return vals;
    }
}
