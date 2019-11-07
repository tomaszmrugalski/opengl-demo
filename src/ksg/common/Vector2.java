
package ksg.common;

/**
 @author MK
 */

// Klasa kontenerowa do przechowywania dwuwymiarowych współrzędnych.
public class Vector2
{
    public float x;
    public float y;
    
    public Vector2(float x, float y)
    {
        this.x = x;
        this.y = y;
    }
    
    public Vector2()
    {
        this(0, 0);
    }
    
    public void add(Vector2 pos)
    {
        x += pos.x;
        y += pos.y;
    }
}
