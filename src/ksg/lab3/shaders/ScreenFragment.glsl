
uniform sampler2D texture;
uniform float screenWidth;
uniform float screenHeight;

varying vec2 textureCoordinates;

void main(void)
{
    vec2 posPos = textureCoordinates;

    gl_FragColor = texture2D(texture, posPos);
    gl_FragColor.r += 0.3f;
    gl_FragColor.g += 0.3f;
}
