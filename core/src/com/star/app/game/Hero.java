package com.star.app.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import static com.star.game.ScreenManager.*;

public class Hero {
    private final float SHIP_ROTATE_SPEED = 180f;
    private final float SHIP_FORWARD_SPEED = 240f;
    private final float SHIP_BACKWARD_SPEED = 120f;

    private Texture texture;
    private Vector2 position;
    private Vector2 velocity;
    private float angle;
    private int textureW;
    private int textureH;

    public Vector2 getVelocity() {
        return velocity;
    }

    public Hero() {
        this.texture = new Texture("ships/ship.png");
        this.position = new Vector2(SCREEN_WIDTH / 2f, SCREEN_HEIGHT / 2f);
        this.velocity = new Vector2(0, 0);
        this.angle = 0.0f;
        this.textureW = texture.getWidth();
        this.textureH = texture.getHeight();
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, position.x - textureW / 2f, position.y - textureH / 2f,
                textureW / 2f, textureH / 2f, textureW, textureH, 1, 1, angle,
                0, 0, textureW, textureH, false, false);
    }

    public void update(float dt) {
        velocity.set(0, 0);
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            angle += SHIP_ROTATE_SPEED * dt;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            angle -= SHIP_ROTATE_SPEED * dt;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            velocity.set((float) Math.cos(Math.toRadians(angle)) * SHIP_FORWARD_SPEED * dt,
                    (float) Math.sin(Math.toRadians(angle)) * SHIP_FORWARD_SPEED * dt);
            position.x += velocity.x;
            position.y += velocity.y;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            velocity.set(-(float) Math.cos(Math.toRadians(angle)) * SHIP_BACKWARD_SPEED * dt,
                    -(float) Math.sin(Math.toRadians(angle)) * SHIP_BACKWARD_SPEED * dt);
            position.x += velocity.x;
            position.y += velocity.y;
        }
        if (position.x < textureW / 2f) {
            float oldX = position.x;
            position.x = textureW / 2f;
            velocity.x -= oldX - position.x;
        }
        if (position.x > SCREEN_WIDTH - textureW / 2f) {
            float oldX = position.x;
            position.x = SCREEN_WIDTH - textureW / 2f;
            velocity.x -= oldX - position.x;
        }
        if (position.y < textureH / 2f) {
            float oldY = position.y;
            position.y = textureH / 2f;
            velocity.y -= oldY - position.y;
        }
        if (position.y > SCREEN_HEIGHT - textureH / 2f) {
            float oldY = position.y;
            position.y = SCREEN_HEIGHT - textureH / 2f;
            velocity.y -= oldY - position.y;
        }
    }
}
