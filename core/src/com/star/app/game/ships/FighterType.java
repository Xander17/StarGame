package com.star.app.game.ships;

import com.badlogic.gdx.graphics.Texture;
import com.star.app.game.GameController;
import com.star.app.game.helpers.Piloting;

public class FighterType extends Ship {

    private boolean isRightGun;
    private float[] rightGunPosition;
    private float[] leftGunPosition;

    public FighterType(GameController gameController, Piloting pilot) {
        super(gameController, pilot, 240f, 120f, 120f, 60f, 120f, 90f, 0.1f, 600f);
        texture = new Texture("ships/fighter.png");
        textureW = texture.getWidth();
        textureH = texture.getHeight();
        massCenterXY = new float[]{23, 32};
        rightGunPosition = new float[]{1, -27};
        leftGunPosition = new float[]{1, 27};
    }

    @Override
    protected void shooting() {
        if (isRightGun) {
            engageBullet(rightGunPosition);
        } else {
            engageBullet(leftGunPosition);
        }
        isRightGun = !isRightGun;
    }
}
