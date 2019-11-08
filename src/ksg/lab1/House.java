package ksg.lab1;

import ksg.common.GameObject;
import ksg.common.Vector3;
import com.jogamp.opengl.GL2;

/**
 @author MK
 */
public class House extends GameObject
{
    public House(Vector3 position)
    {
        super();
        setPosition(position);
    }
    
    public House(float x, float y, float z)
    {
        this( new Vector3(x,y,z) );
    }

    @Override
    public void draw(GL2 gl) // metoda rysująca obiekt na scenie
    {
        gl.glPushMatrix(); // odłożenie aktualnego stanu macierzy transformacji na stos
        gl.glTranslatef(position.x, position.y, position.z); // ustawienie pozycji
        gl.glRotatef(40, 0,1,0);
        
        gl.glPushAttrib(GL2.GL_ALL_ATTRIB_BITS); // odłożenie aktualnego stanu atrybutów na stos
        gl.glEnable(GL2.GL_COLOR_MATERIAL); // wykorzystanie kolorów wierzchołków zamiast domyślnego materiału
        
        // Początek definiowania powierzchni modelu.
        gl.glBegin(GL2.GL_QUADS); // tryb rysowania czworościanów
        
        // Ustawienie koloru brązowego.
        gl.glColor3f(0.5f, 0.2f, 0f);
        
        // Definicja normalnych i pozycji wierzchołków dla przedniej ściany.
        gl.glNormal3f(0.0f, 0.0f, 1.0f);   gl.glVertex3f(-150.0f, 0.0f,   100.0f);
        gl.glNormal3f(0.0f, 0.0f, 1.0f);   gl.glVertex3f(150.0f,  0.0f,   100.0f);
        gl.glNormal3f(0.0f, 0.0f, 1.0f);   gl.glVertex3f(150.0f,  150.0f, 100.0f);
        gl.glNormal3f(0.0f, 0.0f, 1.0f);   gl.glVertex3f(-150.0f, 150.0f, 100.0f);
        
        // Definicja normalnych i pozycji wierzchołków dla prawej ściany (przednia połowa).
        gl.glNormal3f(1.0f, 0.0f, 0.0f);   gl.glVertex3f(150.0f, 0.0f,    100.0f);
        gl.glNormal3f(1.0f, 0.0f, 0.0f);   gl.glVertex3f(150.0f, 0.0f,    0.0f);
        gl.glNormal3f(1.0f, 0.0f, 0.0f);   gl.glVertex3f(150.0f, 250.0f,  0.0f);
        gl.glNormal3f(1.0f, 0.0f, 0.0f);   gl.glVertex3f(150.0f, 150.0f,  100.0f);
        // Definicja normalnych i pozycji wierzchołków dla prawej ściany (tylna połowa).
        gl.glNormal3f(1.0f, 0.0f, 0.0f);   gl.glVertex3f(150.0f, 0.0f,    0.0f);
        gl.glNormal3f(1.0f, 0.0f, 0.0f);   gl.glVertex3f(150.0f, 0.0f,    -100.0f);
        gl.glNormal3f(1.0f, 0.0f, 0.0f);   gl.glVertex3f(150.0f, 150.0f,  -100.0f);
        gl.glNormal3f(1.0f, 0.0f, 0.0f);   gl.glVertex3f(150.0f, 250.0f,  0.0f);
        
        // Definicja normalnych i pozycji wierzchołków dla tylnej ściany.
        gl.glNormal3f(0.0f, 0.0f, -1.0f);   gl.glVertex3f(150.0f,  0.0f,   -100.0f);
        gl.glNormal3f(0.0f, 0.0f, -1.0f);   gl.glVertex3f(-150.0f, 0.0f,   -100.0f);
        gl.glNormal3f(0.0f, 0.0f, -1.0f);   gl.glVertex3f(-150.0f, 150.0f, -100.0f);
        gl.glNormal3f(0.0f, 0.0f, -1.0f);   gl.glVertex3f(150.0f,  150.0f, -100.0f);
        
        // Definicja normalnych i pozycji wierzchołków dla lewej ściany (przednia połowa).
        gl.glNormal3f(-1.0f, 0.0f, 0.0f);   gl.glVertex3f(-150.0f, 0.0f,    0.0f);
        gl.glNormal3f(-1.0f, 0.0f, 0.0f);   gl.glVertex3f(-150.0f, 0.0f,    100.0f);
        gl.glNormal3f(-1.0f, 0.0f, 0.0f);   gl.glVertex3f(-150.0f, 150.0f,  100.0f);
        gl.glNormal3f(-1.0f, 0.0f, 0.0f);   gl.glVertex3f(-150.0f, 250.0f,  0.0f);
        // Definicja normalnych i pozycji wierzchołków dla lewej ściany (tylna połowa).
        gl.glNormal3f(-1.0f, 0.0f, 0.0f);   gl.glVertex3f(-150.0f, 0.0f,    -100.0f);
        gl.glNormal3f(-1.0f, 0.0f, 0.0f);   gl.glVertex3f(-150.0f, 0.0f,    0.0f);
        gl.glNormal3f(-1.0f, 0.0f, 0.0f);   gl.glVertex3f(-150.0f, 250.0f,  0.0f);
        gl.glNormal3f(-1.0f, 0.0f, 0.0f);   gl.glVertex3f(-150.0f, 150.0f,  -100.0f);
        
        // Ustawienie koloru czerwonego.
        gl.glColor3f(0.7f, 0f, 0f);
        
        // Definicja normalnych i pozycji wierzchołków dla dachu (przednia połowa).
        gl.glNormal3f(0.0f, 0.5f, 0.5f);   gl.glVertex3f(-150.0f, 150.0f, 100.0f);
        gl.glNormal3f(0.0f, 0.5f, 0.5f);   gl.glVertex3f(150.0f,  150.0f, 100.0f);
        gl.glNormal3f(0.0f, 0.5f, 0.5f);   gl.glVertex3f(150.0f,  250.0f, 0.0f);
        gl.glNormal3f(0.0f, 0.5f, 0.5f);   gl.glVertex3f(-150.0f, 250.0f, 0.0f);
        
        // Definicja normalnych i pozycji wierzchołków dla dachu (tylna połowa).
        gl.glNormal3f(0.0f, 0.5f, -0.5f);   gl.glVertex3f(150.0f,  150.0f, -100.0f);
        gl.glNormal3f(0.0f, 0.5f, -0.5f);   gl.glVertex3f(-150.0f, 150.0f, -100.0f);
        gl.glNormal3f(0.0f, 0.5f, -0.5f);   gl.glVertex3f(-150.0f, 250.0f, 0.0f);
        gl.glNormal3f(0.0f, 0.5f, -0.5f);   gl.glVertex3f(150.0f,  250.0f, 0.0f);
        
        // -------------------------------------------------------
        // TUTAJ ZMIEŃ KOLOR RYSOWANIA I ZDEFINIUJ KSZTAŁT KOMINA
        // -------------------------------------------------------
        gl.glColor3f(0f, 1.0f, 0f);
   
        float h = 400.0f;
        float x = 50.0f;
        float y = 50.0f;
        
        gl.glNormal3f(-1.0f, 0.0f, 0.0f);   gl.glVertex3f(250.0f, 0.0f,    50.0f);
        gl.glNormal3f(-1.0f, 0.0f, 0.0f);   gl.glVertex3f(250.0f, 0.0f,   -50.0f);
        gl.glNormal3f(-1.0f, 0.0f, 0.0f);   gl.glVertex3f(250.0f, h, -50.0f);
        gl.glNormal3f(-1.0f, 0.0f, 0.0f);   gl.glVertex3f(250.0f, h,  50.0f);

        gl.glNormal3f(0.0f, 0.0f, 1.0f);   gl.glVertex3f(250.0f,  0.0f,   -50.0f);
        gl.glNormal3f(0.0f, 0.0f, 1.0f);   gl.glVertex3f(150.0f, 0.0f,   -50.0f);
        gl.glNormal3f(0.0f, 0.0f, 1.0f);   gl.glVertex3f(150.0f, h, -50.0f);
        gl.glNormal3f(0.0f, 0.0f, 1.0f);   gl.glVertex3f(250.0f,  h, -50.0f);

        gl.glNormal3f(0.0f, 0.0f, -1.0f);   gl.glVertex3f(250.0f,  0.0f,   50.0f);
        gl.glNormal3f(0.0f, 0.0f, -1.0f);   gl.glVertex3f(150.0f, 0.0f,   50.0f);
        gl.glNormal3f(0.0f, 0.0f, -1.0f);   gl.glVertex3f(150.0f, h, 50.0f);
        gl.glNormal3f(0.0f, 0.0f, -1.0f);   gl.glVertex3f(250.0f,  h, 50.0f);

        gl.glNormal3f(-1.0f, 0.0f, 0.0f);   gl.glVertex3f(150.0f, 0.0f,    50.0f);
        gl.glNormal3f(-1.0f, 0.0f, 0.0f);   gl.glVertex3f(150.0f, 0.0f,   -50.0f);
        gl.glNormal3f(-1.0f, 0.0f, 0.0f);   gl.glVertex3f(150.0f, h, -50.0f);
        gl.glNormal3f(-1.0f, 0.0f, 0.0f);   gl.glVertex3f(150.0f, h,  50.0f);

        
        gl.glEnd(); // koniec definiowania powierzchni
        
        gl.glPopAttrib(); // przywrócenie poprzedniego stanu atrybutów
        
        gl.glPopMatrix(); // przywrócenie poprzedniego stanu macierzy transformacji modeli
    }
    
}
