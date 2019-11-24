
package ksg.lab1;

import com.jogamp.common.nio.Buffers;
import ksg.common.GameObject;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.texture.Texture;
import java.awt.image.BufferedImage;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import ksg.common.Vector3;
import java.util.Random;

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
        int cols = 128, rows = 128;

        Random r = new Random();

        System.out.printf("Image type is %d\n", displacementMap.getType());

        // Stworzenie bufora przechowującego informacje o położeniu wierzchołków (3 składowe: x,y,z).
        //float[] vertexArray = {-500, 0, -500,
        //                        500, 0, -500,
        //                       -500, 0,  500,
        //                        500, 0,  500};

        int imgWidth  = displacementMap.getWidth();
        int imgHeight = displacementMap.getHeight();

        int maxElev = 80;

        float[] vertexArray = new float[cols*rows*3];
        for (int y=0; y<rows; y++) {
            for (int x=0; x<cols; x++) {
                int offset = 3*(y*cols + x);

                // x*imgWidth/cols will change between 0..imgWidth
                int col = displacementMap.getRGB(x*imgWidth/cols, y*imgHeight/rows);
                // col is returned in ffrrggbb syntax. The displacement is grayscale, so we simply use B (lower 8bits)
                col = col & 0xff;

                int height = col * maxElev / 255; // Elevation taken from bitmap
                // int height = r.nextInt(10); // Random elevation
                // int height = (int)(maxElev*Math.cos(Math.sqrt(0.01*(x*x + y*y)))); // Sine waves elevation

                vertexArray[offset] = -500 + x*1000/128;
                vertexArray[offset + 1] = height;
                vertexArray[offset + 2] = -500 + y*1000/128;
            }
        }


        vertices = Buffers.newDirectFloatBuffer(vertexArray.length);
        vertices.put(vertexArray);
        vertices.flip();

        // Stworzenie bufora przechowującego informacje o mapowaniu tekstury (2 składowe: u,v).
        //float[] texCoordArray = {0f, 0f,
        //                         1f, 0f,
        //                         1f, 1f,
        //                         0f, 1f};
        float[] texCoordArray = new float[cols*rows*2];
        for (int y=0; y<rows; y++) {
            for (int x=0; x<cols; x++) {
                int offset = 2*(y*cols + x);
                texCoordArray[offset]     = 1.0f*x/cols;
                texCoordArray[offset + 1] = 1.0f*y/rows;
            }
        }
        texCoords = Buffers.newDirectFloatBuffer(texCoordArray.length);
        texCoords.put(texCoordArray);
        texCoords.flip();

        // Stworzenie bufora przechowującego normalne wierzchołków (3 składowe: x,y,z).
//        float[] normalArray = {0, 0, 1,
//                               0, 0, 1,
//                               0, 0, 1,
//                               0, 0, 1};
        float[] normalArray = new float[3*rows*cols];
        for (int y=0; y<rows; y++) {
            for (int x=0; x<cols; x++) {
                int offset = 3*(y*cols + x);

                // TODO: Calculate normals properly
                normalArray[offset]     = r.nextInt(100)/100.0f;
                normalArray[offset + 1] = r.nextInt(100)/100.0f;
                normalArray[offset + 2] = r.nextInt(100)/100.0f;
            }
        }
        normals = Buffers.newDirectFloatBuffer(normalArray.length);
        normals.put(normalArray);
        normals.flip();
        
        // Stworzenie bufora opisującego sposób budowania trójkątów na podstawie indeksów wierzchołków.
        //   0---1
        //   |  /|
        //   | / |
        //   |/  |
        //   2---3
        //int[] indexArray = {0,2,1, 1,2,3};
        int[] indexArray = new int[(cols-1)*(rows-1)*6];
        int iter = 0;
        for (int y=0; y < rows-1; y++) {
            for (int x=0; x<cols-1; x++) {
                int offset = y*cols + x;
                indexArray[iter] = (short)(offset + 0);
                indexArray[iter + 1] = (short)(offset + cols);
                indexArray[iter + 2] = (short)(offset + 1);
                indexArray[iter + 3] = (short)(offset + 1);
                indexArray[iter + 4] = (short)(offset + cols);
                indexArray[iter + 5] = (short)(offset + cols+1);
                iter += 6;
            }
        }

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
