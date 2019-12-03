package com.star.app.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.star.app.game.Background;
import com.star.app.game.pilots.PlayerStatistic;
import com.star.app.utils.Assets;

import java.util.Map;
import java.util.TreeMap;

import static com.star.app.screen.ScreenManager.SCREEN_HEIGHT;
import static com.star.app.screen.ScreenManager.SCREEN_WIDTH;

public class GameOverScreen extends AbstractScreen {
    private final float TIME_TO_GAIN_AMOUNT = 3f;

    private Background background;
    private BitmapFont font16, font24, font64;
    private String[] statName;
    private int[] statValues;
    private float[] statViewValues;
    private boolean readyForMenu;

    public GameOverScreen(SpriteBatch batch) {
        super(batch);
    }

    @Override
    public void show() {
        font16 = Assets.getInstance().getAssetManager().get("fonts/font16.ttf", BitmapFont.class);
        font24 = Assets.getInstance().getAssetManager().get("fonts/font24.ttf", BitmapFont.class);
        font64 = Assets.getInstance().getAssetManager().get("fonts/font64.ttf", BitmapFont.class);
        background = new Background(null);
        readyForMenu = false;
    }

    public void uploadStatistic(PlayerStatistic statistic) {
        TreeMap<PlayerStatistic.Stats, Float> map = statistic.getFull();
        statName = new String[map.size()];
        statValues = new int[map.size()];
        statViewValues = new float[map.size()];
        int i = 0;
        for (Map.Entry<PlayerStatistic.Stats, Float> entry : map.entrySet()) {
            statName[i] = entry.getKey().toString();
            statValues[i] = entry.getValue().intValue();
            statViewValues[i] = 0;
            i++;
        }
    }

    private void update(float dt) {
        if (!readyForMenu) readyForMenu = updateViewValues(dt);
        background.update(dt);
        if (readyForMenu && Gdx.input.isKeyPressed(Input.Keys.ANY_KEY))
            ScreenManager.getInstance().changeScreen(ScreenManager.ScreenType.MENU);
    }

    private boolean updateViewValues(float dt) {
        boolean updateFinished = true;
        for (int i = 0; i < statViewValues.length; i++) {
            if (statViewValues[i] < statValues[i]) {
                statViewValues[i] = getViewAmount(statValues[i], statViewValues[i], dt);
                updateFinished = false;
            }
        }
        return updateFinished;
    }

    private float getViewAmount(float realAmount, float viewAmount, float dt) {
        float newViewAmount;
        newViewAmount = viewAmount + realAmount / TIME_TO_GAIN_AMOUNT * dt;
        if (newViewAmount > realAmount) newViewAmount = realAmount;
        return newViewAmount;
    }

    @Override
    public void render(float dt) {
        update(dt);
        batch.begin();
        background.render(batch);
        font64.draw(batch, "GAME OVER", 0, SCREEN_HEIGHT * 0.85f, SCREEN_WIDTH, Align.center, false);
        drawStats();
        if (readyForMenu)
            font16.draw(batch, "Press any key", 0, SCREEN_HEIGHT * 0.15f, SCREEN_WIDTH, Align.center, false);
        batch.end();
    }

    private void drawStats() {
        float offsetY = 1.2f * font24.getLineHeight();
        float topY = (SCREEN_HEIGHT + offsetY * statName.length + font24.getLineHeight()) / 2;
        for (int i = 0; i < statName.length; i++) {
            font24.draw(batch, statName[i], SCREEN_WIDTH * 0.3f, topY - i * offsetY);
            font24.draw(batch, String.valueOf(Math.round(statViewValues[i])),
                    SCREEN_WIDTH * 0.7f, topY - i * offsetY,
                    0, Align.right, false);
        }
    }

    @Override
    public void dispose() {
        background.dispose();
    }
}
