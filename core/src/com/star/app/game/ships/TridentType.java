package com.star.app.game.ships;

import com.star.app.game.GameController;
import com.star.app.game.helpers.Piloting;
import com.star.app.utils.Assets;

public class TridentType extends Ship {

    private final float SHOT_ANGLE_OFFSET = 5f;

    private float[][] gunsPosition;
    private int gunIndex, gunIndexOffset;

    TridentType(GameController gameController, Piloting pilot) {
        super(gameController, pilot, 100f, 240f, 120f, 150f, 80f, 140f, 180f, 0.1f, 600f);
        texture = Assets.getInstance().getTextureAtlas().findRegion("trident");
        textureW = texture.getRegionWidth();
        textureH = texture.getRegionHeight();
        massCenterXY = new float[]{40, 32};
        gunsPosition = new float[][]{{15, -12}, {15, -6}, {15, 0}, {15, 6}, {15, 12}};
        gunIndex = 0;
        gunIndexOffset = 1;
    }

    @Override
    protected void shooting() {
        engageBullet(gunsPosition[gunIndex], SHOT_ANGLE_OFFSET * (gunIndex - 1));
        if (gunIndex == 0) {
            gunIndex++;
            gunIndexOffset = 1;
        } else if (gunIndex == gunsPosition.length-1) {
            gunIndex--;
            gunIndexOffset = -1;
        } else gunIndex += gunIndexOffset;
    }
}
