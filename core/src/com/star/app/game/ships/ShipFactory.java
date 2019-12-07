package com.star.app.game.ships;

import com.badlogic.gdx.math.MathUtils;
import com.star.app.game.GameController;
import com.star.app.game.helpers.Piloting;

public class ShipFactory {
    private ShipFactory() {
    }

    public static Ship getShip(ShipTypes type, GameController gameController, Piloting pilot) {
        switch (type) {
            case FIGHTER:
                return new FighterType(gameController, pilot);
            case HORSESHOE:
                return new HorseshoeType(gameController, pilot);
            case TRIDENT:
                return new TridentType(gameController, pilot);
        }
        return null;
    }

    public static Ship getRandomShip(GameController gameController, Piloting pilot) {
        int index = MathUtils.random(ShipTypes.values().length - 1);
        return getShip(ShipTypes.values()[index], gameController, pilot);
    }
}
