package com.star.app.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import static com.star.game.ScreenManager.*;

public class Hero {
    private final float ROTATE_SPEED = 180f;
    private final float FORWARD_MAX_SPEED = 240f;
    private final float BACKWARD_MAX_SPEED = 120f;
    private final float FORWARD_POWER = 120f;
    private final float BACKWARD_POWER = 60f;
    private final float BOUND_BREAK_FACTOR = 0.5f;
    private final float FRICTION_BREAK = 120f;

    private final int KEY_FORWARD = Input.Keys.W;
    private final int KEY_BACK = Input.Keys.S;
    private final int KEY_LEFT = Input.Keys.A;
    private final int KEY_RIGHT = Input.Keys.D;
    private final int KEY_FIRE = Input.Keys.SPACE;

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
        checkKeyPressed(dt);
        frictionBreak(dt);
        position.mulAdd(velocity, dt);
        checkBounds();
    }

    private void checkKeyPressed(float dt) {
        float directionX = (float) Math.cos(Math.toRadians(angle));
        float directionY = (float) Math.sin(Math.toRadians(angle));
        boolean isForwardMoving = velocity.dot(directionX, directionY) >= 0;
        if (Gdx.input.isKeyPressed(KEY_LEFT)) {
            angle += ROTATE_SPEED * dt;
            angle %= 360;
        }
        if (Gdx.input.isKeyPressed(KEY_RIGHT)) {
            angle -= ROTATE_SPEED * dt;
            if (angle < 0) angle += 360;
        }
        if (Gdx.input.isKeyPressed(KEY_FORWARD)) {
            velocity.add(directionX * FORWARD_POWER * dt, directionY * FORWARD_POWER * dt);
            if (velocity.len() > FORWARD_MAX_SPEED && isForwardMoving)
                velocity = velocity.nor().scl(FORWARD_MAX_SPEED);
        }
        if (Gdx.input.isKeyPressed(KEY_BACK)) {
            velocity.sub(directionX * BACKWARD_POWER * dt, directionY * BACKWARD_POWER * dt);
            if (velocity.len() > BACKWARD_MAX_SPEED && !isForwardMoving)
                velocity = velocity.nor().scl(BACKWARD_MAX_SPEED);
        }
    }

    private void frictionBreak(float dt) {
        if (!Gdx.input.isKeyPressed(KEY_FORWARD) && !Gdx.input.isKeyPressed(KEY_BACK)) {
            if (velocity.len() < FRICTION_BREAK * dt) velocity.set(0, 0);
            else velocity = velocity.sub(velocity.cpy().nor().scl(FRICTION_BREAK * dt));
        }
    }

    private void checkBounds() {
        if (position.x < textureW / 2f) {
            position.x = textureW / 2f;
            velocity.x *= -BOUND_BREAK_FACTOR;
        } else if (position.x > SCREEN_WIDTH - textureW / 2f) {
            position.x = SCREEN_WIDTH - textureW / 2f;
            velocity.x *= -BOUND_BREAK_FACTOR;
        }
        if (position.y < textureH / 2f) {
            position.y = textureH / 2f;
            velocity.y *= -BOUND_BREAK_FACTOR;
        } else if (position.y > SCREEN_HEIGHT - textureH / 2f) {
            position.y = SCREEN_HEIGHT - textureH / 2f;
            velocity.y *= -BOUND_BREAK_FACTOR;
        }
    }
}
