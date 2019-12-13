package com.star.app.game.ships;

import com.badlogic.gdx.math.MathUtils;
import com.star.app.game.GameController;
import com.star.app.game.helpers.Piloting;
import com.star.app.game.ships.parts.Exhaust;
import com.star.app.game.ships.parts.Gun;
import com.star.app.game.ships.parts.Weapon;
import com.star.app.utils.Assets;

public class ShipFactory {
    // TODO: 07.12.2019 оптимизировать
    public static Ship getShip(ShipTypes type, GameController gameController, Piloting pilot, Updates updates, boolean isPlayer) {
        Ship ship = null;
        float durabilityUpdate = 0;
        float damageUpdate = 0;
        float rotationSpeedUpdate = 0;
        float forwardSpeedUpdate = 0;
        float enemyWeakFactor;
        if (isPlayer) enemyWeakFactor = 1;
        else enemyWeakFactor = 0.7f;
        if (updates != null) {
            durabilityUpdate = updates.getLevelEffect(Updates.Types.MAX_HEALTH, true);
            damageUpdate = updates.getLevelEffect(Updates.Types.DAMAGE, true);
            rotationSpeedUpdate = updates.getLevelEffect(Updates.Types.ROTATION_SPEED, true);
            forwardSpeedUpdate = updates.getLevelEffect(Updates.Types.FORWARD_SPEED, true);
        }
        switch (type) {
            case FIGHTER:
                ship = new Ship(gameController, pilot, enemyWeakFactor * 100f + durabilityUpdate,
                        enemyWeakFactor * 240f + forwardSpeedUpdate, enemyWeakFactor * 120f,
                        enemyWeakFactor * 30f, enemyWeakFactor * 60f,
                        enemyWeakFactor * 90f + rotationSpeedUpdate);
                ship.setTextureSettings(Assets.getInstance().getTextureAtlas().findRegion("fighter"),
                        23, 32);
                ship.setExhausts(
                        new Exhaust(gameController, -20, 28, Exhaust.Flags.LEFT_THRUST),
                        new Exhaust(gameController, -20, -28, Exhaust.Flags.RIGHT_THRUST)
                );
                ship.setWeapon(new Weapon(gameController, ship, 0.1f, 1000, enemyWeakFactor * 1 + damageUpdate),5);
                ship.getWeapon().setGunSeparate(new Gun(1, -27, 600f, 0));
                ship.getWeapon().setGunSeparate(new Gun(1, 27, 600f, 0));
                break;
            case HORSESHOE:
                ship = new Ship(gameController, pilot, enemyWeakFactor * 100f + durabilityUpdate,
                        enemyWeakFactor * 300f + forwardSpeedUpdate, enemyWeakFactor * 200f,
                        enemyWeakFactor * 50f, enemyWeakFactor * 70f,
                        enemyWeakFactor * 90f + rotationSpeedUpdate);
                ship.setTextureSettings(Assets.getInstance().getTextureAtlas().findRegion("horseshoe"),
                        23, 32);
                ship.setExhausts(new Exhaust(gameController, -20, 18, Exhaust.Flags.LEFT_THRUST),
                        new Exhaust(gameController, -20, -18, Exhaust.Flags.RIGHT_THRUST));
                ship.setWeapon(new Weapon(gameController, ship, 0.1f, 1000, enemyWeakFactor * 1 + damageUpdate),3);
                ship.getWeapon().setGunGroup(
                        new Gun(32, -14, 600f, 0),
                        new Gun(32, 14, 600f, 0)
                );
                break;
            case TRIDENT:
                ship = new Ship(gameController, pilot, enemyWeakFactor * 100f + durabilityUpdate,
                        enemyWeakFactor * 240f + forwardSpeedUpdate, enemyWeakFactor * 150f,
                        enemyWeakFactor * 50f, enemyWeakFactor * 80f,
                        enemyWeakFactor * 180f + rotationSpeedUpdate);
                ship.setTextureSettings(Assets.getInstance().getTextureAtlas().findRegion("trident"),
                        40, 32);
                ship.setExhausts(
                        new Exhaust(gameController, -37, 16, Exhaust.Flags.LEFT_THRUST),
                        new Exhaust(gameController, -27, -16, Exhaust.Flags.RIGHT_THRUST)
                );
                ship.setWeapon(new Weapon(gameController, ship, 0.1f, 2000, enemyWeakFactor * 1 + damageUpdate),2);
                ship.getWeapon().setGunSeparate(new Gun(15, -12, 600f, -10));
                ship.getWeapon().setGunSeparate(new Gun(15, -6, 600f, 5));
                ship.getWeapon().setGunSeparate(new Gun(15, 0, 600f, 0));
                ship.getWeapon().setGunSeparate(new Gun(15, 6, 600f, 5));
                ship.getWeapon().setGunSeparate(new Gun(15, 12, 600f, 10));
                break;
        }
        return ship;
    }

    public static Ship getShip(ShipTypes type, GameController gameController, Piloting pilot) {
        return getShip(type, gameController, pilot, null, false);
    }

    public static Ship getRandomShip(GameController gameController, Piloting pilot, Updates updates, boolean isPlayer) {
        int index = MathUtils.random(ShipTypes.values().length - 1);
        return getShip(ShipTypes.values()[index], gameController, pilot, updates, isPlayer);
    }

    public static Ship getRandomShip(GameController gameController, Piloting pilot) {
        return getRandomShip(gameController, pilot, null, false);
    }
}
