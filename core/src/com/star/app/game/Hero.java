package com.star.app.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;

import static com.star.game.ScreenManager.*;

public class Hero {
    private final float ROTATE_SPEED = 90f;
    private final float FORWARD_MAX_VELOCITY = 240f;
    private final float BACKWARD_MAX_VELOCITY = 120f;
    private final float FORWARD_POWER = 120f;
    private final float BACKWARD_POWER = 60f;
    private final float BOUND_BREAK_FACTOR = 0.5f;
    private final float FRICTION_BREAK = 120f;
    private final float SHOOT_DELAY_MIN = 0.1f;

    private final float SHOT_VELOCITY = 600f;

    private final int KEY_FORWARD = Input.Keys.UP;
    private final int KEY_BACK = Input.Keys.DOWN;
    private final int KEY_LEFT = Input.Keys.LEFT;
    private final int KEY_RIGHT = Input.Keys.RIGHT;
    private final int KEY_SHOT = Input.Keys.Z;

    private GameController gameController;
    private Texture texture;
    private int textureW;
    private int textureH;
    private Vector2 position;
    private Vector2 velocity;
    private float angle;
    private boolean isRightGun;
    private float shootDelay;
    private float[] massCenterXY;
    private float[] rightGunPosition;
    private float[] leftGunPosition;
    private Circle hitBox;

    public Vector2 getVelocity() {
        return velocity;
    }

    public Hero(GameController gameController) {
        this.gameController = gameController;
        this.texture = new Texture("ships/ship.png");
        this.position = new Vector2(SCREEN_WIDTH / 2f, SCREEN_HEIGHT / 2f);
        this.velocity = new Vector2(0, 0);
        this.angle = 0.0f;
        this.isRightGun = true;
        // TODO: 20.11.2019 вынести настройки корабля отдельно
        textureW = 64;
        textureH = 64;
        massCenterXY = new float[]{23, 32};
        rightGunPosition = new float[]{10, -27};
        leftGunPosition = new float[]{10, 27};
        hitBox = new Circle();
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, position.x - massCenterXY[0], position.y - massCenterXY[1],
                massCenterXY[0], massCenterXY[1], textureW, textureH, 1, 1, angle,
                0, 0, textureW, textureH, false, false);
    }

    public void update(float dt) {
        shootDelay += dt;
        control(dt);
        frictionBreak(dt);
        position.mulAdd(velocity, dt);
        checkBounds();
    }

    private void shooting() {
        float gunX, gunY;
        if (isRightGun) {
            gunX = position.x + getShipSystemX(rightGunPosition);
            gunY = position.y + getShipSystemY(rightGunPosition);
        } else {
            gunX = position.x + getShipSystemX(leftGunPosition);
            gunY = position.y + getShipSystemY(leftGunPosition);
        }
        gameController.getBulletController().createNew(gunX, gunY, angle,
                (float) Math.cos(Math.toRadians(angle)) * SHOT_VELOCITY + velocity.x,
                (float) Math.sin(Math.toRadians(angle)) * SHOT_VELOCITY + velocity.y);
        shootDelay = 0;
        isRightGun = !isRightGun;
    }

    public void tryShooting() {
        if (shootDelay < SHOOT_DELAY_MIN) return;
        shooting();
    }

    private void control(float dt) {
        if (Gdx.input.isKeyPressed(KEY_SHOT)) {
            tryShooting();
        }
        if (Gdx.input.isKeyPressed(KEY_LEFT)) {
            turnLeft(dt);
        }
        if (Gdx.input.isKeyPressed(KEY_RIGHT)) {
            turnRight(dt);
        }
        if (Gdx.input.isKeyPressed(KEY_FORWARD)) {
            moveForward(dt);
        }
        if (Gdx.input.isKeyPressed(KEY_BACK)) {
            moveBack(dt);
        }
    }

    private void turnLeft(float dt) {
        angle += ROTATE_SPEED * dt;
        if (angle >= 360) angle %= 360;
    }

    private void turnRight(float dt) {
        angle -= ROTATE_SPEED * dt;
        if (angle < 0) angle = angle % 360 + 360;
    }

    private void moveForward(float dt) {
        float directionX = (float) Math.cos(Math.toRadians(angle));
        float directionY = (float) Math.sin(Math.toRadians(angle));
        boolean isForwardMoving = velocity.dot(directionX, directionY) >= 0;
        velocity.add(directionX * FORWARD_POWER * dt, directionY * FORWARD_POWER * dt);
        if (velocity.len() > FORWARD_MAX_VELOCITY && isForwardMoving)
            velocity.nor().scl(FORWARD_MAX_VELOCITY);
    }

    private void moveBack(float dt) {
        float directionX = (float) Math.cos(Math.toRadians(angle));
        float directionY = (float) Math.sin(Math.toRadians(angle));
        boolean isForwardMoving = velocity.dot(directionX, directionY) >= 0;
        velocity.sub(directionX * BACKWARD_POWER * dt, directionY * BACKWARD_POWER * dt);
        if (velocity.len() > BACKWARD_MAX_VELOCITY && !isForwardMoving)
            velocity.nor().scl(BACKWARD_MAX_VELOCITY);
    }

    private void frictionBreak(float dt) {
        if (!Gdx.input.isKeyPressed(KEY_FORWARD) && !Gdx.input.isKeyPressed(KEY_BACK)) {
            if (velocity.len() < FRICTION_BREAK * dt) velocity.set(0, 0);
            else {
                float skl = FRICTION_BREAK * dt / velocity.len();
                velocity.mulAdd(velocity, -skl);
            }
        }
    }

    private float getShipSystemX(float[] coords) {
        return (float) (Math.cos(Math.toRadians(angle)) * coords[0] - Math.sin(Math.toRadians(angle)) * coords[1]);
    }

    private float getShipSystemY(float[] coords) {
        return (float) (Math.sin(Math.toRadians(angle)) * coords[0] + Math.cos(Math.toRadians(angle)) * coords[1]);
    }

    private float[] getTextureCenterCoords() {
        return new float[]{textureW / 2f - massCenterXY[0], textureH / 2f - massCenterXY[1]};
    }

    private void checkBounds() {
        float offsetX = getShipSystemX(getTextureCenterCoords());
        float offsetY = getShipSystemY(getTextureCenterCoords());
        if (position.x + offsetX < textureW / 2f) {
            position.x = textureW / 2f - offsetX;
            velocity.x *= -BOUND_BREAK_FACTOR;
        } else if (position.x + offsetX > SCREEN_WIDTH - textureW / 2f) {
            position.x = SCREEN_WIDTH - textureW / 2f - offsetX;
            velocity.x *= -BOUND_BREAK_FACTOR;
        }
        if (position.y + offsetY < textureH / 2f) {
            position.y = textureH / 2f - offsetY;
            velocity.y *= -BOUND_BREAK_FACTOR;
        } else if (position.y + offsetY > SCREEN_HEIGHT - textureH / 2f) {
            position.y = SCREEN_HEIGHT - textureH / 2f - offsetY;
            velocity.y *= -BOUND_BREAK_FACTOR;
        }
    }

    public Circle getHitBox() {
        float[] coords = getTextureCenterCoords();
        hitBox.set(coords[0], coords[1], textureH / 2f);
        return hitBox;
    }
}
