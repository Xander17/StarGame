package com.star.app.game.bullets;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.star.app.game.helpers.Collisional;
import com.star.app.game.helpers.Poolable;
import com.star.app.utils.Assets;

import static com.star.app.screen.ScreenManager.SCREEN_HEIGHT;
import static com.star.app.screen.ScreenManager.SCREEN_WIDTH;

public class Bullet implements Poolable {

    private final float START_DAMAGE = 1;

    private TextureRegion texture;
    private int textureW;
    private int textureH;
    private Vector2 position;
    private float angle;
    private Vector2 velocity;
    private float damage;
    private boolean isActive;

    @Override
    public boolean isActive() {
        return isActive;
    }

    Bullet() {
        this.texture = Assets.getInstance().getTextureAtlas().findRegion("bullet");
        this.textureW = texture.getRegionWidth();
        this.textureH = texture.getRegionHeight();
        position = new Vector2(0, 0);
        velocity = new Vector2(0, 0);
        angle = 0;
        damage = START_DAMAGE;
        isActive = false;
    }

    void activate(float x, float y, float angle, float velocityX, float velocityY) {
        position.set(x, y);
        this.angle = angle;
        velocity.set(velocityX, velocityY);
        isActive = true;
    }

    private void deactivate() {
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

        return obj.takeDamage(damage);
    }

    private float getHitPointX() {
        return position.x + MathUtils.cosDeg(angle) * textureW / 2f;
    }

    private float getHitPointY() {
        return position.y + MathUtils.sinDeg(angle) * textureW / 2f;
    }
}
