
uniform sampler2D texture;

varying vec2 textureCoordinates;

void main(void)
{
    gl_FragColor = texture2D(texture, textureCoordinates);

    // TASK 3.1: turn Sol into red giant
    gl_FragColor.r *= 3.0;
}
