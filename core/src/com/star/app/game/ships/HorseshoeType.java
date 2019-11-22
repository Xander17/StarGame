package com.star.app.game.ships;

import com.badlogic.gdx.graphics.Texture;
import com.star.app.game.GameController;
import com.star.app.game.helpers.Piloting;

public class HorseshoeType extends Ship {
    private float[] rightGunPosition;
    private float[] leftGunPosition;

    HorseshoeType(GameController gameController, Piloting pilot) {
        super(gameController, pilot, 300f, 100f, 200f, 50f, 100f, 90f, 0.1f, 600f);
        texture = new Texture("ships/horseshoe.png");
        textureW = texture.getWidth();
        textureH = texture.getHeight();
        massCenterXY = new float[]{23, 32};
        rightGunPosition = new float[]{32, -14};
        leftGunPosition = new float[]{32, 14};
    }

    @Override
    protected void shooting() {
        engageBullet(rightGunPosition);
        engageBullet(leftGunPosition);
    }
}
