package io.github.flappybird;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class Bird {
    private final Animation<TextureRegion> animation;
    private final Texture texture;
    private float x, y;
    private float stateTime;
    private float velocityY;
    private float gravity; // pixel/s²
    private int countFall;
    private long delay;
    private float scale;
    private Rectangle hitbox;

    // Hitbox parameters - có thể điều chỉnh để fit với sprite
    private static final float HITBOX_WIDTH_RATIO = 0.7f;  // 70% chiều rộng sprite
    private static final float HITBOX_HEIGHT_RATIO = 0.8f; // 80% chiều cao sprite
    private static final float HITBOX_OFFSET_X = 0.15f;    // Offset theo X
    private static final float HITBOX_OFFSET_Y = 0.1f;     // Offset theo Y

    public Bird() {
        texture = new Texture("objects/yellowbird-downflap.png");
        velocityY = 0;

        Texture textureDown = new Texture("objects/yellowbird-downflap.png");
        Texture textureMid = new Texture("objects/yellowbird-midflap.png");
        Texture textureUp = new Texture("objects/yellowbird-upflap.png");

        TextureRegion[] frames = new TextureRegion[3];
        frames[0] = new TextureRegion(textureDown);
        frames[1] = new TextureRegion(textureMid);
        frames[2] = new TextureRegion(textureUp);

        animation = new Animation<TextureRegion>(0.275f, new Array<TextureRegion>(frames));
        stateTime = 0f;

        // Khởi tạo hitbox
        hitbox = new Rectangle();
    }

    public void update(float delta, int topLim, int botLim) {
        stateTime += delta;

        velocityY += gravity * delta;
        y += velocityY * delta;

        if (System.currentTimeMillis() - delay > 20) {
            if (velocityY < 0 && countFall < 6) {
                countFall++;
            }
            delay = System.currentTimeMillis();
        }

        if (y < botLim * (scale / 2)) {
            y = botLim * (scale / 2);
            velocityY = 0;
        }

        if (y > topLim + (34 * scale)) {
            y = (topLim + (34 * scale));
            velocityY = 0;
        }

        // Cập nhật hitbox mỗi frame
        updateHitbox();
    }

    /**
     * Cập nhật hitbox theo vị trí và rotation của bird
     */
    private void updateHitbox() {
        float rotation = countFall * -15f; // Góc xoay hiện tại

        // Kích thước hitbox thực tế
        float hitboxWidth = getScaledWidth() * HITBOX_WIDTH_RATIO;
        float hitboxHeight = getScaledHeight() * HITBOX_HEIGHT_RATIO;

        // Tính toán vị trí hitbox với offset
        float hitboxX = x + (getScaledWidth() * HITBOX_OFFSET_X);
        float hitboxY = y + (getScaledHeight() * HITBOX_OFFSET_Y);

        // Nếu muốn hitbox xoay theo bird (phức tạp hơn)
        // Ở đây tôi giữ hitbox là hình chữ nhật không xoay để đơn giản
        hitbox.set(hitboxX, hitboxY, hitboxWidth, hitboxHeight);
    }

    /**
     * Phương thức để set hitbox theo tỷ lệ tùy chỉnh
     */
    public void setCustomHitbox(float widthRatio, float heightRatio, float offsetX, float offsetY) {
        float hitboxWidth = getScaledWidth() * widthRatio;
        float hitboxHeight = getScaledHeight() * heightRatio;
        float hitboxX = x + (getScaledWidth() * offsetX);
        float hitboxY = y + (getScaledHeight() * offsetY);

        hitbox.set(hitboxX, hitboxY, hitboxWidth, hitboxHeight);
    }

    public void jump(float jumpHeight) {
        velocityY = jumpHeight;
        stateTime = 0f;
        countFall = -2;
        delay = System.currentTimeMillis();
    }

    public void render(SpriteBatch batch) {
        TextureRegion currentFrame = animation.getKeyFrame(stateTime, true);
        batch.draw(currentFrame, x, y, getWidth() / 2, getHeight() / 2,
            getWidth(), getHeight(), scale, scale, countFall * -15f);
    }

    public void dispose() {
        texture.dispose();
    }

    // Getters và Setters
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

    public void setScale(float scale) {
        this.scale = scale;
    }

    public float getVelocityY() {
        return velocityY;
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

    public float getScaledWidth() {
        return getWidth() * scale;
    }

    public float getScaledHeight() {
        return getHeight() * scale;
    }

    /**
     * Kiểm tra va chạm với rectangle khác
     */
    public boolean isColliding(Rectangle other) {
        return hitbox.overlaps(other);
    }

    /**
     * Kiểm tra va chạm với bird khác
     */
    public boolean isColliding(Bird other) {
        return hitbox.overlaps(other.getHitbox());
    }
}
