package io.github.flappybird;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Pipe {
    private final Texture texture;
    private final float x, y;
    private final Rectangle hitbox;

    public Pipe(float x, float y) {
        texture = new Texture("objects/pipe-green.png");
        this.x = x;
        this.y = y;

        hitbox = new Rectangle();
        hitbox.set(x, y, texture.getWidth(), texture.getHeight());
    }

    public void render(SpriteBatch batch) {
        batch.begin();
        batch.draw(texture, x, y, texture.getWidth(), texture.getHeight(), 0, 0, texture.getWidth(), texture.getHeight(), false, true);
        batch.end();
    }

    public void dispose() {
        texture.dispose();
    }

    public Rectangle getHitbox() {
        return hitbox;
    }
}

