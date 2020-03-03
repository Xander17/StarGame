package com.star.app.game.pilots;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.star.app.game.GameController;
import com.star.app.game.helpers.GameTimer;
import com.star.app.game.helpers.Piloting;
import com.star.app.game.helpers.Poolable;
import com.star.app.game.ships.Ship;
import com.star.app.game.ships.ShipFactory;

public class Enemy implements Piloting, Poolable {

    private final float DETECTION_RADIUS = 1000f;
    private final float DETECTION_LOST_RADIUS = 2000f;
    private final float TIME_RANDOM_TRAVEL_MIN = 10f;
    private final float TIME_RANDOM_TRAVEL_MAX = 20f;

    private final float SAFE_DIST = 200f;
    private final float SHOT_DIST = 1000f;
    private final float SHOT_ANGLE = 10f;

    private Ship ship;
    private boolean isActive;
    private GameController gameController;
    private Vector2 targetVector;
    private GameTimer randomTargetTimer;
    private boolean detected;

    public Enemy(GameController gameController) {
        this.gameController = gameController;
        this.ship = ShipFactory.getRandomShip(gameController, this);
        targetVector = new Vector2(0, 0);
        isActive = false;
        detected = false;
        randomTargetTimer = new GameTimer(0);
    }

    public void activate() {
        ship.setRandomState();
        isActive = true;
    }

    private void deactivate() {
        isActive = false;
    }

    @Override
    public boolean control(float dt) {
        Vector2 playerPos = gameController.getPlayer().getShip().getPosition();
        Vector2 shipPos = ship.getRenderPosition();
        float dst = Vector2.dst(playerPos.x, playerPos.y, shipPos.x, shipPos.y);
        if ((dst > DETECTION_RADIUS && !detected) ||
                (dst > DETECTION_LOST_RADIUS && detected) ||
                gameController.getPlayer().isDead())
            return randomTarget(dt);
        else return playerTarget(dt, playerPos, shipPos);
    }

    private boolean playerTarget(float dt, Vector2 playerPos, Vector2 shipPos) {
        if (!detected) {
            detected = true;
            randomTargetTimer.disable();
        }
        targetVector.set(playerPos.x - shipPos.x, playerPos.y - shipPos.y);
        float angle = rotateToTarget(dt);
        fire(angle);
        return closingToTarget(dt, angle, true);
    }

    private boolean randomTarget(float dt) {
        if (randomTargetTimer.isReady()) setRandomTargetVector();
        float angle = rotateToTarget(dt);
        return closingToTarget(dt, angle, false);
    }

    private void setRandomTargetVector() {
        if (detected) detected = false;
        targetVector.set(1, 0);
        targetVector.setAngle(MathUtils.random(359));
        randomTargetTimer.reset(MathUtils.random(TIME_RANDOM_TRAVEL_MIN, TIME_RANDOM_TRAVEL_MAX));
    }

    private float rotateToTarget(float dt) {
        float angle = (targetVector.angle() - ship.getAngle()) % 360;
        if (angle < 0) angle += 360;
        if (angle <= 180) {
            ship.turnLeft(dt, angle);
        } else {
            ship.turnRight(dt, angle);
        }
        return angle;
    }

    private boolean closingToTarget(float dt, float angle, boolean onSafeDistance) {
        float dst = targetVector.len();
        if ((angle < 90 || angle > 270) &&
                (onSafeDistance && dst >= SAFE_DIST) || (!onSafeDistance && (angle < 90 || angle > 270))) {
            ship.moveForward(dt);
            return true;
        }
        return false;
    }

    private void fire(float angle) {
        if (targetVector.len() <= SHOT_DIST && Math.abs(angle) <= SHOT_ANGLE) ship.fire(false);
    }

    public void update(float dt) {
        ship.updateEnemy(dt);
        randomTargetTimer.update(dt);
    }

    public void render(SpriteBatch batch) {
        ship.renderEnemy(batch);
    }

    @Override
    public void setDeadStatus(boolean status) {
        if (status) deactivate();
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    public Ship getShip() {
        return ship;
    }
}
