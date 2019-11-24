package com.star.app.game.ships;

import com.star.app.game.GameController;
import com.star.app.game.helpers.Piloting;
import com.star.app.utils.Assets;

public class FighterType extends Ship {

    private boolean isRightGun;
    private float[] rightGunPosition;
    private float[] leftGunPosition;

    public FighterType(GameController gameController, Piloting pilot) {
        super(gameController, pilot, 100f,240f, 120f, 120f, 60f, 120f, 90f, 0.1f, 600f);
        texture = Assets.getInstance().getTextureAtlas().findRegion("fighter");
        textureW = texture.getRegionWidth();
        textureH = texture.getRegionHeight();
        // TODO: 22.11.2019 Переделать расчеты точек относительно центра текстуры. На данный момент координаты относительно центра масс
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
