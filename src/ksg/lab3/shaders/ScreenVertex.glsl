
varying vec2 textureCoordinates;

void main(void)
{
    gl_Position = ftransform();
    gl_FrontColor = gl_Color;
    textureCoordinates = vec2(gl_MultiTexCoord0);
}
