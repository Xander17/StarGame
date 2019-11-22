package com.star.app.game;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.StringBuilder;
import com.star.app.utils.Assets;

import static com.star.app.screen.ScreenManager.SCREEN_HEIGHT;
import static com.star.app.screen.ScreenManager.SCREEN_WIDTH;

public class InfoOverlay {
    GameController gameController;
    BitmapFont font;
    StringBuilder stringBuilder;

    public InfoOverlay(GameController gameController) {
        this.gameController = gameController;
        font = Assets.getInstance().getAssetManager().get("fonts/font22.ttf", BitmapFont.class);
        stringBuilder = new StringBuilder();
    }

    public void render(SpriteBatch batch) {
        stringBuilder.clear();
        stringBuilder.append("SCORE: ").append(gameController.getPlayer().getScoreView());
        font.draw(batch, stringBuilder.toString(), 20, SCREEN_HEIGHT - 20);
        stringBuilder.clear();
        stringBuilder.append("LIVES: ").append(gameController.getPlayer().getLives());
        font.draw(batch, stringBuilder.toString(), SCREEN_WIDTH-130, SCREEN_HEIGHT - 20);
    }
}
