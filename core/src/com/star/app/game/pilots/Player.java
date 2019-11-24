package com.star.app.game.pilots;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.star.app.game.GameController;
import com.star.app.game.helpers.Piloting;
import com.star.app.game.ships.Ship;
import com.star.app.game.ships.ShipFactory;
import com.star.app.game.ships.ShipTypes;

public class Player implements Piloting {
    private final int KEY_FORWARD = Input.Keys.UP;
    private final int KEY_BACK = Input.Keys.DOWN;
    private final int KEY_LEFT = Input.Keys.LEFT;
    private final int KEY_RIGHT = Input.Keys.RIGHT;
    private final int KEY_SHOT = Input.Keys.Z;

    private final int START_LIVES = 3;
    private final ShipTypes START_TYPE = ShipTypes.HORSESHOE;

    private GameController gameController;
    private Ship ship;
    private int lives;
    private boolean deadStatus;

    public void setDeadStatus(boolean status) {
        this.deadStatus = status;
        gameController.getStatistic().scoreDeadPenalty();
    }

    public boolean isDead() {
        return deadStatus;
    }

    public Player(GameController gameController) {
        this.gameController = gameController;
        ship = ShipFactory.getShip(START_TYPE, gameController, this);
        deadStatus = false;
        lives = START_LIVES;
    }

    public void update(float dt) {
        if (deadStatus) return;
        if (ship.isShipDestoyed()) {
            ship = ShipFactory.getShip(START_TYPE, gameController, this);
            lives--;
        }
        ship.update(dt);
    }

    public void render(SpriteBatch batch) {
        ship.render(batch);
    }

    public Ship getShip() {
        return ship;
    }

    public int getLives() {
        return lives;
    }

    @Override
    public boolean control(float dt) {
        boolean isTrust = false;
        if (Gdx.input.isKeyPressed(KEY_SHOT)) {
            ship.tryShooting();
        }
        if (Gdx.input.isKeyPressed(KEY_LEFT)) {
            ship.turnLeft(dt);
        }
        if (Gdx.input.isKeyPressed(KEY_RIGHT)) {
            ship.turnRight(dt);
        }
        if (Gdx.input.isKeyPressed(KEY_FORWARD)) {
            ship.moveForward(dt);
            isTrust = true;
        }
        if (Gdx.input.isKeyPressed(KEY_BACK)) {
            ship.moveBack(dt);
            isTrust = true;
        }
        return isTrust;
    }
}
