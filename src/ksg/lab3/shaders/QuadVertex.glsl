
varying vec2 textureCoordinates;

void main(void)
{
    mat4 modelViewMatrix = gl_ModelViewMatrix;

    // TASK 3.1: Make sure the light is always facing the camera
    modelViewMatrix[0][0] = 1.0f;
    modelViewMatrix[0][1] = 0.0f;
    modelViewMatrix[0][2] = 0.0f;

    modelViewMatrix[1][0] = 0.0f;
    modelViewMatrix[1][1] = 1.0f;
    modelViewMatrix[1][2] = 0.0f;

    modelViewMatrix[2][0] = 0.0f;
    modelViewMatrix[2][1] = 0.0f;
    modelViewMatrix[2][2] = 1.0f;

    gl_Position = gl_ProjectionMatrix * modelViewMatrix * gl_Vertex;

    textureCoordinates = vec2(gl_MultiTexCoord0);
}
