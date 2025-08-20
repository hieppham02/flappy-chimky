package io.github.some_example_name;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Bird {
    private Texture texture;
    private float x, y;
    private float velocityY;
    private final float GRAVITY = -500; // pixel/s²

    public Bird() {
        texture = new Texture("objects/yellowbird-downflap.png");
        x = 100;
        y = 300;
        velocityY = 0;
    }

    public void update(float delta) {
//        velocityY += GRAVITY * delta;
//        y += velocityY * delta;
//
//        // chạm đất
//        if (y < 0) {
//            y = 0;
//            velocityY = 0;
//        }
    }

    public void jump() {
        velocityY = 300; // nhảy lên
    }

    public void render(SpriteBatch batch) {
        float scale = 1f; // thu nhỏ còn 50%
        batch.draw(texture, x, y, 34 * scale, 24 * scale);
    }

    public void dispose() {
        texture.dispose();
    }

    // Getter để check va chạm
    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return 34; }
    public float getHeight() { return 24; }
}
