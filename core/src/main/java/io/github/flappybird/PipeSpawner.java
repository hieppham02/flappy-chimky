package io.github.flappybird;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class PipeSpawner {
    private final Pipe pipe;
    public PipeSpawner() {
        pipe = new Pipe(300, 0); // toạ độ (300,0)
    }

    public void render(SpriteBatch batch) {
        pipe.render(batch);
    }

    public void dispose() {
        pipe.dispose();
    }
}
