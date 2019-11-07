package ksg.common;

/**
 @author MK
 */

// Klasa kontenerowa do przechowywania czterowymiarowych współrzędnych.
public class Vector4
{
    public float x;
    public float y;
    public float z;
    public float w;
    
    public Vector4(float x, float y, float z, float w)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }
    
    public Vector4()
    {
        this(0, 0, 0, 0);
    }
    
    public void add(Vector4 pos)
    {
        x += pos.x;
        y += pos.y;
        z += pos.z;
        w += pos.w;
    }
    
    public float[] toFloatArray()
    {
        float[] vals = new float[4];
        vals[0] = x;
        vals[1] = y;
        vals[2] = z;
        vals[3] = 0;
        return vals;
    }
}
