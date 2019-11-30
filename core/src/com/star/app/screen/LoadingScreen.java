package com.star.app.screen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import com.star.app.utils.Assets;

import static com.star.app.screen.ScreenManager.SCREEN_HEIGHT;
import static com.star.app.screen.ScreenManager.SCREEN_WIDTH;

public class LoadingScreen extends AbstractScreen {
    private final float LOADING_LINE_WIDTH = SCREEN_WIDTH / 2;
    private final float LOADING_LINE_HEIGHT = 10;

    private Texture texture;
    //private BitmapFont font;

    public LoadingScreen(SpriteBatch batch) {
        super(batch);
        Pixmap pixmap = new Pixmap(1, 20, Pixmap.Format.RGB888);
        pixmap.setColor(Color.rgb888(0, 144, 255));
        pixmap.fill();
        this.texture = new Texture(pixmap);
        //this.font = Assets.getInstance().getFont(64);
        pixmap.dispose();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float dt) {
        if (Assets.getInstance().getAssetManager().update()) {
            Assets.getInstance().makeLinks();
            ScreenManager.getInstance().goToTargetScreen();
        }
        float offset = 0;//font.getXHeight();
        batch.begin();
        //font.draw(batch, "LOADING...", 0, SCREEN_HEIGHT / 2f + offset, SCREEN_WIDTH, Align.center, false);
        batch.draw(texture, (SCREEN_WIDTH - LOADING_LINE_WIDTH) / 2, SCREEN_HEIGHT / 2f - offset,
                Assets.getInstance().getAssetManager().getProgress() * LOADING_LINE_WIDTH, LOADING_LINE_HEIGHT);
        batch.end();
    }

    @Override
    public void dispose() {
        texture.dispose();
    }
}
