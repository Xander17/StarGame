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

    private Ship ship;
    private int score;
    private int scoreView;
    private int lives;

    public Player(GameController gameController) {
        ship = ShipFactory.getShip(ShipTypes.HORSESHOE, gameController, this);
        score = 0;
        scoreView = 0;
        lives = START_LIVES;
    }

    public void update(float dt) {
        if (score - scoreView > 3000) scoreView += (score - scoreView) / 3f * dt;
        else scoreView += dt * 1000;
        if (scoreView > score) scoreView = score;
        ship.update(dt);
    }

    public void render(SpriteBatch batch) {
        ship.render(batch);
    }

    public Ship getShip() {
        return ship;
    }

    public int getScoreView() {
        return scoreView;
    }

    public void addScore(int score) {
        this.score += score;
    }

    public void subScore(int score) {
        this.score -= score;
        if (score < 0) score = 0;
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
