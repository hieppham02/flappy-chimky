package io.github.flappybird;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class Bird implements Disposable {
    // Constants
    private static final float ANIMATION_FRAME_DURATION = 0.275f;
    private static final float BASE_WIDTH = 34f;
    private static final float BASE_HEIGHT = 24f;
    private static final int MAX_FALL_ROTATION = 6;
    private static final float ROTATION_PER_FALL = -15f;
    private static final long FALL_DELAY_MS = 20;

    // Animation
    private Animation<TextureRegion> animation;
    private float stateTime;

    // Textures
    private Texture downFlapTexture;
    private Texture midFlapTexture;
    private Texture upFlapTexture;

    // Position and physics
    private float x, y;
    private float velocityY;
    private float gravity;
    private float scale;

    // Rotation logic
    private int fallCount;
    private long lastFallTime;

    // Collision
    private Rectangle hitbox;

    public Bird() {
        initializeTextures();
        initializeAnimation();
        initializePhysics();
        initializeHitbox();
    }

    private void initializeTextures() {
        downFlapTexture = new Texture("objects/yellowbird-downflap.png");
        midFlapTexture = new Texture("objects/yellowbird-midflap.png");
        upFlapTexture = new Texture("objects/yellowbird-upflap.png");
    }

    private void initializeAnimation() {
        TextureRegion[] frames = {
            new TextureRegion(downFlapTexture),
            new TextureRegion(midFlapTexture),
            new TextureRegion(upFlapTexture)
        };

        animation = new Animation<>(ANIMATION_FRAME_DURATION, new Array<>(frames));
        stateTime = 0f;
    }

    private void initializePhysics() {
        velocityY = 0f;
        gravity = 0f;
        fallCount = 0;
        lastFallTime = 0;
    }

    private void initializeHitbox() {
        hitbox = new Rectangle();
        updateHitbox();
    }

    public void update(float delta, float topLimit, float bottomLimit) {
        updateHitbox();
        updateAnimation(delta);
        updatePhysics(delta);
        updateRotation();
        applyBoundaries(topLimit, bottomLimit);
    }

    private void updateAnimation(float delta) {
        stateTime += delta;
    }

    private void updatePhysics(float delta) {
        velocityY += gravity * delta;
        y += velocityY * delta;
    }

    private void updateRotation() {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastFallTime > FALL_DELAY_MS) {
            if (velocityY < 0 && fallCount < MAX_FALL_ROTATION) {
                fallCount++;
            }
            lastFallTime = currentTime;
        }
    }

    private void applyBoundaries(float topLimit, float botLimit) {
        float scaledTopLimit = topLimit + (BASE_HEIGHT * scale);
        float scaledBotLimit = botLimit * scale + 50;

        if (y <= scaledBotLimit) {
            y = scaledBotLimit;
            velocityY = 0;
        }

        if (y > scaledTopLimit) {
            y = scaledTopLimit;
            velocityY = 0;
        }
    }

    private void updateHitbox() {
        float offsetX = x-getScaledWidth()/2 + 12;
        float offsetY = y-getScaledHeight()/2 + 5;
        hitbox.set(offsetX, offsetY, getScaledWidth(), getScaledHeight());
    }

    public void jump(float jumpHeight) {
        velocityY = jumpHeight;
        stateTime = 0f;
        fallCount = -2; // Reset rotation để bird hướng lên
        lastFallTime = System.currentTimeMillis();
    }

    public void render(SpriteBatch batch) {
        TextureRegion currentFrame = animation.getKeyFrame(stateTime, true);
        float rotation = fallCount * ROTATION_PER_FALL;

        batch.draw(
            currentFrame,
            x, y,
            BASE_WIDTH / 2, BASE_HEIGHT / 2,
            BASE_WIDTH, BASE_HEIGHT,
            scale, scale,
            rotation
        );
    }

    // Getters and Setters
    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getVelocityY() {
        return velocityY;
    }

    public float getScale() {
        return scale;
    }

    public Rectangle getHitbox() {
        return hitbox;
    }

    public void setX(float x) {
        this.x = x;
        updateHitbox();
    }

    public void setY(float y) {
        this.y = y;
        updateHitbox();
    }

    public void setVelocityY(float velocityY) {
        this.velocityY = velocityY;
    }

    public void setGravity(float gravity) {
        this.gravity = gravity;
    }

    public void setScale(float scale) {
        this.scale = scale;
        updateHitbox();
    }

    public float getWidth() {
        return BASE_WIDTH;
    }

    public float getHeight() {
        return BASE_HEIGHT;
    }

    public float getScaledWidth() {
        return BASE_WIDTH * scale;
    }

    public float getScaledHeight() {
        return BASE_HEIGHT * scale;
    }

    // Collision detection
    public boolean isColliding(Rectangle other) {
        return hitbox.overlaps(other);
    }

    public boolean isColliding(Bird other) {
        return hitbox.overlaps(other.getHitbox());
    }

    @Override
    public void dispose() {
        downFlapTexture.dispose();
        midFlapTexture.dispose();
        upFlapTexture.dispose();
    }
}
