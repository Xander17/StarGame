package com.star.app.game.ships;

import com.badlogic.gdx.graphics.Texture;
import com.star.app.game.GameController;
import com.star.app.game.helpers.Piloting;

public class TridentType extends Ship {

    private final float SHOT_ANGLE_OFFSET = 10f;

    private float[][] gunsPosition;
    private int gunIndex, gunIndexOffset;

    TridentType(GameController gameController, Piloting pilot) {
        super(gameController, pilot, 240f, 120f, 150f, 80f, 140f, 180f, 0.1f, 600f);
        texture = new Texture("ships/trident.png");
        textureW = texture.getWidth();
        textureH = texture.getHeight();
        massCenterXY = new float[]{40, 32};
        gunsPosition = new float[][]{{15, -12}, {15, 0}, {15, 12}};
        gunIndex = 0;
        gunIndexOffset = 1;
    }

    @Override
    protected void shooting() {
        engageBullet(gunsPosition[gunIndex],  SHOT_ANGLE_OFFSET * (gunIndex - 1));
        if (gunIndex == 0) {
            gunIndex++;
            gunIndexOffset = 1;
        } else if (gunIndex == 2) {
            gunIndex--;
            gunIndexOffset = -1;
        } else gunIndex += gunIndexOffset;
    }
}