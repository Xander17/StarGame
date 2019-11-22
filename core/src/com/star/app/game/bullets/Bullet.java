package com.star.app.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.star.app.game.helpers.Destroyable;
import com.star.app.game.helpers.Poolable;

import static com.star.app.screen.ScreenManager.SCREEN_HEIGHT;
import static com.star.app.screen.ScreenManager.SCREEN_WIDTH;

public class Bullet implements Poolable {
    private Texture texture;
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

    Bullet(Texture texture) {
        this.texture = texture;
        this.textureW = texture.getWidth();
        this.textureH = texture.getHeight();
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

    void deactivate() {
        isActive = false;
    }

    void update(float dt) {
        position.mulAdd(velocity, dt);
        if (position.x < -textureW / 2f || position.x > SCREEN_WIDTH + textureW / 2f ||
                position.y < -textureH / 2f || position.y > SCREEN_HEIGHT + textureH / 2f)
            deactivate();
    }

    void render(SpriteBatch batch) {
        batch.draw(texture, position.x - textureW / 2f, position.y - textureH / 2f,
                textureW / 2f, textureH / 2f, textureW, textureH,
                1, 1, angle, 0, 0, textureW, textureH, false, false);
    }

    boolean checkHit(Destroyable obj) {
        return position.dst(obj.getPosition()) <= obj.getHitBoxRadius();
    }
}
