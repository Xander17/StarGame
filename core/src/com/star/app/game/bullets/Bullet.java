package com.star.app.game.bullets;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.star.app.game.helpers.Collisional;
import com.star.app.game.helpers.Poolable;

import static com.star.app.screen.ScreenManager.SCREEN_HEIGHT;
import static com.star.app.screen.ScreenManager.SCREEN_WIDTH;

public class Bullet implements Poolable {
    private final int BASE_DAMAGE = 1;

    private TextureRegion texture;
    private int textureW;
    private int textureH;
    private Vector2 position;
    float angle;
    private Vector2 velocity;
    private boolean isActive;

    @Override
    public boolean isActive() {
        return isActive;
    }

    Bullet(TextureRegion texture) {
        this.texture = texture;
        this.textureW = texture.getRegionWidth();
        this.textureH = texture.getRegionHeight();
        position = new Vector2(0, 0);
        velocity = new Vector2(0, 0);
        isActive = false;
    }

    void activate(float x, float y, float angle, float velocityX, float velocityY) {
        position.set(x, y);
        this.angle = angle;
        velocity.set(velocityX, velocityY);
        isActive = true;
    }

    public void deactivate() {
        isActive = false;
    }

    public void update(float dt) {
        position.mulAdd(velocity, dt);
        if (position.x < -textureW / 2f || position.x > SCREEN_WIDTH + textureW / 2f ||
                position.y < -textureH / 2f || position.y > SCREEN_HEIGHT + textureH / 2f)
            deactivate();
    }

    void render(SpriteBatch batch) {
        batch.draw(texture, position.x - textureW / 2f, position.y - textureH / 2f,
                textureW / 2f, textureH / 2f, textureW, textureH,
                1, 1, angle);
    }

    public boolean checkHit(Collisional obj) {
        return obj.getHitBox().contains(getHitPointX(), getHitPointY());
    }

    public boolean damageTarget(Collisional obj) {
        deactivate();
        return obj.takeDamage(BASE_DAMAGE);
    }

    public float getHitPointX() {
        return position.x + (float) Math.cos(Math.toRadians(angle)) * textureW / 2f;
    }

    public float getHitPointY() {
        return position.y + (float) Math.sin(Math.toRadians(angle)) * textureW / 2f;
    }
}
