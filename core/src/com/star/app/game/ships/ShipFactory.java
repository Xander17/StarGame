package com.star.app.game.ships;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.star.app.game.GameController;
import com.star.app.game.helpers.Piloting;
import com.star.app.game.ships.updates.Updates;
import com.star.app.utils.Assets;

public class ShipFactory {

    public static Ship getShip(ShipTypes type, GameController gameController, Piloting pilot, Updates updates) {
        Ship ship = null;
        float durabilityUpdate = 0;
        float damageUpdate = 0;
        if (updates != null) {
            durabilityUpdate = updates.getLevelEffect(Updates.Types.MAX_HEALTH, true);
            damageUpdate = updates.getLevelEffect(Updates.Types.DAMAGE, true);
        }
        switch (type) {
            case FIGHTER:
                ship = new Ship(gameController, pilot, 100f + durabilityUpdate, 240f, 120f, 120f, 80f, 60f, 90f);
                ship.setTextureSettings(Assets.getInstance().getTextureAtlas().findRegion("fighter"),
                        23, 32, new Vector2[]{
                                new Vector2(-20, 28),
                                new Vector2(-20, -28)
                        });
                ship.setWeapon(new Weapon(gameController, ship, 0.1f, 1000, 1 + damageUpdate));
                ship.getWeapon().setGunSeparate(new Gun(1, -27, 600f, 0));
                ship.getWeapon().setGunSeparate(new Gun(1, 27, 600f, 0));
                break;
            case HORSESHOE:
                ship = new Ship(gameController, pilot, 100f, 300f, 100f, 200f, 80f, 70f, 90f);
                ship.setTextureSettings(Assets.getInstance().getTextureAtlas().findRegion("horseshoe"),
                        23, 32, new Vector2[]{
                                new Vector2(-20, 18),
                                new Vector2(-20, -18)
                        });
                ship.setWeapon(new Weapon(gameController, ship, 0.1f, 1000, 1 + damageUpdate));
                ship.getWeapon().setGunGroup(new Gun[]{
                        new Gun(32, -14, 600f, 0),
                        new Gun(32, 14, 600f, 0)
                });
                break;
            case TRIDENT:
                ship = new Ship(gameController, pilot, 100f, 240f, 120f, 150f, 100f, 80f, 180f);
                ship.setTextureSettings(Assets.getInstance().getTextureAtlas().findRegion("trident"),
                        40, 32, new Vector2[]{
                                new Vector2(-37, 16),
                                new Vector2(-37, -16)
                        });
                ship.setWeapon(new Weapon(gameController, ship, 0.1f, 2000, 1 + damageUpdate));
                ship.getWeapon().setGunSeparate(new Gun(15, -12, 600f, -10));
                ship.getWeapon().setGunSeparate(new Gun(15, -6, 600f, 5));
                ship.getWeapon().setGunSeparate(new Gun(15, 0, 600f, 0));
                ship.getWeapon().setGunSeparate(new Gun(15, 6, 600f, 5));
                ship.getWeapon().setGunSeparate(new Gun(15, 12, 600f, 10));
                break;
        }
        return ship;
    }

    public static Ship getRandomShip(GameController gameController, Piloting pilot, Updates updates) {
        int index = MathUtils.random(ShipTypes.values().length - 1);
        return getShip(ShipTypes.values()[index], gameController, pilot, updates);
    }
}
