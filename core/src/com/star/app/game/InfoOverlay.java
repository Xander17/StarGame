package com.star.app.game;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.StringBuilder;
import com.star.app.utils.Assets;

import static com.star.app.screen.ScreenManager.SCREEN_HEIGHT;
import static com.star.app.screen.ScreenManager.SCREEN_WIDTH;

public class InfoOverlay {
    GameController gameController;
    BitmapFont font22;
    BitmapFont font64;
    StringBuilder stringBuilder;
    TextureRegion durability;
    GlyphLayout layout;

    public InfoOverlay(GameController gameController) {
        this.gameController = gameController;
        this.font22 = Assets.getInstance().getAssetManager().get("fonts/font22.ttf", BitmapFont.class);
        this.font64 = Assets.getInstance().getAssetManager().get("fonts/font64.ttf", BitmapFont.class);
        this.stringBuilder = new StringBuilder();
        this.durability = Assets.getInstance().getTextureAtlas().findRegion("durability");
        this.layout = new GlyphLayout();
    }

    public void render(SpriteBatch batch) {
        batch.draw(durability, 20, SCREEN_HEIGHT - 43);
        font22.draw(batch, String.valueOf(MathUtils.round(gameController.getPlayer().getShip().getDurability())), 50, SCREEN_HEIGHT - 20);
        stringBuilder.clear();
        stringBuilder.append("SCORE: ").append(gameController.getPlayer().getScoreView());
        font22.draw(batch, stringBuilder.toString(), 150, SCREEN_HEIGHT - 20);
        stringBuilder.clear();
        stringBuilder.append("LIVES: ").append(gameController.getPlayer().getLives());
        font22.draw(batch, stringBuilder.toString(), SCREEN_WIDTH - 130, SCREEN_HEIGHT - 20);
        if (gameController.isGameOver())
            drawCenterAlign(batch, font64, "GAME OVER", SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2);
        else if (gameController.getPlayer().isDead())
            drawCenterAlign(batch, font64, "YOU ARE DEAD", SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2);
    }

    private void drawCenterAlign(SpriteBatch batch, BitmapFont font, String text, float x, float y) {
        layout.setText(font, text);
        font.draw(batch, text, x - layout.width / 2, y + layout.height / 2);
    }
}
