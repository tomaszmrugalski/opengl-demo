package ksg.lab1;

import ksg.common.GameObject;
import ksg.common.Vector3;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.GL2;

/**
 @author MK
 */

// Klasa reprezentująca model terenu otrzymanego za pomocą przestarzałych sposobów.
public class GroundRetro extends GameObject
{
    protected float size = 1000;
    protected Texture texture;
    protected float red=1, green=1, blue=1;
    
    public GroundRetro(Vector3 position, Texture texture)
    {
        super();
        setPosition(position);
        this.texture = texture;
    }
    
    public GroundRetro(float x, float y, float z, Texture texture)
    {
        this( new Vector3(x,y,z), texture );
    }
    
    public GroundRetro(Texture texture)
    {
        this(new Vector3(), texture);
    }

    @Override
    public void draw(GL2 gl) // metoda rysująca obiekt na scenie
    {
        gl.glPushMatrix(); // odłożenie aktualnego stanu macierzy transformacji na stos
        gl.glTranslatef(position.x, position.y, position.z); // ustawienie pozycji
        update(gl);
        
        // Włączenie tekstury.
        texture.enable(gl);
        texture.bind(gl);
        
        // Początek definiowania powierzchni modelu.
        gl.glBegin(GL2.GL_QUADS); // tryb rysowania czworościanów
        
        // Ustawienie normalnych dla wszystkich wierzchołków.
        gl.glNormal3f(0, 1, 0);
        
        // Ustawienie kolorów wierzchołków.
        gl.glColor3f(red, green, blue);
        
        // Ustawienie współrzędnych wierzchołków na teksturze oraz w przestrzeni 3D.
        gl.glTexCoord2f(0, 0);   gl.glVertex3f(-size/2, 0, -size/2);
        gl.glTexCoord2f(1, 0);   gl.glVertex3f( size/2, 0, -size/2);
        gl.glTexCoord2f(1, 1);   gl.glVertex3f( size/2, 0,  size/2);
        gl.glTexCoord2f(0, 1);   gl.glVertex3f(-size/2, 0,  size/2);
        
        gl.glEnd(); // koniec definiowania powierzchni
        
        texture.disable(gl); // wyłączenie tekstury
        
        gl.glPopMatrix(); // przywrócenie poprzedniego stanu macierzy transformacji modeli
    }
    
}
