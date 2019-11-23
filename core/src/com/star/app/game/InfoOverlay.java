package com.star.app.game;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.StringBuilder;
import com.star.app.utils.Assets;

import static com.star.app.screen.ScreenManager.SCREEN_HEIGHT;
import static com.star.app.screen.ScreenManager.SCREEN_WIDTH;

public class InfoOverlay {
    GameController gameController;
    BitmapFont font;
    StringBuilder stringBuilder;
    TextureRegion durability;

    public InfoOverlay(GameController gameController) {
        this.gameController = gameController;
        font = Assets.getInstance().getAssetManager().get("fonts/font22.ttf", BitmapFont.class);
        stringBuilder = new StringBuilder();
        durability = Assets.getInstance().getTextureAtlas().findRegion("durability");
    }

    public void render(SpriteBatch batch) {
        batch.draw(durability, 20, SCREEN_HEIGHT - 43);
        font.draw(batch, String.valueOf(MathUtils.round(gameController.getPlayer().getShip().getDurability())), 50, SCREEN_HEIGHT - 20);
        stringBuilder.clear();
        stringBuilder.append("SCORE: ").append(gameController.getPlayer().getScoreView());
        font.draw(batch, stringBuilder.toString(), 150, SCREEN_HEIGHT - 20);
        stringBuilder.clear();
        stringBuilder.append("LIVES: ").append(gameController.getPlayer().getLives());
        font.draw(batch, stringBuilder.toString(), SCREEN_WIDTH - 130, SCREEN_HEIGHT - 20);
    }
}
