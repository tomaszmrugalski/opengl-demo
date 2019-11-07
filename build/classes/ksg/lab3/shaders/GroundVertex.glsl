
uniform sampler2D texture;

varying vec3 vNormal;
varying vec2 textureCoordinates;

void main(void)
{
    // Wyznaczenie wektora normalnego wierzcholka w przestrzeni wzroku (eye space)
    // oraz znormalizowanie uzyskanego wyniku.
    vNormal = normalize(gl_NormalMatrix * gl_Normal);

    textureCoordinates = vec2(gl_MultiTexCoord0);

    //gl_Position = ftransform();
    float y = 100.0;

    vec4 newPos = vec4(gl_Vertex.x, y, gl_Vertex.z, 1.0 );

    gl_Position = gl_ModelViewProjectionMatrix * newPos;
}
