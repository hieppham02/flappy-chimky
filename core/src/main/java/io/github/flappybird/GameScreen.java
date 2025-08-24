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
    //Screen
    private Camera camera;
    private Viewport viewport;

    //Graphics
    private Texture background;
    private SpriteBatch batch;

    //Timing
    private int backgroundOffset;
    private int baseOffset;

    //World parameters
    private int WORLD_WIDTH;
    private int WORLD_HEIGHT;

    //Other parameters
    private int scrollSpeed;
    private float scale;
    private Stage stage;
    private Label logLabel;

    // Debug parameters
    private boolean showHitbox = true; // Bật/tắt hiển thị hitbox
    private Label debugLabel;

    //Objects
    private Bird bird;
    private ShapeRenderer shapeRenderer;

    @Override
    public void show() {
        SetWorldSize();
        scale = 5f;
        SetBirdPosition();
        setView();
        setBackground();

        shapeRenderer = new ShapeRenderer();
        scrollSpeed = 300;

        stage = new Stage(new ScreenViewport());
        BitmapFont font = new BitmapFont();
        font.getData().setScale(3f); // Giảm size font một chút
        Label.LabelStyle style = new Label.LabelStyle(font, Color.WHITE);

        logLabel = new Label("VelocityY: 0", style);
        debugLabel = new Label("Debug: Press H to toggle hitbox", style);
        debugLabel.setPosition(10, WORLD_HEIGHT - 100);

        // Thêm input processor để toggle hitbox
        setupInputProcessor();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        scrollBackround(delta);

        // Vẽ background và objects
        batch.begin();
        drawBackground();
        bird.update(delta, WORLD_HEIGHT, 112);
        bird.render(batch);
        batch.end();

        // Vẽ hitbox (nếu bật debug)
        drawHitbox();

        // Vẽ debug info
        drawLog(delta);
    }

    public void SetWorldSize() {
        WORLD_WIDTH = Gdx.graphics.getWidth();
        WORLD_HEIGHT = Gdx.graphics.getHeight();
    }

    public void SetBirdPosition() {
        bird = new Bird();
        bird.setX((float) WORLD_WIDTH / 2 - (17 * scale));
        bird.setY((float) WORLD_HEIGHT / 2);
        bird.setGravity(-5000);
        bird.setScale(scale);
    }

    public void setView() {
        camera = new OrthographicCamera();
        viewport = new ExtendViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
    }

    public void setBackground() {
        background = new Texture("objects/background-day.png");
        backgroundOffset = 0;
        batch = new SpriteBatch();
    }

    public void scrollBackround(float delta) {
        backgroundOffset += (int) (scrollSpeed * delta);
        if (backgroundOffset > WORLD_WIDTH) {
            backgroundOffset = 0;
        }

        baseOffset += (int) (scrollSpeed * delta);
        if (baseOffset > WORLD_WIDTH) {
            baseOffset = 0;
        }
    }

    public void drawBackground() {
        // Vẽ 2 background liền nhau theo trục X
        batch.draw(background, -backgroundOffset, 0, WORLD_WIDTH, WORLD_HEIGHT);
        batch.draw(background, -backgroundOffset + WORLD_WIDTH, 0, WORLD_WIDTH, WORLD_HEIGHT);

        Texture base = new Texture("objects/base.png");
        batch.draw(base, -baseOffset, 0, 336 * scale, 112 * 2);
        batch.draw(base, -baseOffset + WORLD_WIDTH, 0, 336 * scale, 112 * 2);
    }

    public void drawHitbox() {
        if (!showHitbox) return;

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        // Vẽ hitbox của bird bằng màu đỏ
        shapeRenderer.setColor(Color.RED);
        Rectangle birdHitbox = bird.getHitbox();
        shapeRenderer.rect(
            birdHitbox.x,
            birdHitbox.y,
            birdHitbox.width,
            birdHitbox.height
        );

        // Vẽ outline của sprite bằng màu xanh để so sánh
        shapeRenderer.setColor(Color.CYAN);
        shapeRenderer.rect(
            bird.getX(),
            bird.getY(),
            bird.getScaledWidth(),
            bird.getScaledHeight()
        );

        shapeRenderer.end();
    }

    public void drawLog(float delta) {
        // Cập nhật thông tin debug
        logLabel.setText("VelocityY: " + (int) bird.getVelocityY());
        logLabel.setPosition(bird.getX() + bird.getScaledWidth() + 10, bird.getY() + 50);

        debugLabel.setText("Debug: Press H to toggle hitbox | Hitbox: " + (showHitbox ? "ON" : "OFF"));

        stage.addActor(logLabel);
        stage.addActor(debugLabel);
        stage.act(delta);
        stage.draw();

        // Clear actors để tránh duplicate
        stage.clear();
    }

    public void setupInputProcessor() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                bird.jump(1500);
                return true;
            }

            @Override
            public boolean keyDown(int keycode) {
                // Toggle hitbox display với phím H
                if (keycode == com.badlogic.gdx.Input.Keys.H) {
                    showHitbox = !showHitbox;
                    return true;
                }
                // Jump với phím Space
                if (keycode == com.badlogic.gdx.Input.Keys.SPACE) {
                    bird.jump(1500);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        batch.setProjectionMatrix(camera.combined);
    }

    @Override
    public void dispose() {
        batch.dispose();
        background.dispose();
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
        stage.dispose();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }
}
