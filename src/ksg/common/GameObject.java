package ksg.common;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import ksg.lab3.ShaderLocations;
import ksg.lab3.ShaderTool;
import ksg.common.Vector3;

/**
 @author MK
 */

// Prosta klasa nadrzędna dla obiektów dodawanych do sceny.
public class GameObject
{
    protected Vector3 position; // położenie obiektu w świecie
    protected ShaderLocations shaders=null; // shadery podpięte do obiektu
    
    public GameObject()
    {
        setPosition(0, 0, 0);
    }
    
    public void setPosition(Vector3 position)
    {
        this.position = position;
    }
    
    public void setPosition(float x, float y, float z)
    {
        setPosition( new Vector3(x, y, z) );
    }
    
    public Vector3 getPosition()
    {
        return position;
    }
    
    public void move(Vector3 offset)
    {
        position.add(offset);
    }
    
    public void move(float x, float y, float z)
    {
        position.add( new Vector3(x, y, z) );
    }
    
    public void update(GL2 gl)
    {
    }
    
    public void draw(GL2 gl)
    {
    }
    
    public void freeMemory(GL2 gl)
    {
    }
    
    // kompilacja shaderów
    public void createShaders(GL2 gl, String vertexShaderPath, String fragmentShaderPath)
    {
        shaders = new ShaderTool().createShaders(gl, vertexShaderPath, fragmentShaderPath);
        
        int c;
        if ((c = gl.glGetError()) != GL2.GL_NO_ERROR)
        {
            System.out.println(new GLU().gluErrorString(c));
        }
    }
}
