package io.github.flappybird;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameScreen implements Screen {
    // Constants
    private static final float SCALE = 5f;
    private static final float SCROLL_SPEED = 300f;
    private static final float BIRD_GRAVITY = -5000f;
    private static final float JUMP_HEIGHT = 1500f;
    private static final float GROUND_HEIGHT = 112f;
    private static final float BASE_SPRITE_WIDTH = 336f;
    private static final float DEBUG_FONT_SCALE = 3f;

    //World size
    private static int WORLD_WIDTH = 1280;
    private static int WORLD_HEIGHT = 720;

    // Graphics
    private Camera camera;
    private Viewport viewport;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;

    // Textures
    private Texture backgroundTexture;
    private Texture baseTexture;

    // Scrolling
    private float backgroundOffset;
    private float baseOffset;

    // Game objects
    private Bird bird;

    // UI
    private Stage stage;
    private Label velocityLabel;
    private BitmapFont font;

    // Debug
    private boolean showHitbox = true;

    @Override
    public void show() {
        initializeGraphics();
        initializeBird();
        initializeUI();
        setupInput();
    }

    private void initializeGraphics() {
        // Camera and viewport
        camera = new OrthographicCamera();
        viewport = new ExtendViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);

        // Rendering
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        // Textures
        backgroundTexture = new Texture("objects/background-day.png");
        baseTexture = new Texture("objects/base.png");

        // Initialize offsets
        backgroundOffset = 0f;
        baseOffset = 0f;
    }

    private void initializeBird() {
        bird = new Bird();

        float birdX = WORLD_WIDTH / 2f;
        float birdY = WORLD_HEIGHT / 2f;

        bird.setX(birdX);
        bird.setY(birdY);
        bird.setGravity(BIRD_GRAVITY);
        bird.setVelocityY(0);
        bird.setScale(SCALE);
    }

    private void initializeUI() {
        stage = new Stage(new ScreenViewport());

        // Font setup
        font = new BitmapFont();
        font.getData().setScale(DEBUG_FONT_SCALE);

        // Label style
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);

        // Velocity label
        velocityLabel = new Label("VelocityY: 0", labelStyle);
    }

    private void setupInput() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                bird.jump(JUMP_HEIGHT);
                return true;
            }
        });
    }

    @Override
    public void render(float delta) {
        update(delta);
        draw(delta);
    }

    private void update(float delta) {
        updateScrolling(delta);
        updateBird(delta);
    }

    private void updateScrolling(float delta) {
        float scrollAmount = SCROLL_SPEED * delta;

        backgroundOffset += scrollAmount;
        if (backgroundOffset >= WORLD_WIDTH) {
            backgroundOffset = 0;
        }

        baseOffset += scrollAmount;
        if (baseOffset >= WORLD_WIDTH) {
            baseOffset = 0;
        }
    }

    private void updateBird(float delta) {
        bird.update(delta, WORLD_HEIGHT, GROUND_HEIGHT);
    }

    private void draw(float delta) {
        // Clear screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Draw sprites
        batch.begin();
        drawBackground();
        drawGround();
        bird.render(batch);
        batch.end();

        // Draw debug info
        if (showHitbox) {
            drawHitbox();
        }

        drawUI(delta);
    }

    private void drawBackground() {
        // Draw scrolling background
        batch.draw(backgroundTexture, -backgroundOffset, 0, WORLD_WIDTH, WORLD_HEIGHT);
        batch.draw(backgroundTexture, -backgroundOffset + WORLD_WIDTH, 0, WORLD_WIDTH, WORLD_HEIGHT);
    }

    private void drawGround() {
        // Draw scrolling ground
        float groundHeight = GROUND_HEIGHT * SCALE; // Scale ground height
        float groundWidth = BASE_SPRITE_WIDTH * SCALE;

        batch.draw(baseTexture, -baseOffset, 0, groundWidth, groundHeight);
        batch.draw(baseTexture, -baseOffset + WORLD_WIDTH, 0, groundWidth, groundHeight);
    }

    private void drawHitbox() {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);

        Rectangle birdHitbox = bird.getHitbox();
        shapeRenderer.rect(
            birdHitbox.x,
            birdHitbox.y,
            birdHitbox.width,
            birdHitbox.height
        );

        shapeRenderer.end();
    }

    private void drawUI(float delta) {
        // Update velocity label
        velocityLabel.setText("VelocityY: " + (int) bird.getVelocityY());
        velocityLabel.setPosition(
            bird.getX() + bird.getScaledWidth() + 10,
            bird.getY() + 50
        );

        // Clear previous actors and add current ones
        stage.clear();
        stage.addActor(velocityLabel);

        // Update and draw stage
        stage.act(delta);
        stage.draw();
    }

    public void toggleHitboxDisplay() {
        showHitbox = !showHitbox;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        batch.setProjectionMatrix(camera.combined);

        // Update world dimensions
        WORLD_WIDTH = width;
        WORLD_HEIGHT = height;
    }

    @Override
    public void pause() {
        // Game pause logic here if needed
    }

    @Override
    public void resume() {
        // Game resume logic here if needed
    }

    @Override
    public void hide() {
        // Clean up when screen is hidden
    }

    @Override
    public void dispose() {
        // Dispose of all resources
        if (batch != null) {
            batch.dispose();
        }
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }
        if (baseTexture != null) {
            baseTexture.dispose();
        }
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
        if (stage != null) {
            stage.dispose();
        }
        if (font != null) {
            font.dispose();
        }
        if (bird != null) {
            bird.dispose();
        }
    }
}
