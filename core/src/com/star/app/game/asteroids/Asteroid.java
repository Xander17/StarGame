package com.star.app.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.star.app.game.helpers.Destroyable;
import com.star.app.game.helpers.Poolable;

import static com.star.app.screen.ScreenManager.SCREEN_HEIGHT;
import static com.star.app.screen.ScreenManager.SCREEN_WIDTH;

public class Asteroid implements Poolable, Destroyable {
    private final float SPEED_MIN = 100f;
    private final float SPEED_MAX = 30f;
    private final float SCALE_MIN = 0.2f;
    private final float SCALE_MAX = 0.4f;
    private final float ROTATION_SPEED_MIN = 60f;
    private final float ROTATION_SPEED_MAX = 10f;
    private final float ANGLE_NO_CREATE = 15f;

    private TextureRegion[] asteroidTypes = new TextureRegion[]{
            new TextureRegion("asteroid1"),
            new TextureRegion("asteroid2"),
            new TextureRegion("asteroid3"),
            new TextureRegion("asteroid4"),
            new TextureRegion("asteroid5"),
            new TextureRegion("asteroid6")
    };

    private TextureRegion texture;
    private Vector2 position;
    private Vector2 velocity;
    private float rotationAngle;
    private float rotationSpeed;
    private float scale;
    private int textureW;
    private int textureH;
    private boolean isActive;

    public Asteroid() {
        position = new Vector2(0, 0);
        velocity = new Vector2(0, 0);
        isActive = false;
    }

    public void activate() {
        float speed = MathUtils.random(SPEED_MIN, SPEED_MAX);
        float angle = 0;
        while (angle % 90 < ANGLE_NO_CREATE || angle % 90 > (90 - ANGLE_NO_CREATE)) {
            angle = MathUtils.random(0, 360);
        }
        activate(getRandomStartPoint(), MathUtils.random(SCALE_MIN, SCALE_MAX),
                MathUtils.randomSign() * (float) Math.cos(Math.toRadians(angle)) * speed,
                MathUtils.randomSign() * (float) Math.sin(Math.toRadians(angle)) * speed);
    }

    public void activate(Vector2 position, float scale, float velocityX, float velocityY) {
        activate(position.x, position.y, scale, velocityX, velocityY);
    }

    public void activate(float x, float y, float scale, float velocityX, float velocityY) {
        this.texture = asteroidTypes[MathUtils.random(asteroidTypes.length - 1)];
        this.textureW = texture.getWidth();
        this.textureH = texture.getHeight();
        position.set(x, y);
        this.scale = scale;
        velocity.set(velocityX, velocityY);
        rotationAngle = MathUtils.random(0, 360);
        rotationSpeed = MathUtils.randomSign() * MathUtils.random(ROTATION_SPEED_MIN, ROTATION_SPEED_MAX);
        isActive = true;
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, position.x - textureW / 2f, position.y - textureH / 2f,
                textureW / 2f, textureH / 2f, textureW, textureH, scale, scale, rotationAngle,
                0, 0, textureW, textureH, false, false);
    }

    public void update(float dt) {
        position.x += velocity.x * dt;
        position.y += velocity.y * dt;
        if (position.x < -textureW / 2f * scale) position.x = SCREEN_WIDTH + textureW / 2f * scale;
        else if (position.x > SCREEN_WIDTH + textureW / 2f * scale) position.x = -textureW / 2f * scale;
        if (position.y < -textureH / 2f * scale) position.y = SCREEN_HEIGHT + textureH / 2f * scale;
        else if (position.y > SCREEN_HEIGHT + textureH / 2f * scale) position.y = -textureH / 2f * scale;

        rotationAngle += rotationSpeed * dt;
    }

    private Vector2 getRandomStartPoint() {
        if (MathUtils.random(1) == 0) return new Vector2(MathUtils.random(0, SCREEN_WIDTH), -textureH / 2f * scale);
        else return new Vector2(-textureW / 2f * scale, MathUtils.random(0, SCREEN_HEIGHT));
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    @Override
    public void destroy() {
        isActive = false;
    }

    @Override
    public Vector2 getPosition() {
        return position;
    }

    @Override
    public float getHitBoxRadius() {
        return Math.max(textureW, textureH) * scale / 2;
    }
}