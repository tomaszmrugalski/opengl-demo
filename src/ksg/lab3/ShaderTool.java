package ksg.lab3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;

// Klasa pomocnicza odpowiedzialna za kompilowanie i linkowanie shaderów.
public class ShaderTool
{
    static protected String readFromStream(InputStream ins)
    {
        BufferedReader br = new BufferedReader( new InputStreamReader(ins) );
        StringBuilder buffer = new StringBuilder();
        String s;
        try
        {
            while ( (s = br.readLine()) != null )
            {
                buffer.append(s+"\n");
            }
            br.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        
        //System.out.println(buffer.toString());
        return buffer.toString();
    }

    public ShaderLocations createShaders(GL2 gl, String vertexShaderPath, String fragmentShaderPath)
    {
        int vertexShader = gl.glCreateShader(GL2.GL_VERTEX_SHADER);
        int fragmentShader = gl.glCreateShader(GL2.GL_FRAGMENT_SHADER);
        String vsrc = readFromStream(getClass().getResourceAsStream(vertexShaderPath));
        gl.glShaderSource(vertexShader, 1, new String[]
        {
            vsrc
        }, (int[]) null, 0);
        gl.glCompileShader(vertexShader);
        String fsrc = readFromStream(getClass().getResourceAsStream(fragmentShaderPath));
        gl.glShaderSource(fragmentShader, 1, new String[]
        {
            fsrc
        }, (int[]) null, 0);
        gl.glCompileShader(fragmentShader);
        int shaderProgram = gl.glCreateProgram();
        gl.glAttachShader(shaderProgram, vertexShader);
        gl.glAttachShader(shaderProgram, fragmentShader);
        gl.glLinkProgram(shaderProgram);
        gl.glValidateProgram(shaderProgram);
        //gl.glUseProgram(shaderProgram);
        GLU glu = new GLU();
        int c;
        if ((c = gl.glGetError()) != GL2.GL_NO_ERROR)
        {
            System.out.println(glu.gluErrorString(c));
        }
        //timeUniform = gl.glGetUniformLocation(shaderprogram, "time");
        
        //int[] result = {vertexShader, fragmentShader, shaderProgram};
        return new ShaderLocations(vertexShader, fragmentShader, shaderProgram);
    }
}
