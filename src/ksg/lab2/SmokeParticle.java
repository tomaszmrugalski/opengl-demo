package ksg.lab2;

import com.jogamp.opengl.util.texture.Texture;
import java.util.Random;
import com.jogamp.opengl.GL2;
import ksg.common.Colour;
import ksg.common.Vector3;
import ksg.common.GameObject;

/**
 @author MK
 */

// Klasa reprezentująca pojedynczą cząsteczkę dymu.
public class SmokeParticle extends GameObject
{
    protected Texture texture;
    protected Colour colour;
    protected float size = 60f;
    protected Vector3 moveSpeed; // prędkość ruchu cząsteczki
    protected float energy=1.0f; // malejąca energia cząsteczki
    protected float fadeOutSpeed=0.004f, fadeInSpeed=0.04f; // prędkość pojawiania się i znikania cząstaczki
    protected boolean isFadingAway; // informacja, czy cząsteczka już znika, czy dopiero się pojawia
    
    public SmokeParticle(Vector3 position, Texture texture, Colour colour)
    {
        super();
        setPosition(position);
        this.texture = texture;
        setColour( new Colour(colour) );
        moveSpeed = new Vector3(0.f, 1.5f, 0.f);
        isFadingAway = false;
    }
    
    public SmokeParticle(Vector3 position, Texture texture)
    {
        this(position, texture, new Colour(1,1,1,1));
    }
    
    void setColour(Colour colour)
    {
        this.colour = colour;
    }
    
    Colour getColour()
    {
        return this.colour;
    }
    
    @Override
    public void update(GL2 gl) // aktualizacja niektórych parametrów cząsteczki
    {
        gl.glColor4f(colour.red, colour.green, colour.blue, colour.alpha); // ustawienie odpowiedniego koloru
        
        if (!isFadingAway)
        {
            energy += fadeInSpeed;
            if (energy > 1f)
            {
                energy = 1f;
                isFadingAway = true;
            }
        }
        else
        {
            energy -= fadeOutSpeed;
        }
        
        colour.alpha = 0.4f;
        move(moveSpeed);
    }
    
}
