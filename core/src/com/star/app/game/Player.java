package com.star.app.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.star.app.game.helpers.Piloting;
import com.star.app.game.ships.Ship;
import com.star.app.game.ships.ShipClassic;

public class Player implements Piloting {
    private final int KEY_FORWARD = Input.Keys.UP;
    private final int KEY_BACK = Input.Keys.DOWN;
    private final int KEY_LEFT = Input.Keys.LEFT;
    private final int KEY_RIGHT = Input.Keys.RIGHT;
    private final int KEY_SHOT = Input.Keys.Z;

    private Ship ship;

    public Player(GameController gameController) {
        ship = new ShipClassic(gameController, this);
    }

    public void update(float dt) {
        ship.update(dt);
    }

    public void render(SpriteBatch batch) {
        ship.render(batch);
    }

    public Ship getShip() {
        return ship;
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
