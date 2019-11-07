package ksg.lab3;

/**
 @author MK
 */

// Klasa kontenerowa do przechowywania adresów shaderów.
public class ShaderLocations
{
    protected int vertexShader;
    protected int fragmentShader;
    protected int shaderProgram;
    
    public ShaderLocations(int vertexShader, int fragmentShader, int shaderProgram)
    {
        this.vertexShader = vertexShader;
        this.fragmentShader = fragmentShader;
        this.shaderProgram = shaderProgram;
    }

    public int getVertexShader()
    {
        return vertexShader;
    }

    public int getFragmentShader()
    {
        return fragmentShader;
    }

    public int getShaderProgram()
    {
        return shaderProgram;
    }
}
