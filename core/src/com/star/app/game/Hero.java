package com.star.app.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
    private final float SHOOT_DELAY_MIN = 1f;

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
    private Vector2 rightGunPosition;
    private Vector2 leftGunPosition;
    private float[] massCenter;

    public Vector2 getVelocity() {
        return velocity;
    }

    public Hero(GameController gameController) {
        this.gameController = gameController;
        this.texture = new Texture("ships/ship.png");
        this.position = new Vector2(SCREEN_WIDTH / 2f, SCREEN_HEIGHT / 2f);
        this.velocity = new Vector2(0, 0);
        this.angle = 0.0f;
        this.textureW = texture.getWidth();
        this.textureH = texture.getHeight();
        this.isRightGun = true;
        // TODO: 20.11.2019 вынести настройки корабля отдельно
        rightGunPosition = new Vector2(6, -26);
        leftGunPosition = new Vector2(6, 26);
        massCenter = new float[]{-9, 0};
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, position.x - textureW / 2f, position.y - textureH / 2f,
                textureW / 2f + massCenter[0], textureH / 2f + massCenter[1], textureW, textureH, 1, 1, angle,
                0, 0, textureW, textureH, false, false);
    }

    public void update(float dt) {
        shooting(dt);
        moving(dt);
        frictionBreak(dt);
        position.mulAdd(velocity, dt);
        checkBounds();
    }

    private void shooting(float dt) {
        if ((shootDelay += dt) < SHOOT_DELAY_MIN) return;
        if (Gdx.input.isKeyPressed(KEY_SHOT)) {
            float gunX, gunY;
            if (isRightGun) {
                gunX = rightGunPosition.x + position.x;
                gunY = rightGunPosition.y + position.y;
            } else {
                gunX = leftGunPosition.x + position.x;
                gunY = leftGunPosition.y + position.y;
            }
            gameController.getBulletController().createNew(gunX, gunY, angle,
                    (float) Math.cos(Math.toRadians(angle)) * SHOT_VELOCITY + velocity.x,
                    (float) Math.sin(Math.toRadians(angle)) * SHOT_VELOCITY + velocity.y);
            shootDelay = 0;
            isRightGun = !isRightGun;
        }
    }

    private void moving(float dt) {
        float directionX = (float) Math.cos(Math.toRadians(angle));
        float directionY = (float) Math.sin(Math.toRadians(angle));
        boolean isForwardMoving = velocity.dot(directionX, directionY) >= 0;
        if (Gdx.input.isKeyPressed(KEY_LEFT)) {
            angle += ROTATE_SPEED * dt;
            angle %= 360;
            gunPositionUpdate(ROTATE_SPEED * dt);
        }
        if (Gdx.input.isKeyPressed(KEY_RIGHT)) {
            angle -= ROTATE_SPEED * dt;
            if (angle < 0) angle += 360;
            gunPositionUpdate(-ROTATE_SPEED * dt);
        }
        if (Gdx.input.isKeyPressed(KEY_FORWARD)) {
            velocity.add(directionX * FORWARD_POWER * dt, directionY * FORWARD_POWER * dt);
            if (velocity.len() > FORWARD_MAX_VELOCITY && isForwardMoving)
                velocity.nor().scl(FORWARD_MAX_VELOCITY);
        }
        if (Gdx.input.isKeyPressed(KEY_BACK)) {
            velocity.sub(directionX * BACKWARD_POWER * dt, directionY * BACKWARD_POWER * dt);
            if (velocity.len() > BACKWARD_MAX_VELOCITY && !isForwardMoving)
                velocity.nor().scl(BACKWARD_MAX_VELOCITY);
        }
    }

    private void frictionBreak(float dt) {
        if (!Gdx.input.isKeyPressed(KEY_FORWARD) && !Gdx.input.isKeyPressed(KEY_BACK)) {
            if (velocity.len() < FRICTION_BREAK * dt) velocity.set(0, 0);
            else velocity.sub(velocity.cpy().nor().scl(FRICTION_BREAK * dt));
        }
    }

    private void gunPositionUpdate(float degrees) {
        leftGunPosition.rotate(degrees);
        rightGunPosition.rotate(degrees);
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
