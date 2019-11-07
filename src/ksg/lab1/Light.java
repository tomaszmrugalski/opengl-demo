package ksg.lab1;

import ksg.common.GameObject;
import ksg.lab3.TexturedQuad;
import com.jogamp.opengl.GL2;
import ksg.common.Colour;

/**
 @author MK
 */

// Klasa reprezentująca światło na scenie.
public class Light extends GameObject
{
    protected TexturedQuad mesh=null;
    protected Colour diffuseColour;
    protected Colour specularColour;
    
    public Light()
    {
        super();
    }
    
    public Light(TexturedQuad mesh)
    {
        super();
        this.mesh = mesh;
    }

    @Override
    public void update(GL2 gl)
    {
        // Aktualizacja parametrów oświetlenia sceny.
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, position.toFloatArray(), 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, diffuseColour.toFloatArray(), 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, specularColour.toFloatArray(), 0);
    }

    @Override
    public void draw(GL2 gl)
    {
        update(gl);
        
        // Narysowanie modelu.
        if (mesh != null)
        {
            gl.glPushMatrix();
            gl.glTranslatef(position.x, position.y, position.z);
            mesh.draw(gl);
            gl.glPopMatrix();
        }
    }
    
    public Colour getDiffuseColour()
    {
        return diffuseColour;
    }

    public void setDiffuseColour(Colour diffuseColour)
    {
        this.diffuseColour = diffuseColour;
        if (mesh != null)
        {
            mesh.setColour(diffuseColour.red, diffuseColour.green, diffuseColour.blue);
        }
    }

    public Colour getSpecularColour()
    {
        return specularColour;
    }

    public void setSpecularColour(Colour specularColour)
    {
        this.specularColour = specularColour;
    }
    
    public TexturedQuad getMesh()
    {
        return mesh;
    }
}
