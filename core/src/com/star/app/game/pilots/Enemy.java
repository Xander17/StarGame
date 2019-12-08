package com.star.app.game.pilots;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.star.app.game.GameController;
import com.star.app.game.helpers.Piloting;
import com.star.app.game.helpers.Poolable;
import com.star.app.game.ships.Ship;
import com.star.app.game.ships.ShipFactory;

public class Enemy implements Piloting, Poolable {
    private Ship ship;
    private boolean isActive;
    private GameController gameController;
    private Vector2 targetVector;

    public Enemy(GameController gameController) {
        this.gameController = gameController;
        this.ship = ShipFactory.getRandomShip(gameController, this);
        targetVector = new Vector2(0, 0);
        isActive = false;
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
        final float SAFE_DIST = 200f;
        final float SHOT_DIST = 1000f;

        boolean isTrust = false;
        Vector2 playerPos = gameController.getPlayer().getShip().getPosition();
        Vector2 shipPos = ship.getRenderPosition();
        targetVector.set(playerPos.x - shipPos.x,
                playerPos.y - shipPos.y);
        float ang = (targetVector.angle() - ship.getAngle()) % 360;
        if (ang < 0) ang += 360;
        if (ang <= 180) {
            ship.turnLeft(dt, ang);
        } else {
            ship.turnRight(dt, ang);
        }
        float dst = playerPos.dst(shipPos);
        if ((ang < 90 || ang > 270) && dst >= SAFE_DIST) {
            ship.moveForward(dt);
            isTrust = true;
        }

        if(dst<=SHOT_DIST) ship.fire(false);

        return isTrust;
    }

    public void update(float dt) {
        ship.updateEnemy(dt);
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
