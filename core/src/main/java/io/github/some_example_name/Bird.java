package io.github.some_example_name;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class Bird {
    private TextureRegion[] frames;
    private Animation<TextureRegion> animation;
    private final Texture texture;
    private float x, y;
    private float width, height;
    private float stateTime;
    private float velocityY;
    private float gravity; // pixel/s²
    private Rectangle hitbox;

    public Bird() {
        texture = new Texture("objects/yellowbird-downflap.png");
        velocityY = 0;

        Texture textureDown = new Texture("objects/yellowbird-downflap.png");
        Texture textureMid = new Texture("objects/yellowbird-midflap.png");
        Texture textureUp = new Texture("objects/yellowbird-upflap.png");

        frames = new TextureRegion[3];
        frames[0] = new TextureRegion(textureDown);
        frames[1] = new TextureRegion(textureMid);
        frames[2] = new TextureRegion(textureUp);

        animation = new Animation<TextureRegion>(0.275f, new Array<TextureRegion>(frames));
        stateTime = 0f;

    }

    public void update(float delta) {
        stateTime += delta;

        velocityY += gravity * delta;
        y += velocityY * delta;

        // chạm đất
        if (y < 0) {
            y = 0;
            velocityY = 0;
        }

    }

    public void setHitboxSize(float width, float height) {
        this.width = width;
        this.height = height;
        hitbox = new Rectangle(x, y, this.width, this.height);
        hitbox.setPosition(x, y);
    }

    public void jump() {
        velocityY = 1000; // nhảy lên
        stateTime = 0f;
    }

    public void render(SpriteBatch batch) {
        float scale = 5f;
        TextureRegion currentFrame = animation.getKeyFrame(stateTime, true); // Lặp animation
        batch.draw(currentFrame, x, y, 34 * scale, 24 * scale);
    }

    public void dispose() {
        texture.dispose();
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setGravity(float gravity) {
        this.gravity = gravity;
    }

    public void setVelocityY(float velocityY) {
        this.velocityY = velocityY;

    }

    public Rectangle getHitbox() {
        return hitbox;
    }

    public float getWidth() {
        return 34;
    }

    public float getHeight() {
        return 24;
    }
}
