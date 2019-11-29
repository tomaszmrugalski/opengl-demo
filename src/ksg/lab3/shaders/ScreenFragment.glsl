
uniform sampler2D texture;
uniform float screenWidth;
uniform float screenHeight;
uniform float phase;

varying vec2 textureCoordinates;

void main(void)
{
    vec2 posPos = textureCoordinates;

    // This changes the x offset based on y and a phase
    // the /100 at the end determines amplitude of the wave
    // the 20 determines how many waves are there vertically on screen
    posPos.x += sin(posPos.y * 20.0 + phase) / 100.0;

    // TASK 3.3: Make the world more orange
    gl_FragColor = texture2D(texture, posPos);
    gl_FragColor.r += 0.3;
    gl_FragColor.g += 0.3;
}
