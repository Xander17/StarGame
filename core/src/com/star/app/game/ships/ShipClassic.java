package com.star.app.game.ships;

import com.badlogic.gdx.graphics.Texture;
import com.star.app.game.GameController;
import com.star.app.game.helpers.Piloting;

public class ShipClassic extends Ship {

    private boolean isRightGun;

    public ShipClassic(GameController gameController, Piloting pilot) {
        super(gameController, pilot);
        type = ShipType.CLASSIC;
        texture = new Texture("ships/ship.png");
        textureW = 64;
        textureH = 64;
        massCenterXY = new float[]{23, 32};
        rightGunPosition = new float[]{10, -27};
        leftGunPosition = new float[]{10, 27};
    }

    @Override
    protected void shooting() {
        if (isRightGun) {
            engageBullet(rightGunPosition);
        } else {
            engageBullet(leftGunPosition);
        }
        resetShootDelay();
        isRightGun = !isRightGun;
    }
}
