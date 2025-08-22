package io.github.some_example_name;

import static java.rmi.server.LogStream.log;

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
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * First screen of the application. Displayed after the application is created.
 */
public class GameScreen implements Screen {

    //Screen
    private Camera camera;
    private Viewport viewport;

    //Graphics
    private Texture background;
    private SpriteBatch batch;

    //Timing
    private int backgroundOffset;

    //World parameters
    private int WORLD_WIDTH;
    private int WORLD_HEIGHT;

    //Other parameters
    private int scrollSpeed;
    private Stage stage;
    private BitmapFont font;
    private Label.LabelStyle style;
    private Label logLabel;

    //Objects
    private Bird bird;
    private ShapeRenderer shapeRenderer;

    @Override
    public void show() {
        SetWorldSize();
        SetBirdPosition();
        setView();
        setBackground();

        shapeRenderer = new ShapeRenderer();
        scrollSpeed = 300;

        stage = new Stage(new ScreenViewport());
        font = new BitmapFont();
        font.getData().setScale(5f);
        style = new Label.LabelStyle(font, Color.WHITE);
        logLabel = new Label("Debug log...", style);

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        scrollBackround(delta);

        batch.begin();
        drawBackground();

        bird.update(delta);
        bird.render(batch);

        touchDown();
        if (bird.getY() > WORLD_HEIGHT - (24 * 5)) {
            bird.setY(WORLD_HEIGHT - (24 * 5));
            bird.setVelocityY(0);
            System.out.println("Đu đỉnh rồi huhu ");
        }

        batch.end();

        drawHitbox();

        drawLog(delta);

    }

    public void SetWorldSize() {
        WORLD_WIDTH = Gdx.graphics.getWidth();
        WORLD_HEIGHT = Gdx.graphics.getHeight();
    }

    public void SetBirdPosition() {
        bird = new Bird();
        bird.setX(WORLD_WIDTH / 2 - (17 * 5));
        bird.setY(WORLD_HEIGHT / 2);
        bird.setGravity(-3000);


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
        backgroundOffset += scrollSpeed * delta; // tốc độ (pixel/giây)
        if (backgroundOffset > WORLD_WIDTH) {
            backgroundOffset = 0;
        }
    }

    public void drawBackground() {
        // Vẽ 2 cái background liền nhau theo trục X
        batch.draw(background, -backgroundOffset, 0, WORLD_WIDTH, WORLD_HEIGHT);
        batch.draw(background, -backgroundOffset + WORLD_WIDTH, 0, WORLD_WIDTH, WORLD_HEIGHT);
    }

    public void drawHitbox() {
        bird.setHitboxSize(34 * 5, 24 * 5);
        // vẽ hitbox debug
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);

        // vẽ hitbox của chim
        shapeRenderer.rect(
            bird.getHitbox().x,
            bird.getHitbox().y,
            bird.getHitbox().width,
            bird.getHitbox().height
        );

        shapeRenderer.end();
    }
    public void drawLog(float delta){
        logLabel.setPosition(bird.getX() + (34 * 5), bird.getY()); // tọa độ trên màn hình
        stage.addActor(logLabel);
        stage.act(delta);
        logLabel.setText((int)bird.getY());
        stage.draw();
    }
    public void touchDown() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                bird.jump();
                return true;
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
        shapeRenderer.dispose();
    }


    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void hide() {
        // This method is called when another screen replaces this one.
    }
}
