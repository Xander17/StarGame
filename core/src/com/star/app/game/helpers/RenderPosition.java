package com.star.app.game.helpers;

import com.badlogic.gdx.math.Vector2;
import com.star.app.game.GameController;

import static com.star.app.screen.ScreenManager.*;

public class RenderPosition extends Vector2 {

    private Vector2 position;
    private boolean isRenderable;
    private float[] tmpCoords;

    public RenderPosition(Vector2 position) {
        this.position = position;
        tmpCoords = new float[2];
    }

    public float[] recalculate(GameController gameController, Vector2 position, float halfWidth, float halfHeight) {
        calculate(gameController, position, halfWidth, halfHeight, false, 0, 0);
        return tmpCoords;
    }

    public void recalculate(GameController gameController, float halfWidth, float halfHeight) {
        recalculate(gameController, halfWidth, halfHeight, 0, 0);
    }

    public void recalculate(GameController gameController, float halfWidth, float halfHeight, float renderOffsetX, float renderOffsetY) {
        calculate(gameController, position, halfWidth, halfHeight, true, renderOffsetX, renderOffsetY);
        x = tmpCoords[0];
        y = tmpCoords[1];
    }

    private void calculate(GameController gameController, Vector2 position, float halfWidth, float halfHeight, boolean renderableUpdate, float renderOffsetX, float renderOffsetY) {
        Vector2 playerPosition = gameController.getPlayer().getShip().getPosition();
        float worldWidth = gameController.SPACE_WIDTH;
        float worldHeight = gameController.SPACE_HEIGHT;
        float minDst = worldWidth + worldHeight;
        for (int j = -1; j <= 1; j++) {
            for (int i = -1; i <= 1; i++) {
                float tmpX = position.x + worldWidth * i;
                float tmpY = position.y + worldHeight * j;
                float dst = playerPosition.dst(tmpX, tmpY);
                if (dst < minDst) {
                    minDst = dst;
                    tmpCoords[0] = tmpX;
                    tmpCoords[1] = tmpY;
                }
            }
        }
        if (renderableUpdate) renderableUpdate(playerPosition, renderOffsetX, renderOffsetY, halfWidth, halfHeight);
    }

    private void renderableUpdate(Vector2 playerPosition, float offsetX, float offsetY, float halfWidth, float halfHeight) {
        isRenderable = Math.abs(playerPosition.x - (tmpCoords[0] + offsetX)) <= SCREEN_HALF_WIDTH + halfWidth &&
                Math.abs(playerPosition.y - (y + offsetY)) <= SCREEN_HALF_HEIGHT + halfHeight;
    }

    public boolean isRenderable() {
        return isRenderable;
    }
}
