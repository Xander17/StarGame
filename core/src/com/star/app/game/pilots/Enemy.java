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
    private int[] visibleIndex;

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
        visibleIndex = ship.getDistIndex();
        Vector2 playerPos = gameController.getPlayer().getShip().getPosition();
        Vector2 shipPos = ship.getPosition();
        float shipX = shipPos.x + gameController.SPACE_WIDTH * visibleIndex[0];
        float shipY = shipPos.y + gameController.SPACE_HEIGHT * visibleIndex[1];
        targetVector.set(playerPos.x - shipX,
                playerPos.y - shipY);
        float ang = (targetVector.angle() - ship.getAngle()) % 360;
        if (ang < 0) ang += 360;
        if (ang <= 180) {
            ship.turnLeft(dt, ang);
        } else {
            ship.turnRight(dt, ang);
        }
        float dst = Vector2.dst(playerPos.x, playerPos.y, shipX, shipY);
        if ((ang < 90 || ang > 270) && dst >= SAFE_DIST) {
            ship.moveForward(dt);
            isTrust = true;
        }

        if(dst<=SHOT_DIST) ship.fire(false);

        return isTrust;
    }

    public void update(float dt) {
        ship.update(dt);
    }

    public void render(SpriteBatch batch) {
        ship.renderEnemy(batch, visibleIndex);
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
