package com.star.app.game.pilots;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.star.app.game.GameController;
import com.star.app.game.helpers.Piloting;
import com.star.app.game.ships.Ship;
import com.star.app.game.ships.ShipFactory;
import com.star.app.game.ships.ShipTypes;
import com.star.app.utils.Options;

public class Player implements Piloting {
    private final int START_LIVES = 2;
    private final int SCORE_DEAD_PENALTY = 20000;
    private final ShipTypes START_TYPE = ShipTypes.HORSESHOE;

    private GameController gameController;
    private Ship ship;
    private KeyControls keyControls;
    private PlayerStatistic playerStatistic;
    private int playerNumber;
    private int lives;
    private boolean deadStatus;
    private int cash;

    public void setDeadStatus(boolean status) {
        this.deadStatus = status;
        if (status) playerStatistic.add(PlayerStatistic.Stats.SCORE, -SCORE_DEAD_PENALTY);
    }

    public boolean isDead() {
        return deadStatus;
    }

    public Player(GameController gameController, int playerNumber) {
        this.gameController = gameController;
        this.playerNumber = playerNumber;
        this.keyControls = new KeyControls(Options.loadProperties(), "PLAYER" + playerNumber);
        this.ship = ShipFactory.getShip(START_TYPE, gameController, this);
        this.deadStatus = false;
        this.lives = START_LIVES;
        this.playerStatistic = new PlayerStatistic();
    }

    public void update(float dt) {
        if (deadStatus) return;
        if (ship.isShipDestoyed()) {
            ship = ShipFactory.getShip(START_TYPE, gameController, this);
            lives--;
            playerStatistic.inc(PlayerStatistic.Stats.LIVES_LOST);
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
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) gameController.setPaused(true);
        if (Gdx.input.isKeyPressed(keyControls.fire)) {
            ship.fire();
        }
        if (Gdx.input.isKeyPressed(keyControls.left)) {
            ship.turnLeft(dt);
        }
        if (Gdx.input.isKeyPressed(keyControls.right)) {
            ship.turnRight(dt);
        }
        if (Gdx.input.isKeyPressed(keyControls.forward)) {
            ship.moveForward(dt);
            isTrust = true;
        }
        if (Gdx.input.isKeyPressed(keyControls.backward)) {
            ship.moveBack(dt);
            isTrust = true;
        }
        return isTrust;
    }

    public int getCash() {
        return cash;
    }

    public void addCash(int amount) {
        this.cash += amount;
    }

    public PlayerStatistic getPlayerStatistic() {
        return playerStatistic;
    }
}
