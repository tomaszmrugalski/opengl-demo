
varying vec2 textureCoordinates;

void main(void)
{
    mat4 modelViewMatrix = gl_ModelViewMatrix;

    gl_Position = gl_ProjectionMatrix * modelViewMatrix * gl_Vertex;

    textureCoordinates = vec2(gl_MultiTexCoord0);
}
