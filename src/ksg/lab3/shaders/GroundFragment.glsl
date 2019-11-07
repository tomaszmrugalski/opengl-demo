
uniform sampler2D texture;

varying vec3 vNormal;
varying vec2 textureCoordinates;

void main(void)
{
    // Znormalizowanie kierunku swiatla.
    // Kierunek swiatla jest wyznaczany na podstawie jego polozenia.
    vec3 lightDir = normalize(vec3(gl_LightSource[0].position));

    // Wyznaczenie cosinusa kata pomiedzy wektorem normalnym a kierunkiem swiatla.
    // Cosinus jest rowny iloczynowi skalarnemu powyzszych wektorow.
    float NdotL = max(dot(vNormal, lightDir), 0.0);

    // Wyznaczenie koloru fragmentu/piksela.
    gl_FragColor = texture2D(texture, textureCoordinates) * NdotL * gl_LightSource[0].diffuse;
    gl_FragColor.a = 1.0;
    //gl_FragColor = vec4(1.0,0.0,0.0,1.0);
}
