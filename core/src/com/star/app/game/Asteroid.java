package com.star.app.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import static com.star.game.ScreenManager.*;

public class Asteroid {
    private final float SPEED_MIN = 100f;
    private final float SPEED_MAX = 30f;
    private final float SCALE_MIN = 0.2f;
    private final float SCALE_MAX = 0.4f;
    private final float ROTATION_SPEED_MIN = 60f;
    private final float ROTATION_SPEED_MAX = 10f;
    private final float ANGLE_NO_CREATE = 15f;

    private Texture[] asteroidTypes = {
            new Texture("asteroids/asteroid1.png"),
            new Texture("asteroids/asteroid2.png"),
            new Texture("asteroids/asteroid3.png"),
            new Texture("asteroids/asteroid4.png"),
            new Texture("asteroids/asteroid5.png"),
            new Texture("asteroids/asteroid6.png")
    };

    private Texture texture;
    private Vector2 position;
    private Vector2 velocity;
    private float rotationAngle;
    private float rotationSpeed;
    private float scale;
    private int textureW;
    private int textureH;

    public Asteroid() {
        texture = asteroidTypes[MathUtils.random(asteroidTypes.length - 1)];
        this.textureW = texture.getWidth();
        this.textureH = texture.getHeight();
        scale = MathUtils.random(SCALE_MIN, SCALE_MAX);
        position = getRandomStartPoint();
        float speed = MathUtils.random(SPEED_MIN, SPEED_MAX);
        float angle = 0;
        while (angle % 90 < ANGLE_NO_CREATE || angle % 90 > (90 - ANGLE_NO_CREATE)) {
            angle = MathUtils.random(0, 360);
        }
        velocity = new Vector2(MathUtils.randomSign() * (float) Math.cos(Math.toRadians(angle)) * speed,
                MathUtils.randomSign() * (float) Math.sin(Math.toRadians(angle)) * speed);
        rotationAngle = MathUtils.random(0, 360);
        rotationSpeed = MathUtils.randomSign() * MathUtils.random(ROTATION_SPEED_MIN, ROTATION_SPEED_MAX);
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

}
