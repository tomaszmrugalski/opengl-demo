
package ksg.lab1;

import com.jogamp.common.nio.Buffers;
import ksg.common.GameObject;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.texture.Texture;
import java.awt.image.BufferedImage;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import ksg.common.Vector3;

/**
 *
 * @author MK
 */

// Klasa reprezentująca model terenu otrzymanego za pomocą VBO.
public class GroundVbo extends GameObject
{
    int[] buffers;
    protected FloatBuffer vertices, texCoords, normals;
    protected IntBuffer indices;
    protected int verticesHandle, texCoordsHandle, normalsHandle, indicesHandle;
    protected Texture texture=null;
    protected int diffuseTextureHandle, displacementTextureHandle;
    protected BufferedImage displacementMap;
    
    public GroundVbo(Vector3 position, Texture texture, BufferedImage displacementMap)
    {
        super();
        setPosition(position);
        this.texture = texture;
        this.displacementMap = displacementMap;
    }
    
    public GroundVbo(float x, float y, float z, Texture texture, BufferedImage displacementMap)
    {
        this( new Vector3(x,y,z), texture, displacementMap );
    }
    
    public void generateBuffers(GL2 gl)
    {
        // Stworzenie bufora przechowującego informacje o położeniu wierzchołków (3 składowe: x,y,z).
        float[] vertexArray = {-500, 0, -500,
                                500, 0, -500,
                               -500, 0,  500,
                                500, 0,  500};
        vertices = Buffers.newDirectFloatBuffer(vertexArray.length);
        vertices.put(vertexArray);
        vertices.flip();
        
        // Stworzenie bufora przechowującego informacje o mapowaniu tekstury (2 składowe: u,v).
        float[] texCoordArray = {0f, 0f,
                                 1f, 0f,
                                 1f, 1f,
                                 0f, 1f};
        texCoords = Buffers.newDirectFloatBuffer(texCoordArray.length);
        texCoords.put(texCoordArray);
        texCoords.flip();

        // Stworzenie bufora przechowującego normalne wierzchołków (3 składowe: x,y,z).
        float[] normalArray = {0, 0, 1,
                               0, 0, 1,
                               0, 0, 1,
                               0, 0, 1};
        normals = Buffers.newDirectFloatBuffer(normalArray.length);
        normals.put(normalArray);
        normals.flip();
        
        // Stworzenie bufora opisującego sposób budowania trójkątów na podstawie indeksów wierzchołków.
        //   0---1
        //   |  /|
        //   | / |
        //   |/  |
        //   2---3
        int[] indexArray = {0,2,1, 1,2,3};
        indices = Buffers.newDirectIntBuffer(indexArray.length);
        indices.put(indexArray);
        indices.flip();

        buffers = new int[4]; // "uchwyty" buforów przechowujących informacje o modelu
        gl.glGenBuffers(buffers.length, buffers, 0);

        // Podpięcie bufora pozycji wierzchołków.
        verticesHandle = buffers[0];
        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, verticesHandle);
        gl.glBufferData(GL2.GL_ARRAY_BUFFER, vertices.capacity() * Buffers.SIZEOF_FLOAT,
                            vertices, GL2.GL_STATIC_DRAW);
        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);

        // Podpięcie bufora mapowania tekstury.
        texCoordsHandle = buffers[1];
        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, texCoordsHandle);
        gl.glBufferData(GL2.GL_ARRAY_BUFFER, texCoords.capacity() * Buffers.SIZEOF_FLOAT,
                            texCoords, GL2.GL_STATIC_DRAW);
        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);
        
        // Podpięcie bufora normalnych.
        normalsHandle = buffers[2];
        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, normalsHandle);
        gl.glBufferData(GL2.GL_ARRAY_BUFFER, normals.capacity() * Buffers.SIZEOF_FLOAT,
                            normals, GL2.GL_STATIC_DRAW);
        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);
        
        // Podpięcie bufora indeksów.
        indicesHandle = buffers[3];
        gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, indicesHandle);
        gl.glBufferData(GL2.GL_ELEMENT_ARRAY_BUFFER, indices.capacity() * Buffers.SIZEOF_INT,
                            indices, GL2.GL_STATIC_DRAW);
        gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, 0);
    }
    
    @Override
    public void draw(GL2 gl)
    {
        gl.glPushMatrix(); // odłożenie aktualnego stanu macierzy transformacji na stos
        gl.glTranslatef(position.x, position.y, position.z); // ustawienie pozycji
        
        // Włączenie tekstury.
        gl.glEnable(texture.getTarget());
        gl.glActiveTexture(gl.GL_TEXTURE0);
        gl.glBindTexture(texture.getTarget(), texture.getTextureObject());
        
        // Włączenie poszczególnych buforów.
        gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
        gl.glEnableClientState(GL2.GL_NORMAL_ARRAY);

        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, texCoordsHandle);
        gl.glTexCoordPointer(2, GL2.GL_FLOAT, 0, 0);
        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, normalsHandle);
        gl.glNormalPointer(GL2.GL_FLOAT, 0, 0);
        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, verticesHandle);
        gl.glVertexPointer(3, GL2.GL_FLOAT, 0, 0);
        
        gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, indicesHandle);
        gl.glDrawElements(GL2.GL_TRIANGLES, indices.capacity(), GL2.GL_UNSIGNED_INT, 0); // narysowanie modelu

        // Wyłączenie buforów.
        gl.glDisableClientState(GL2.GL_NORMAL_ARRAY);
        gl.glDisableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
        gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
        
        gl.glDisable(texture.getTarget()); // wyłączenie tekstury
        
        gl.glUseProgram(0); // użycie domyślnego programu cieniującego
        
        gl.glPopMatrix(); // przywrócenie poprzedniego stanu macierzy transformacji modeli
    }
    
    @Override
    public void freeMemory(GL2 gl)
    {
        gl.glDeleteBuffers(buffers.length, buffers, 0);
    }
    
}
