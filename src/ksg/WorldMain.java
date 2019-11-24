package ksg;

import ksg.lab2.Weather;
import com.jogamp.newt.Window;
import com.jogamp.newt.event.KeyAdapter;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.event.MouseAdapter;
import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.event.MouseListener;
import com.jogamp.newt.event.awt.AWTKeyAdapter;
import com.jogamp.newt.event.awt.AWTMouseAdapter;
import com.jogamp.opengl.FBObject;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import ksg.common.Colour;
import ksg.common.FPSCounter;
import ksg.lab3.ShaderLocations;
import ksg.lab3.ShaderTool;
import ksg.common.Camera;
import ksg.common.GameObject;
import ksg.lab1.GroundVbo;
import ksg.lab1.House;
import ksg.lab1.Light;
import ksg.lab2.SmokeEmitter;
import ksg.lab2.SmokeParticle;
import ksg.lab2.Weather;
import ksg.lab2.Global;
import ksg.lab3.TexturedQuad;
import ksg.common.Vector3;
import java.util.Random;

/**
 * @author MK, changes TM
 */
public class WorldMain implements GLEventListener {

    protected ArrayList<GameObject> objects; // kolekcja obiektów wyświetlanych na scenie
    protected static ArrayList<Texture> textures; // kolekcja tekstur
    protected Camera camera; // kamera odpowiedzialna za położenie i kąt widzenia obserwatora
    protected Light light; // oświetlenie sceny
    protected GLU glu;
    protected FPSCounter fps; // miernik liczby klatek na sekundę
    protected boolean showFPS;
    protected int renderingType = 2; // tryby wyświetlania: punkty / linie / ściany
    protected long currentTime, previousTime; // czas jaki upłynął pomiędzy kolejnymi klatkami obrazu
    public int screenWidth, screenHeight; // rozdzielczość ekranu
    public int windowBordersWidth, windowBordersHeight; // grubość ramek okna
    public java.awt.Frame frame; // okno aplikacji

    // Poniższe zmienne będą wytłumaczone podczas laboratorium 3.
    protected FBObject frameBuffer = null, textureBuffer = null; // bufory przechowujące zawartość sceny
    protected ShaderLocations postProcessingShaders; // shadery nakładane na cały ekran
    protected boolean postProcessingActive = false;
    protected boolean postProcessingPrepared = false;
    protected int screenWidthUniformHandle, screenHeightUniformHandle; // adresy zmiennych w shaderach

    protected float angle = 26.7f; // in radians, chosen experimentally to give a good lighting
    boolean copernicus = true; // true - the sun doesn't move

    // TASK 2.4: configurable number of particles
    int particlesCnt = 128;
    int smokeIndex = -1;

    // TASK 2.5: printing time it took to draw one frame
    int statsPrint = 0; // counter increasing from 0 to statsPrintEveryFrames
    int statsPrintEveryFrames = 60; // Stats will be printed every X frames

    @Override
    public void init(GLAutoDrawable drawable) // inicjalizacja sceny
    {
        currentTime = previousTime = System.nanoTime();
        objects = new ArrayList<>();
        textures = new ArrayList<>();

        camera = new Camera();
        camera.setPosition(0, 0, 350);

        GL2 gl = drawable.getGL().getGL2();
        glu = new GLU();

        // Wypisanie informacji o dostępnym sprzęcie.
        System.err.println("GL_VENDOR: " + gl.glGetString(GL2.GL_VENDOR));
        System.err.println("GL_RENDERER: " + gl.glGetString(GL2.GL_RENDERER));
        System.err.println("GL_VERSION: " + gl.glGetString(GL2.GL_VERSION));

        // Konfiguracja parametrów wyświetlania.
        gl.glShadeModel(GL2.GL_SMOOTH); // dokładne cieniowanie
        gl.glClearDepth(1.0f);
        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glEnable(GL2.GL_ALPHA_TEST);
        gl.glDepthFunc(GL2.GL_LEQUAL);
        gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);
        gl.glEnable(GL2.GL_TEXTURE_2D);
        gl.glHint(GL2.GL_POINT_SMOOTH_HINT, GL2.GL_NICEST);
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);

        // Konfiguracja wyglądu podstawowych materiałów (w tym rodzaj połysku).
        float mat_specular[] = {1.0f, 1.0f, 1.0f, 1.0f};
        float mat_shininess[] = {25.0f};
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, mat_specular, 0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SHININESS, mat_shininess, 0);

        // Dodanie obiektów przestrzennych do świata.
        addModels(gl);

        initSmoke();

        // Jednorazowa konfiguracja oświetlenia.
        setupLight(gl);

        // Obsługa myszki i klawiatury.
        MouseListener mouseListener = new WorldMouseAdapter();
        KeyListener keyListener = new WorldKeyAdapter();
        if (drawable instanceof Window) {
            Window window = (Window) drawable;
            window.addMouseListener(mouseListener);
            window.addKeyListener(keyListener);
        } else if (GLProfile.isAWTAvailable() && drawable instanceof java.awt.Component) {
            java.awt.Component comp = (java.awt.Component) drawable;
            new AWTMouseAdapter(mouseListener, drawable).addTo(comp);
            new AWTKeyAdapter(keyListener, drawable).addTo(comp);
        }

        // Utworzenie obiektu wyświetlającego średnią liczbę klatek wyświetlaną podczas pojedynczej sekundy.
        fps = new FPSCounter(drawable, 24);
        fps.setTextLocation(FPSCounter.UPPER_LEFT);
        showFPS = true;

        if (!postProcessingPrepared) {
            // Inicjalizacja buforów do przechowywania rezultatu wyrenderowanej sceny.
            frameBuffer = new FBObject();
            frameBuffer.init(gl, screenWidth, screenHeight, 0);
            frameBuffer.attachTexture2D(gl, 0, false);
            frameBuffer.attachRenderbuffer(gl, FBObject.Attachment.Type.DEPTH, 32);
            frameBuffer.unbind(gl);

            // Kompilacja shaderów potrzebnych do postprodukcji.
            postProcessingShaders = new ShaderTool().createShaders(gl, "/ksg/lab3/shaders/ScreenVertex.glsl", "/ksg/lab3/shaders/ScreenFragment.glsl");
            outputError(gl);
            postProcessingPrepared = true;
        }
    }

    protected void addModels(GL2 gl) // metoda dodająca podstawowy zestaw modeli
    {
        try {
            // Wczytanie tekstur do kolekcji.
            textures.add(TextureIO.newTexture(getClass().getClassLoader().getResourceAsStream("ksg/resources/Particle.png"), false, TextureIO.PNG));
            textures.add(TextureIO.newTexture(getClass().getClassLoader().getResourceAsStream("ksg/resources/grass_texturelib.com.jpg"), false, TextureIO.JPG));
            textures.add(TextureIO.newTexture(getClass().getClassLoader().getResourceAsStream("ksg/resources/DefaultFire.png"), false, TextureIO.PNG));

            // Dodanie obiektów do sceny.
            objects.add(new House(-250, -150, -650));

            BufferedImage displacementMap = readImage("resources/DisplacementMap.png");
            GroundVbo vboMesh = new GroundVbo(0, -150, -500, textures.get(1), displacementMap);
            vboMesh.generateBuffers(gl);
            objects.add(vboMesh);
            //objects.add( new GroundRetro(0, -150, -500, textures.get(1)) );
        } catch (IOException | GLException e) {
            e.printStackTrace();
        }
    }

    // TASK LAB2.1
    public void initSmoke() {

        BufferedImage fire = readImage("resources/DefaultFire.png");
        BufferedImage part = readImage("resources/Particle.png");

        ksg.common.Vector3 pos = new ksg.common.Vector3(-100, 270, 650);
        ksg.lab2.SmokeEmitter sm = new ksg.lab2.SmokeEmitter(pos, particlesCnt, textures.get(2));

        // If this is the first time, we're adding smoke emitter, remember its index
        if (smokeIndex != -1) {
            objects.remove(smokeIndex);
        } else {
            // If this is not the first, time, remove the old one, and remember ths index of the new one
            smokeIndex = objects.size();
        }

        objects.add(sm);
    }

    protected void setupLight(GL2 gl) // konfiguracja oświetlenia
    {
        // Stworzenie obiektu reprezentującego światło i dodanie go do kolekcji obiektów.
        light = new Light(new TexturedQuad(0, 0, 0, textures.get(0)));

        light.getMesh().createShaders(gl,
            "/ksg/lab3/shaders/QuadVertex.glsl",
            "/ksg/lab3/shaders/QuadFragment.glsl");

        objects.add(light);
        light.setPosition(80.0f, 100.0f, 120.0f);
        light.setDiffuseColour(new Colour(1.7f, 1.7f, 1.7f, 1.0f));
        light.setSpecularColour(new Colour(1.0f, 1.0f, 1.0f, 1.0f));

        // Parametry oświetlenia sceny.
        float light_ambient[] = {0.0f, 0.0f, 0.0f, 1.0f};
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, light_ambient, 0);
        light.update(gl);
        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_LIGHT0);
    }

    protected void updateCamera(GL2 gl, int width, int height) {
        // Przełączenie aktualnej macierzy na macierz projekcji.
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();

        // Ustawienie perspektywy.
        float widthHeightRatio = (float) width / (float) height;
        glu.gluPerspective(45, widthHeightRatio, camera.nearClipping, camera.farClipping);

        // Przywrócenie macierzy modeli.
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();

        glu.gluLookAt(camera.getPosition().x, camera.getPosition().y, camera.getPosition().z, camera.getTarget().x, camera.getTarget().y, camera.getTarget().z, 0, camera.yUp, camera.zUp);
    }

    void updateRenderingType(GL2 gl) // ustawienie trybu rysowania zawartości sceny
    {
        if (renderingType == 0) {
            gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_POINT); // rysowanie punktów
        } else if (renderingType == 1) {
            gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE); // rysowanie linii
        } else {
            gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL); // rysowanie ścian
        }
    }

    @Override
    public void dispose(GLAutoDrawable drawable) // destruktor sceny
    {
        System.out.println("dispose()");
        GL2 gl = drawable.getGL().getGL2();

        for (GameObject object : objects) {
            object.freeMemory(gl);
        }

        if (postProcessingPrepared) {
            // Zniszczenie buforów.
            if (frameBuffer != null) {
                frameBuffer.destroy(gl);
                frameBuffer = null;
            }

            if (textureBuffer != null) {
                textureBuffer.destroy(gl);
                textureBuffer = null;
            }
        }
    }

    // Ten kawalek kodu przesuwa swiatlo, wokol srodka "ziemi" (x,z)=(0,-500)
    // TASK LAB1.1: Rotating right.
    void moveLight() {

        // This is a copernican model, the Sun doesn't move. ;)
        if (copernicus) {
            return;
        }

        // Oryginalne wspolrzedne
        // Original light position: 80.0f, 100.0f, 120.0f
        // Ground cetern: 0.0f, 0.0f, -500.0f
        // Nice lighting: r = 200, (80, 100, 220)
        // Around the scene with broken normals: r=500, (0,0, -500)
        angle += 0.02; // rotation speed
        float radius = 200.0f;
        float center_x = 80.0f;
        float center_y = 100.0f;
        float center_z = 220.0f;

        light.setPosition(center_x + (float) (Math.sin(angle) * radius), center_y, center_z + (float) (Math.cos(angle) * radius));
    }

    float getRandom() {
        Random r = new Random();
        float f = r.nextInt(1000) / 500.0f;
        return f;
    }

    void setRandomLightColor() {
        float r = getRandom();
        float g = getRandom();
        float b = getRandom();
        System.out.printf("Setting new color: RGB=(%4.2f, %4.2f, %4.2f)\n", r, g, b);

        Colour c = new Colour(r, g, b, 1.0f);
        light.setDiffuseColour(c);
        light.setSpecularColour(c);
    }

    void resetLightColor() {
        light.setDiffuseColour(new Colour(1.7f, 1.7f, 1.7f, 1.0f));
        light.setSpecularColour(new Colour(1.0f, 1.0f, 1.0f, 1.0f));
    }

    @Override
    public void display(GLAutoDrawable drawable) // metoda renderująca aktualną klatkę
    {
        GL2 gl = drawable.getGL().getGL2();

        if (postProcessingActive) {
            // Podpięcie buforów do zapisywania rezultatu wyrenderowanej sceny.
            frameBuffer.bind(gl);
        }

        gl.glClearColor(0.0f, 0.6f, 1.0f, 0.0f); // kolor tła sceny

        // Czyszczenie sceny.
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

        updateRenderingType(gl);

        // Zaktualizowanie kamery.
        gl.glLoadIdentity();
        updateCamera(gl, screenWidth, screenHeight);

        moveLight();

        // Rysowanie obiektów,
        for (GameObject object : objects) {
            object.draw(gl);
        }

        if (postProcessingActive) {
            // Odłączenie buforów.
            frameBuffer.unbind(gl);

            // Skopiowanie rezultatu wyrenderowanej sceny.
            textureBuffer = frameBuffer;
            gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

            // Użycie rezultatu wyrenderowanej sceny jako tekstury.
            gl.glEnable(GL2.GL_TEXTURE_2D);
            textureBuffer.use(gl, (FBObject.TextureAttachment) textureBuffer.getColorbuffer(0));

            // Ustawienie rzutu ortogonalnego.
            gl.glMatrixMode(GL2.GL_PROJECTION);
            gl.glLoadIdentity();
            gl.glOrthof(0f, screenWidth, 0f, screenHeight, camera.nearClipping, camera.farClipping);
            glu.gluLookAt(0, 0, 100, 0, 0, 0, 0, 1, 0);
            gl.glMatrixMode(GL2.GL_MODELVIEW);
            gl.glLoadIdentity();

            // Wyłączenie oświetlenia.
            gl.glPushAttrib(GL2.GL_LIGHTING);
            gl.glDisable(GL2.GL_LIGHTING);

            // Użycie shaderów do postprodukcji.
            gl.glUseProgram(postProcessingShaders.getShaderProgram());

            // Pobranie adresów zmiennych w shaderach.
            screenWidthUniformHandle = gl.glGetUniformLocation(postProcessingShaders.getShaderProgram(), "screenWidth");
            screenHeightUniformHandle = gl.glGetUniformLocation(postProcessingShaders.getShaderProgram(), "screenHeight");

            // Przekazanie wartości zmiennych do shaderów.
            gl.glUniform1f(screenWidthUniformHandle, (float) screenWidth);
            gl.glUniform1f(screenHeightUniformHandle, (float) screenHeight);

            // Narysowanie prymitywnej ściany z użyciem tekstury zawierającej rezultat wyrenderowania sceny.
            gl.glBegin(GL2.GL_QUADS);
            gl.glTexCoord2f(0, 0);
            gl.glVertex2f(0f, 0f);
            gl.glTexCoord2f(1, 0);
            gl.glVertex2f(screenWidth + windowBordersWidth, 0f);
            gl.glTexCoord2f(1, 1);
            gl.glVertex2f(screenWidth + windowBordersWidth, screenHeight + windowBordersHeight);
            gl.glTexCoord2f(0, 1);
            gl.glVertex2f(0f, screenHeight + windowBordersHeight);
            gl.glEnd();

            gl.glUseProgram(0); // przywrócenie domyślnych shaderów

            gl.glPopAttrib(); // GL_LIGHTING

            // Wyłączenie tekstury.
            textureBuffer.unuse(gl);
            gl.glDisable(GL2.GL_TEXTURE_2D);
            textureBuffer = null;
        }

        if (showFPS) {
            fps.draw();
        }

        // TASK 2.5: Make the smoke movement speed independent of the number of particles
        currentTime = System.nanoTime();
        double scaler = (currentTime - previousTime) * 0.00000006;
        previousTime = currentTime;

        statsPrint++;
        if (statsPrint == 100) {
            statsPrint = 0;
            System.out.printf("Particles = %d, timeScaler=%f\n", particlesCnt, Global.timeScaler);
        }

        Global.timeScaler = scaler;
    }

    // Metoda wywoływana przy każdym przeskalowaniu okna.
    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        System.out.println("reshape()");
        screenWidth = width;
        screenHeight = height;

        GL2 gl = drawable.getGL().getGL2();

        // Wyłączenie VSync (synchronizacji pionowej)
        gl.setSwapInterval(0);

        // Konfiguracja macierzy projekcji.
        float h = (float) height / (float) width;
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        if (h < 1) {
            gl.glFrustum(-1.0f, 1.0f, -h, h, 1.0f, 1000.0f);
        } else {
            h = 1.0f / h;
            gl.glFrustum(-h, h, -1.0f, 1.0f, 1.0f, 1000.0f);
        }

        // Przełączenie z powrotem na macierz modeli.
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    public BufferedImage readImage(String name) /// metoda wczytująca obraz z pliku
    {
        BufferedImage image;
        try {
            // Próba wczytania pliku z katalogu roboczego.
            File file = new File(name);
            if (file.isFile()) {
                image = ImageIO.read(file);
            } // Próba wczytania pliku z katalogu w którym znajduje się aktualna klasa (.class)
            else {
                URL url = getClass().getResource(name);
                if (url == null) {
                    url = new URL(name);
                }
                image = ImageIO.read(url);
            }
        } catch (IOException e) {
            throw new RuntimeException("Nie można odczytać pliku z obrazem: " + name);
        }

        if (image == null) {
            throw new RuntimeException("Niewłaściwy format obrazu: " + name);
        }

        return image;
    }

    protected void outputError(GL2 gl) // metoda wypisująca błędy przy kompilacji shaderów
    {
        int c;
        if ((c = gl.glGetError()) != GL2.GL_NO_ERROR) {
            System.out.println(glu.gluErrorString(c));
        }
    }

    // Obsługa klawiatury.
    class WorldKeyAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {
            int kc = e.getKeyCode();
            if (kc == KeyEvent.VK_ESCAPE) {
                frame.dispose();
                System.exit(0);
            }

            if (kc == KeyEvent.VK_W) {
                camera.moveForward(100);
            }
            if (kc == KeyEvent.VK_S) {
                camera.moveBackward(100);
            }
            if (kc == KeyEvent.VK_A) {
                camera.moveLeft(100);
            }
            if (kc == KeyEvent.VK_D) {
                camera.moveRight(100);
            }
            if (kc == KeyEvent.VK_Q) {
                camera.move(0, -100, 0);
            }
            if (kc == KeyEvent.VK_E) {
                camera.move(0, 100, 0);
            }

            if (kc == KeyEvent.VK_LEFT) {
                camera.rotate(-10, 0, 0);
            }
            if (kc == KeyEvent.VK_RIGHT) {
                camera.rotate(10, 0, 0);
            }
            if (kc == KeyEvent.VK_UP) {
                camera.rotate(0, -10, 0);
            }
            if (kc == KeyEvent.VK_DOWN) {
                camera.rotate(0, 10, 0);
            }

            if (kc == KeyEvent.VK_NUMPAD8) {
                light.move(0, 0, -20);
                System.out.println("Light pos: " + light.getPosition().x + " " + light.getPosition().y + " " + light.getPosition().z);
            }
            if (kc == KeyEvent.VK_NUMPAD2) {
                light.move(0, 0, 20);
                System.out.println("Light pos: " + light.getPosition().x + " " + light.getPosition().y + " " + light.getPosition().z);
            }

            if (kc == KeyEvent.VK_NUMPAD4) {
                light.move(-20, 0, 0);
                System.out.println("Light pos: " + light.getPosition().x + " " + light.getPosition().y + " " + light.getPosition().z);
            }
            if (kc == KeyEvent.VK_NUMPAD6) {
                light.move(20, 0, 0);
                System.out.println("Light pos: " + light.getPosition().x + " " + light.getPosition().y + " " + light.getPosition().z);
            }

            if (kc == KeyEvent.VK_PAGE_DOWN) {
                light.move(0, -20, 0);
                System.out.println("Light pos: " + light.getPosition().x + " " + light.getPosition().y + " " + light.getPosition().z);
            }
            if (kc == KeyEvent.VK_PAGE_UP) {
                light.move(0, 20, 0);
                System.out.println("Light pos: " + light.getPosition().x + " " + light.getPosition().y + " " + light.getPosition().z);
            }

            if (kc == KeyEvent.VK_Z) {
                Weather.changeWindSpeed(-1, 0, 0);
            }
            if (kc == KeyEvent.VK_X) {
                Weather.changeWindSpeed(1, 0, 0);
            }
            if (kc == KeyEvent.VK_C) {
                Weather.changeWindSpeed(0, 0, 1);
            }
            if (kc == KeyEvent.VK_V) {
                Weather.changeWindSpeed(0, 0, -1);
            }
            if (kc == KeyEvent.VK_B) {
                Weather.changeWindSpeed(0, 1, 0);
            }
            if (kc == KeyEvent.VK_N) {
                Weather.changeWindSpeed(0, -1, 0);
            }

            // TASK 2.4: Change the number of smoke particles.
            if (kc == KeyEvent.VK_1) {
                particlesCnt *= 2;
                System.out.printf("Number of particles: %d\n", particlesCnt);
                initSmoke();
            }
            // TASK 2.4: Change the number of smoke particles.
            if (kc == KeyEvent.VK_2) {
                if (particlesCnt > 10) {
                    particlesCnt /= 2;
                }
                System.out.printf("Number of particles: %d\n", particlesCnt);
                initSmoke();
            }
            if (kc == KeyEvent.VK_L) {
                // TASK 1.1: Make the light move around the scene after pressing L
                copernicus = !copernicus;
                System.out.printf("Light position angle: %f\n", angle);
            }
            if (kc == KeyEvent.VK_N) {
                // TASK 1.4: Change light color
                setRandomLightColor();
            }
            if (kc == KeyEvent.VK_M) {
                // TASK 1.4: Change light color
                resetLightColor();
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            int kc = e.getKeyCode();

            if (kc == KeyEvent.VK_F1) {
                showFPS = !showFPS;
            } else if (kc == KeyEvent.VK_R) {
                renderingType = (renderingType + 1) % 3;
            }

            if (kc == KeyEvent.VK_P) {
                postProcessingActive = !postProcessingActive;
                System.out.printf("Postprocessing is %s", (postProcessingActive?"TRUE":"FALSE"));
            }
        }

    }

    // Obsługa myszki (gdyby była potrzebna).
    class WorldMouseAdapter extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
        }

        public void mouseReleased(MouseEvent e) {
        }

        public void mouseDragged(MouseEvent e) {
        }
    }

    public static void main(String[] args) {
        final WorldMain world = new WorldMain();
        world.screenWidth = 800;
        world.screenHeight = 600;

        // Stworzenie okna.
        java.awt.Frame frame = new java.awt.Frame("OpenGL Labs");
        frame.setSize(world.screenWidth, world.screenHeight);
        frame.setLayout(new java.awt.BorderLayout());
        world.frame = frame;

        // Ustawienie automatycznego rysowania sceny na maksymalnie 70 razy na sekundę (max 70 FPS).
        final FPSAnimator animator = new FPSAnimator(70, false);

        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                // Utworzenie nowego wątku zamykającego okno aplikacji.
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        animator.stop();
                        frame.dispose();
                        System.exit(0);
                    }
                }).start();
            }
        });

        // Konfiguracja parametrów wyświetlania.
        GLCapabilities caps = new GLCapabilities(null);
        caps.setDoubleBuffered(true); // podwójne buforowanie
        caps.setHardwareAccelerated(true); // akceleracja sprzętowa
        GLCanvas canvas = new GLCanvas(caps); // utworzenie płótna na którym będzie rysowany kontekst graficzny OpenGL
        animator.add(canvas);

        canvas.addGLEventListener(world);
        frame.add(canvas, java.awt.BorderLayout.CENTER);
        frame.validate();

        frame.setVisible(true);
        animator.start();

        // Pobranie grubości ramek okna.
        Insets frameInsets = frame.getInsets();
        System.out.println("Frame insets: left=" + frameInsets.left + "; right=" + frameInsets.right + "; top=" + frameInsets.top + "; bottom=" + frameInsets.bottom + ";");
        world.windowBordersWidth = frameInsets.left + frameInsets.right;
        world.windowBordersHeight = frameInsets.top + frameInsets.bottom;
    }
}
