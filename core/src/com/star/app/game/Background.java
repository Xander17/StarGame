package com.star.app.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.star.app.game.overlays.DebugOverlay;
import com.star.app.utils.Assets;

import static com.star.app.screen.ScreenManager.*;

public class Background {
    private final int STARS_COUNT = 600;
    private final float PARALLAX_PERCENT = 0.1f;

    private GameController gameController;
    private Texture texture;
    private Star[] stars;
    private float srcX, srcY;
    private int srcW, srcH;
    private float parallaxStepX, parallaxStepY;

    public Background(GameController gameController) {
        this.gameController = gameController;
        this.texture = new Texture("images/background_sl.jpg");
        parallaxSettings();
        this.stars = new Star[STARS_COUNT];
        for (int i = 0; i < stars.length; i++) {
            stars[i] = new Star();
        }
    }

    private void parallaxSettings() {
        srcX = PARALLAX_PERCENT * texture.getWidth();
        srcY = PARALLAX_PERCENT * texture.getHeight();
        srcW = (int) (texture.getWidth() - 2 * srcX);
        srcH = (int) (texture.getHeight() - 2 * srcY);
        parallaxStepX = PARALLAX_PERCENT * texture.getWidth() / SCREEN_HALF_WIDTH;
        parallaxStepY = PARALLAX_PERCENT * texture.getHeight() / SCREEN_HALF_HEIGHT;
    }

    public void update(float dt) {
        if (gameController != null) {
            srcX = parallaxStepX * gameController.getPlayer().getShip().getPosition().x;
            srcY = parallaxStepY * (SCREEN_HEIGHT - gameController.getPlayer().getShip().getPosition().y);
        } else {
            srcX = parallaxStepX * Gdx.input.getX();
            srcY = parallaxStepY * Gdx.input.getY();
        }
        srcX=0;
        srcY=0;
        for (int i = 0; i < stars.length; i++) stars[i].update(dt);
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT,
                (int) srcX, (int) srcY, srcW, srcH, false, false);
        for (int i = 0; i < stars.length; i++) stars[i].render(batch);
    }

    public void dispose() {
        texture.dispose();
    }

    private class Star {
        private final float X_SPEED_MIN = -5f;
        private final float X_SPEED_MAX = -1f;
        private final float MIN_SIZE_SCALING = 0.4f;
        private final float MAX_SIZE_SCALING = 0.8f;
        private final float SCREEN_PADDING = 200;
        private final float MAX_SHINING_SCALE = 1.5f;
        private final float SHINING_TIME = 0.2f;
        private final double SHINING_FREQ = 0.005;
        private final float DISPLACEMENT_FACTOR = 0.2f;

        private TextureRegion[] starTypes = {
                Assets.getInstance().getTextureAtlas().findRegion("star1"),
                Assets.getInstance().getTextureAtlas().findRegion("star2"),
                Assets.getInstance().getTextureAtlas().findRegion("star3"),
                Assets.getInstance().getTextureAtlas().findRegion("star4"),
                Assets.getInstance().getTextureAtlas().findRegion("star5"),
                Assets.getInstance().getTextureAtlas().findRegion("star6")
        };

        private Vector2 position;
        private Vector2 velocity;
        private float scale;
        private TextureRegion texture;
        private int textureW, textureH;
        private boolean shining;
        private float shiningScale;

        Star() {
            this.position = new Vector2(MathUtils.random(-SCREEN_PADDING, SCREEN_WIDTH + SCREEN_PADDING), MathUtils.random(-SCREEN_PADDING, SCREEN_HEIGHT + SCREEN_PADDING));
            this.velocity = new Vector2(MathUtils.random(X_SPEED_MIN, X_SPEED_MAX), 0);
            newStarInit();
        }

        private void newStarInit() {
            scale = Math.abs(velocity.x / X_SPEED_MIN) * MathUtils.random(MIN_SIZE_SCALING, MAX_SIZE_SCALING);
            texture = starTypes[MathUtils.random(starTypes.length - 1)];
            textureW = texture.getRegionWidth();
            textureH = texture.getRegionHeight();
            shining = false;
            shiningScale = 1;
        }

        void update(float dt) {
            float offsetX = 0, offsetY = 0;
            if (gameController != null) {
                offsetX = gameController.getPlayer().getShip().getVelocity().x * DISPLACEMENT_FACTOR;
                offsetY = gameController.getPlayer().getShip().getVelocity().y * DISPLACEMENT_FACTOR;
            }
            position.x += (velocity.x - offsetX) * dt;
            position.y += (velocity.y - offsetY) * dt;
            if (position.x < -SCREEN_PADDING) {
                position.x = SCREEN_WIDTH + SCREEN_PADDING;
                position.y = MathUtils.random(-SCREEN_PADDING, SCREEN_HEIGHT + SCREEN_PADDING);
                newStarInit();
            }

            if (shiningScale == 1 && Math.random() < SHINING_FREQ) shining = true;
            if (shining) shiningScale += (MAX_SHINING_SCALE - 1) * dt / SHINING_TIME;
            else shiningScale -= (MAX_SHINING_SCALE - 1) * dt / SHINING_TIME;
            if (shiningScale > MAX_SHINING_SCALE) {
                shiningScale = MAX_SHINING_SCALE;
                shining = false;
            } else if (shiningScale < 1) shiningScale = 1;

        }

        void render(SpriteBatch batch) {
            float offsetX = 0, offsetY = 0;
            if (gameController == null) {
                offsetX = (SCREEN_WIDTH - Gdx.input.getX()) * DISPLACEMENT_FACTOR;
                offsetY = (Gdx.input.getY() - SCREEN_HEIGHT) * DISPLACEMENT_FACTOR;
            }
            batch.draw(texture, offsetX + position.x - textureW / 2f, offsetY + position.y - textureH / 2f,
                    textureW / 2, textureH / 2, textureW, textureH, scale * shiningScale, scale * shiningScale, 0);
        }
    }
}