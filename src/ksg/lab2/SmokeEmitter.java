package ksg.lab2;

import com.jogamp.opengl.util.texture.Texture;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Random;
import com.jogamp.opengl.GL2;
import ksg.common.Colour;
import ksg.common.Vector3;
import ksg.common.GameObject;

/**
 @author MK
 */

// Klasa realizująca symulację dymu metodą systemu cząsteczkowego.
public class SmokeEmitter extends GameObject
{
    protected ArrayList<SmokeParticle> particles; // kolekcja cząsteczek dymu
    protected Texture texture; // wspólna tekstura dla wszystkich cząsteczek
    protected float radius; // maksymalna odległość, o jaką nowa cząsteczka może być oddalona od źródła dymu
    protected boolean texturedMode; // tryb rysowania
    protected float[] modelView; // macierz transformacji modelu
    protected FloatBuffer modelViewBuffer; // bufor przechowujący macierz modelu
    
    public SmokeEmitter(Vector3 position, float radius, int numberOfParticles, Texture texture)
    {
        this.position = position;
        this.radius = radius;
        this.texture = texture;
        particles = new ArrayList<>();
        texturedMode = true;
        
        // Inicjalizacja cząsteczek z użyciem losowych wartości początkowych.
        double angle;
        Random rand = new Random();
        for (int i=0; i<numberOfParticles; i++)
        {
            angle = ((double)i / (numberOfParticles)) * 2.0 * Math.PI;
            Vector3 pos = generateRandomPosition(angle, rand);
            SmokeParticle particle = new SmokeParticle(new Vector3(pos.x, pos.y, pos.z), texture, new Colour(1.0f, 0.7f, 0.0f, 1.0f));
            resetParticle(particle, rand);
            particle.colour.red = particle.colour.green = particle.colour.blue = 0.5f;
            particles.add(particle);
        }
        
        // Inicjalizacja pomocniczej macierzy transformacji.
        modelView = new float[16];
        modelViewBuffer = FloatBuffer.allocate(modelView.length);
    }
    
    // Metoda pomocnicza do generowania współrzędnych początkowych o losowej odległości od źródła dymu.
    protected Vector3 generateRandomPosition(double angle, Random rand)
    {
        double modifiedRadius = radius * rand.nextDouble();
        Vector3 pos = new Vector3(position.x, position.y, position.z);
        pos.x += modifiedRadius * Math.cos(angle);
        pos.z += modifiedRadius * Math.sin(angle);
        return pos;
    }
    
    // Metoda pomocnicza ustawiająca początkowe wartości niektórych zmiennych danej cząsteczki.
    protected void resetParticle(SmokeParticle particle, Random rand)
    {
        particle.energy = rand.nextFloat();
        particle.isFadingAway = rand.nextBoolean();
        particle.reset();
    }
    
    // Zmiana trybu rysowania.
    public void setTexturedMode(boolean useTextures)
    {
        texturedMode = useTextures;
    }

    @Override
    public void update(GL2 gl)
    {
        Random rand = new Random();
        for (int i=0; i<particles.size(); i++)
        {
            if (particles.get(i).energy <= 0 && particles.get(i).isFadingAway)
            {
                // Odnowienie cząsteczek.
                double angle = ((double)i / (particles.size())) * 2.0 * Math.PI;
                Vector3 pos = generateRandomPosition(angle, rand);
                resetParticle(particles.get(i), rand);
                particles.get(i).setPosition(pos.x, pos.y, pos.z);
                particles.get(i).isFadingAway = false;
            }
        }
    }
    
    @Override
    public void draw(GL2 gl)
    {
        update(gl);
        
        if (texturedMode)
        {
            texture.enable(gl);
            texture.bind(gl);
        }
        
        // Zmiana wartości poszczególnych atrybutów i odłożenie starych wartości na stos.
        gl.glPushAttrib(GL2.GL_BLEND);
        gl.glEnable(GL2.GL_BLEND);
        gl.glPushAttrib(GL2.GL_DEPTH_TEST);
        gl.glDepthMask(false);
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
        gl.glPushAttrib(GL2.GL_LIGHTING);
        gl.glDisable(GL2.GL_LIGHTING);
        
        for (SmokeParticle particle : particles)
        {
            particle.update(gl);

            gl.glPushMatrix(); // odłożenie aktualnego stanu macierzy transformacji na stos

            gl.glTranslatef(particle.getPosition().x, particle.getPosition().y, -particle.getPosition().z); // zmiana położenia
            
            gl.glGetFloatv(GL2.GL_MODELVIEW_MATRIX, modelView, 0); // pobranie wartości macierzy transformacji

            // Obrócenie cząsteczki w stronę kamery poprzez zresetowanie niektórych wartości w macierzy transformacji
            // (podmacierz zawierająca informacje o obrocie i skali obiektu zostaje zostąpiona macierzą jednostkową).
            for (int i=0; i<3; i++) 
            {
                for (int j=0; j<3; j++)
                {
                    if (i == j)
                    {
                        modelView[i*4+j] = 1.0f;
                    }
                    else
                    {
                        modelView[i*4+j] = 0.0f;
                    }
                }
            }

            modelViewBuffer.put(modelView).rewind();
            gl.glLoadMatrixf(modelViewBuffer); // zastąpienie macierzy transformacji
            
            // Zdefiniowanie powierzchni modelu cząsteczki.
            gl.glBegin(GL2.GL_QUADS); // tryb rysowania czworościanów
            gl.glTexCoord2f(0, 0);   gl.glVertex3f(-particle.size/2, -particle.size/2, 0);
            gl.glTexCoord2f(1, 0);   gl.glVertex3f(particle.size/2,  -particle.size/2, 0);
            gl.glTexCoord2f(1, 1);   gl.glVertex3f(particle.size/2,  particle.size/2,  0);
            gl.glTexCoord2f(0, 1);   gl.glVertex3f(-particle.size/2, particle.size/2,  0);
            gl.glEnd();

            gl.glPopMatrix(); // przywrócenie poprzedniego stanu macierzy transformacji
        }
        
        // Przywrócenie poprzednich wartości poszczególnych atrybutów.
        gl.glPopAttrib(); // GL_LIGHTING
        gl.glPopAttrib(); // GL_DEPTH_TEST
        gl.glPopAttrib(); // GL_BLEND
        gl.glDepthMask(true);
        
        if (texturedMode)
        {
            texture.disable(gl);
        }
    }

    public float getRadius()
    {
        return radius;
    }
    
}
