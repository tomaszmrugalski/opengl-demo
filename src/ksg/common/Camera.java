package ksg.common;

import com.jogamp.opengl.GL2;

/**
 @author MK
 */

// Klasa odpowiedzialna za położenie i kąt patrzenia obserwatora sceny.
public class Camera extends GameObject
{
    protected Vector3 target;
    protected Vector3 rotation;
    protected float previousRotationY;
    protected float viewRadius=1;
    public float yUp=1, zUp=0;
    public float nearClipping = 1.0f;
    public float farClipping = 10000.0f;
    
    public Camera()
    {
        super();
        rotation = new Vector3(270,90.1f,0);
        previousRotationY = rotation.y;
        target = new Vector3(0,0,0);
        updateTarget();
        //seTarget(0, -1000, 0);
    }

    public Vector3 getTarget()
    {
        return target;
    }
    
    protected float fixDegrees(float value)
    {
        while (value < 0.f)
        {
            value += 360.f;
        }
        while (value > 360.f)
        {
            value -= 360.f;
        }
        return value;
    }
    
    protected float fixDegreesAsRadians(float value)
    {
        return (float) Math.toRadians(fixDegrees(value));
    }
    
    public void rotate(float x, float y, float z)
    {
        rotation.x += x;
        rotation.y += y;
        rotation.z += z;
        
        rotation.x = fixDegrees(rotation.x);
        rotation.y = fixDegrees(rotation.y);
        rotation.z = fixDegrees(rotation.z);
        
        if ( (rotation.y>0 && rotation.y<180) )
        {
            yUp = 1;
            zUp = 0;
        }
        else
        {
            yUp = -1;
            zUp = 0;
        }
        if (rotation.y == 0.f)
        {
            //rotation.y = 0.1f;
            yUp = 0;
            zUp = 1;
        }
        else if (rotation.y == 180.f)
        {
            //rotation.y = 180.1f;
            yUp = 0;
            zUp = 1;
        }
        previousRotationY = rotation.y;
        
        updateTarget();
    }
    
    public void rotate(Vector3 rotation)
    {
        rotate(rotation.x, rotation.y, rotation.z);
    }
    
    @Override
    public void move(float x, float y, float z)
    {
        super.move(x, y, z);
        updateTarget();
    }
    
    public void moveForward(float distance)
    {
        move(distance*(float)Math.cos(Math.toRadians(rotation.x)), ((target.y-position.y)/viewRadius)*distance, distance*(float)Math.sin(Math.toRadians(rotation.x)));
    }
    
    public void moveBackward(float distance)
    {
        move(distance*(float)Math.cos(fixDegreesAsRadians(rotation.x+180.f)), -((target.y-position.y)/viewRadius)*distance, distance*(float)Math.sin(fixDegreesAsRadians(rotation.x+180.f)));
    }
    
    public void moveLeft(float distance)
    {
        move(distance*(float)Math.cos(fixDegreesAsRadians(rotation.x-90.f)), 0.f, distance*(float)Math.sin(fixDegreesAsRadians(rotation.x-90.f)));
    }
    
    public void moveRight(float distance)
    {
        move(distance*(float)Math.cos(fixDegreesAsRadians(rotation.x+90.f)), 0.f, distance*(float)Math.sin(fixDegreesAsRadians(rotation.x+90.f)));
    }
    
    @Override
    public void update(GL2 gl)
    {
        updateTarget();
    }
    
    public void updateTarget()
    {
        target.z = (float) (position.z + viewRadius * Math.sin( Math.toRadians(rotation.x) ) * Math.sin( Math.toRadians(rotation.y) ));
        target.x = (float) (position.x + viewRadius * Math.cos( Math.toRadians(rotation.x) ) * Math.sin( Math.toRadians(rotation.y) ));
        target.y = (float) (position.y + viewRadius * Math.cos( Math.toRadians(rotation.y) ));
    }
    
}
