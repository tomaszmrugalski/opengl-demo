package ksg.lab2;

import ksg.common.Vector3;

/**
 @author MK
 */

// Prosta klasa reprezentująca warunki atmosferyczne panujące na scenie.
public class Weather
{
    static protected Vector3 wind = new Vector3(0,0,0); // prędkość wiatru w trzech wymiarach

    static public Vector3 getWind()
    {
        return wind;
    }
    
    static public void changeWindSpeed(float x, float y, float z)
    {
        wind.x += x;
        wind.y += y;
        wind.z += z;
    }
    
    static public void changeWindSpeed(Vector3 value)
    {
        changeWindSpeed(value.x, value.y, value.z);
    }
}
