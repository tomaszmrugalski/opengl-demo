
uniform sampler2D texture;
uniform float screenWidth;
uniform float screenHeight;
uniform float phase;

varying vec2 textureCoordinates;

void main(void)
{
    vec2 posPos = textureCoordinates;

    // This changes the x offset based on y and a phase
    // the /200 at the end determines amplitude of the wave
    // the 16*3.14159 determines how many waves are there vertically on screen
    textureCoordinates.x += sin(textureCoordinates.y * 16*3.14159 + phase) / 200;

    // TASK 3.3: Make the world more orange
    gl_FragColor = texture2D(texture, textureCoordinates);
    gl_FragColor.r += 0.3;
    gl_FragColor.g += 0.3;
}
