package com.star.app.game.ships;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.star.app.game.GameController;
import com.star.app.game.helpers.Piloting;

import static com.star.game.ScreenManager.SCREEN_HEIGHT;
import static com.star.game.ScreenManager.SCREEN_WIDTH;

public abstract class Ship {

    private GameController gameController;
    private Piloting pilot;
    private Vector2 position;
    private Vector2 velocity;
    private Circle hitBox;
    private float angle;
    private float shootDelay;

    ShipType type;
    Texture texture;
    int textureW;
    int textureH;
    float[] massCenterXY;
    float[] rightGunPosition;
    float[] leftGunPosition;

    void resetShootDelay() {
        shootDelay = 0;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    Ship(GameController gameController, Piloting pilot) {
        this.gameController = gameController;
        this.pilot = pilot;
        this.position = new Vector2(SCREEN_WIDTH / 2f, SCREEN_HEIGHT / 2f);
        this.velocity = new Vector2(0, 0);
        this.angle = 0.0f;
        hitBox = new Circle();
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, position.x - massCenterXY[0], position.y - massCenterXY[1],
                massCenterXY[0], massCenterXY[1], textureW, textureH, 1, 1, angle,
                0, 0, textureW, textureH, false, false);
    }

    public void update(float dt) {
        shootDelay += dt;
        if (!pilot.control(dt)) frictionBreak(dt);
        position.mulAdd(velocity, dt);
        checkBounds();
    }

    protected abstract void shooting();

    public void tryShooting() {
        if (shootDelay < type.SHOOT_DELAY_MIN) return;
        shooting();
    }

    void engageBullet(float[] coords) {
        gameController.getBulletController().createNew(position.x + getShipSystemX(coords), position.y + getShipSystemY(coords), angle,
                (float) Math.cos(Math.toRadians(angle)) * type.SHOT_VELOCITY + velocity.x,
                (float) Math.sin(Math.toRadians(angle)) * type.SHOT_VELOCITY + velocity.y);
    }

    public void turnLeft(float dt) {
        angle += type.ROTATE_SPEED * dt;
        if (angle >= 360) angle %= 360;
    }

    public void turnRight(float dt) {
        angle -= type.ROTATE_SPEED * dt;
        if (angle < 0) angle = angle % 360 + 360;
    }

    public void moveForward(float dt) {
        float directionX = (float) Math.cos(Math.toRadians(angle));
        float directionY = (float) Math.sin(Math.toRadians(angle));
        boolean isForwardMoving = velocity.dot(directionX, directionY) >= 0;
        velocity.add(directionX * type.FORWARD_POWER * dt, directionY * type.FORWARD_POWER * dt);
        if (velocity.len() > type.FORWARD_MAX_VELOCITY && isForwardMoving)
            velocity.nor().scl(type.FORWARD_MAX_VELOCITY);
    }

    public void moveBack(float dt) {
        float directionX = (float) Math.cos(Math.toRadians(angle));
        float directionY = (float) Math.sin(Math.toRadians(angle));
        boolean isForwardMoving = velocity.dot(directionX, directionY) >= 0;
        velocity.sub(directionX * type.BACKWARD_POWER * dt, directionY * type.BACKWARD_POWER * dt);
        if (velocity.len() > type.BACKWARD_MAX_VELOCITY && !isForwardMoving)
            velocity.nor().scl(type.BACKWARD_MAX_VELOCITY);
    }

    private void frictionBreak(float dt) {
        if (velocity.len() < type.FRICTION_BREAK * dt) velocity.set(0, 0);
        else {
            float skl = type.FRICTION_BREAK * dt / velocity.len();
            velocity.mulAdd(velocity, -skl);
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
            velocity.x *= -type.BOUND_BREAK_FACTOR;
        } else if (position.x + offsetX > SCREEN_WIDTH - textureW / 2f) {
            position.x = SCREEN_WIDTH - textureW / 2f - offsetX;
            velocity.x *= -type.BOUND_BREAK_FACTOR;
        }
        if (position.y + offsetY < textureH / 2f) {
            position.y = textureH / 2f - offsetY;
            velocity.y *= -type.BOUND_BREAK_FACTOR;
        } else if (position.y + offsetY > SCREEN_HEIGHT - textureH / 2f) {
            position.y = SCREEN_HEIGHT - textureH / 2f - offsetY;
            velocity.y *= -type.BOUND_BREAK_FACTOR;
        }
    }

    public Circle getHitBox() {
        float[] coords = getTextureCenterCoords();
        hitBox.set(coords[0], coords[1], textureH / 2f);
        return hitBox;
    }
}
