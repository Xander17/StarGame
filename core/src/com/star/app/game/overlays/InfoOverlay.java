package com.star.app.game.overlays;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.StringBuilder;
import com.star.app.game.GameController;
import com.star.app.game.pilots.PlayerStatistic;
import com.star.app.utils.Assets;

import static com.star.app.screen.ScreenManager.SCREEN_HEIGHT;
import static com.star.app.screen.ScreenManager.SCREEN_WIDTH;

public class InfoOverlay {
    private float MAX_SEC_TO_GAIN_AMOUNT = 1f;
    private int SCORE_GAIN_PER_SEC = 1000;
    private int DURABILITY_GAIN_PER_SEC = 10;
    private int CASH_GAIN_PER_SEC = 100;
    private int AMMO_GAIN_PER_SEC = 100;

    private GameController gameController;
    private BitmapFont font22;
    private BitmapFont font64;
    private StringBuilder stringBuilder;
    private TextureRegion durability;
    private TextureRegion cash;
    private TextureRegion ammo;
    private GlyphLayout layout;

    private float scoreView;
    private float cashView;
    private float durabilityView;
    private float ammoView;

    public InfoOverlay(GameController gameController) {
        this.gameController = gameController;
        this.font22 = Assets.getInstance().getAssetManager().get("fonts/font22.ttf", BitmapFont.class);
        this.font64 = Assets.getInstance().getAssetManager().get("fonts/font64.ttf", BitmapFont.class);
        this.stringBuilder = new StringBuilder();
        this.durability = Assets.getInstance().getTextureAtlas().findRegion("durabilityicon");
        this.cash = Assets.getInstance().getTextureAtlas().findRegion("cashicon");
        this.ammo = Assets.getInstance().getTextureAtlas().findRegion("ammoicon");
        this.layout = new GlyphLayout();
        this.scoreView = 0f;
        this.cashView = 0f;
        this.durabilityView = gameController.getPlayer().getShip().getDurability();
        this.ammoView = gameController.getPlayer().getShip().getWeapon().getBullets();
    }

    public void update(float dt) {
        scoreView = getViewAmount(gameController.getPlayer().getPlayerStatistic().get(PlayerStatistic.Stats.SCORE), scoreView, SCORE_GAIN_PER_SEC, dt);
        cashView = getViewAmount(gameController.getPlayer().getCash(), cashView, CASH_GAIN_PER_SEC, dt);
        durabilityView = getViewAmount(gameController.getPlayer().getShip().getDurability(), durabilityView, DURABILITY_GAIN_PER_SEC, dt);
        ammoView = getViewAmount(gameController.getPlayer().getShip().getWeapon().getBullets(), ammoView, AMMO_GAIN_PER_SEC, dt);
    }

    private float getViewAmount(float realAmount, float viewAmount, float perSecGain, float dt) {
        float newViewAmount;
        float sign = Math.signum(realAmount - viewAmount);
        if (Math.abs(realAmount - viewAmount) > perSecGain * MAX_SEC_TO_GAIN_AMOUNT)
            newViewAmount = viewAmount + (realAmount - viewAmount) / MAX_SEC_TO_GAIN_AMOUNT * dt;
        else newViewAmount = viewAmount + Math.signum(realAmount - viewAmount) * dt * perSecGain;
        if (sign * newViewAmount > sign * realAmount) newViewAmount = realAmount;
        return newViewAmount;
    }

    public void render(SpriteBatch batch) {
        drawIconAmount(batch, durability, font22, 20, SCREEN_HEIGHT - 20, Math.round(durabilityView), 3);
        drawIconMaxAmount(batch, ammo, font22, 130, SCREEN_HEIGHT - 20, Math.round(ammoView), gameController.getPlayer().getShip().getWeapon().getMaxBullets(), 4);
        drawIconAmount(batch, cash, font22, 20, SCREEN_HEIGHT - 60, Math.round(cashView), 0);
        drawCaptionAmount(batch, font22, 350, SCREEN_HEIGHT - 20, "SCORE: ", Math.round(scoreView));
        drawCaptionAmount(batch, font22, SCREEN_WIDTH - 130, SCREEN_HEIGHT - 20, "LIVES: ", gameController.getPlayer().getLives());

        if (gameController.isWin())
            drawCenterAlign(batch, font64, "YOU WIN", 0, SCREEN_WIDTH, SCREEN_HEIGHT / 2);
        else if (gameController.isGameOver())
            drawCenterAlign(batch, font64, "GAME OVER", 0, SCREEN_WIDTH, SCREEN_HEIGHT / 2);
        else if (gameController.getPlayer().isDead())
            drawCenterAlign(batch, font64, "YOU ARE DEAD", 0, SCREEN_WIDTH, SCREEN_HEIGHT / 2);
    }

    private void drawIconAmount(SpriteBatch batch, TextureRegion texture, BitmapFont font, float x, float y, int amount, int zeros) {
        batch.draw(texture, x, y - texture.getRegionHeight() + 4);
        String s;
        if (zeros == 0) s = String.valueOf(amount);
        else s = String.format("%0" + zeros + "d", amount);
        font.draw(batch, s, x + texture.getRegionWidth() + 5, y);
    }

    private void drawIconMaxAmount(SpriteBatch batch, TextureRegion texture, BitmapFont font, float x, float y, int amount, int maxAmount, int zeros) {
        batch.draw(texture, x, y - texture.getRegionHeight() + 4);
        String s;
        if (zeros == 0) s = String.valueOf(amount);
        else s = String.format("%0" + zeros + "d/%0" + zeros + "d", amount, maxAmount);
        font.draw(batch, s, x + texture.getRegionWidth() + 5, y);
    }

    private void drawCaptionAmount(SpriteBatch batch, BitmapFont font, float x, float y, String caption, int amount) {
        stringBuilder.clear();
        stringBuilder.append(caption).append(amount);
        font.draw(batch, stringBuilder.toString(), x, y);
    }

    private void drawCenterAlign(SpriteBatch batch, BitmapFont font, String text, float regionStartX, float regionWidth, float y) {
        font.draw(batch, text, regionStartX, y + font.getXHeight() / 2, regionWidth, Align.center, false);
    }
}
