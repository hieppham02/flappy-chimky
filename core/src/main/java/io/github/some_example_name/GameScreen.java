package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
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
    private final int WORLD_WIDTH = 800;
    private final int WORLD_HEIGHT = 480;

    //Objects
    private Bird bird;

    GameScreen() {
        bird = new Bird();

        camera = new OrthographicCamera();
        viewport = new StretchViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);

        background = new Texture("objects/background-day.png");
        backgroundOffset = 0;

        batch = new SpriteBatch();


    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        backgroundOffset += 100 * delta; // tốc độ (pixel/giây)
        if (backgroundOffset > WORLD_WIDTH) {
            backgroundOffset = 0;
        }

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        bird.update(delta);

        // Vẽ 2 cái background kế nhau theo trục X
        batch.draw(background, -backgroundOffset, 0, WORLD_WIDTH, WORLD_HEIGHT);
        batch.draw(background, -backgroundOffset + WORLD_WIDTH, 0, WORLD_WIDTH, WORLD_HEIGHT);

        bird.render(batch);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        batch.setProjectionMatrix(camera.combined);
    }

    @Override
    public void dispose() {

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
