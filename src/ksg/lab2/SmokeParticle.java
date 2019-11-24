package ksg.lab2;

import com.jogamp.opengl.util.texture.Texture;
import java.util.Random;
import com.jogamp.opengl.GL2;
import ksg.common.Colour;
import ksg.common.Vector3;
import ksg.common.GameObject;
import ksg.lab2.Weather;

/**
 @author MK, changes TM
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

    static final protected float delta = 0.5f;

    static Random rand;

    public SmokeParticle(Vector3 position, Texture texture, Colour colour)
    {
        super();

        if (rand == null) {
            rand = new Random();
        }
        setPosition(position);
        this.texture = texture;
        setColour( new Colour(colour) );

        isFadingAway = false;
    }
    
    public void reset() {

        // TASK 2.1b: particles should slowly drift sideways when moving up (using random x,z components for that)
        float delta_x = rand.nextFloat()*delta - delta/2;
        float delta_y = 1.5f; // moving upwards at constant speed
        float delta_z = rand.nextFloat()*delta - delta/2;
        moveSpeed = new Vector3(delta_x, delta_y, delta_z);
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
        // TASK 2.1a - slow disappearance (we're using energy as alpha)
        gl.glColor4f(colour.red, colour.green, colour.blue, energy); // ustawienie odpowiedniego koloru
        
        if (!isFadingAway)
        {
            energy += fadeInSpeed * Global.timeScaler;
            if (energy > 1f)
            {
                energy = 1f;
                isFadingAway = true;
            }
        }
        else
        {
            energy -= fadeOutSpeed * Global.timeScaler;
        }
        
        // TASK 2.2c: Add irregularity to the movement
        moveSpeed.x += (rand.nextFloat() - 0.5f)*0.1;
        moveSpeed.z += (rand.nextFloat() - 0.5f)*0.1;

        // TASK 2.3: Add wind dependency
        Vector3 wind = Weather.getWind();
        moveSpeed.x += wind.x*0.01f;
        moveSpeed.y += wind.y*0.01f;
        moveSpeed.z += wind.z*0.01f;

        // TASK 2.5: Make the smoke movement speed independent of the number of particles
        Vector3 currentMovement = new Vector3(moveSpeed);
        currentMovement.x *= Global.timeScaler;
        currentMovement.y *= Global.timeScaler;
        currentMovement.z *= Global.timeScaler;

        move(currentMovement);
    }
    
}
