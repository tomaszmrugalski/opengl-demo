package ksg.lab3;

import ksg.common.Vector3;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.GL2;
import ksg.common.GameObject;

/**
 @author MK
 */
public class TexturedQuad extends GameObject
{
    protected float size = 100;
    protected Texture texture;
    protected float red=1, green=1, blue=1;
    
    public TexturedQuad(Vector3 position, Texture texture)
    {
        super();
        setPosition(position);
        this.texture = texture;
    }
    
    public TexturedQuad(float x, float y, float z, Texture texture)
    {
        this( new Vector3(x,y,z), texture );
    }
    
    public TexturedQuad(Texture texture)
    {
        this(new Vector3(), texture);
    }

    @Override
    public void draw(GL2 gl) // metoda rysująca obiekt na scenie
    {
        gl.glPushMatrix(); // odłożenie aktualnego stanu macierzy transformacji na stos
        gl.glTranslatef(position.x, position.y, position.z); // ustawienie pozycji
        update(gl);
        
        if (shaders != null)
        {
            gl.glUseProgram( shaders.getShaderProgram() ); // użycie shaderów
        }
        
        // Włączenie tekstury.
        texture.enable(gl);
        texture.bind(gl);
        
        gl.glPushAttrib(GL2.GL_ALL_ATTRIB_BITS); // odłożenie aktualnego stanu atrybutów na stos
        gl.glDisable(GL2.GL_LIGHTING); // wyłączenie oświetlenia dla obiektu
        gl.glDisable(GL2.GL_FOG);
        
        // Włączenie mechanizmu "blending" w trybie koloru addytywnego.
        // Innymi słowy: im ciemniejszy kolor na teksturze, tym bardziej przejrzysty będzie model w danym miejscu.
        gl.glEnable(GL2.GL_BLEND);
        gl.glBlendFunc(GL2.GL_SRC_COLOR, GL2.GL_ONE);
        //gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
        
        // Początek definiowania powierzchni modelu.
        gl.glBegin(GL2.GL_QUADS); // tryb rysowania czworościanów
        
        // Ustawienie normalnych dla wszystkich wierzchołków.
        gl.glNormal3f(0, 0, 1);
        
        // Ustawienie kolorów wierzchołków.
        gl.glColor3f(red, green, blue);
        
        // Ustawienie współrzędnych wierzchołków na teksturze oraz w przestrzeni 3D.
        gl.glTexCoord2f(0, 0);   gl.glVertex3f(-size/2, -size/2, 0);
        gl.glTexCoord2f(1, 0);   gl.glVertex3f(size/2,  -size/2, 0);
        gl.glTexCoord2f(1, 1);   gl.glVertex3f(size/2,  size/2,  0);
        gl.glTexCoord2f(0, 1);   gl.glVertex3f(-size/2, size/2,  0);
        
        gl.glEnd(); // koniec definiowania powierzchni
        
        gl.glPopAttrib(); // przywrócenie poprzedniego stanu atrybutów
        
        texture.disable(gl); // wyłączenie tekstury
        
        gl.glUseProgram(0); // użycie domyślnego programu cieniującego
        
        gl.glPopMatrix(); // przywrócenie poprzedniego stanu macierzy transformacji modeli
    }
    
    public void setColour(float red, float green, float blue)
    {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }
    
}
