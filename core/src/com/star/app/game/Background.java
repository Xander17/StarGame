package com.star.app.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.star.app.utils.Assets;

import static com.star.app.screen.ScreenManager.*;

public class Background {
    private final int STARS_COUNT = 600;

    private GameController gameController;
    private Texture texture;
    private float textureW, textureH;
    private Star[] stars;
    private float cursorX, cursorY;
    private float dx, dy;

    public Background(GameController gameController) {
        this.gameController = gameController;
        init();
    }

    public Background() {
        this(null);
        cursorX = SCREEN_HALF_WIDTH;
        cursorY = SCREEN_HALF_HEIGHT;
    }

    private void init() {
        this.texture = new Texture("images/background_sl2.jpg");
        textureW = texture.getWidth();
        textureH = texture.getHeight();
        this.stars = new Star[STARS_COUNT];
        for (int i = 0; i < stars.length; i++) {
            stars[i] = new Star();
        }
    }

    public void update(float dt) {
        if (gameController == null) updateDelta();
        for (int i = 0; i < stars.length; i++) stars[i].update(dt);
    }

    private void updateDelta() {
        dx = Gdx.input.getX() - cursorX;
        dy = Gdx.input.getY() - cursorY;
        cursorX = Gdx.input.getX();
        cursorY = Gdx.input.getY();
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, SCREEN_HALF_WIDTH - textureW / 2f, SCREEN_HALF_HEIGHT - textureH / 2f);
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
        private final float DISPLACEMENT_FACTOR = 0.1f;

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
            float offsetX, offsetY;
            if (gameController != null) {
                Vector2 v = gameController.getPlayer().getShip().getVelocity();
                offsetX = v.x * DISPLACEMENT_FACTOR;
                offsetY = v.y * DISPLACEMENT_FACTOR;
            } else {
                offsetX = dx;
                offsetY = -dy;
            }
            position.x += (velocity.x - offsetX) * dt;
            position.y += (velocity.y - offsetY) * dt;
            checkBorders();
            shiningUpdate(dt);
        }

        private void checkBorders() {
            if (position.x < -SCREEN_PADDING) {
                position.x += SCREEN_WIDTH + 2 * SCREEN_PADDING;
//                position.x = SCREEN_WIDTH + SCREEN_PADDING;
//                position.y = MathUtils.random(-SCREEN_PADDING, SCREEN_HEIGHT + SCREEN_PADDING);
//                newStarInit();
            } else if (position.x > SCREEN_WIDTH + SCREEN_PADDING) {
                position.x -= SCREEN_WIDTH + 2 * SCREEN_PADDING;
            }
            if (position.y < -SCREEN_PADDING) position.y += SCREEN_HEIGHT + 2 * SCREEN_PADDING;
            else if (position.y > SCREEN_HEIGHT + SCREEN_PADDING) position.y -= SCREEN_HEIGHT + 2 * SCREEN_PADDING;

        }

        private void shiningUpdate(float dt) {
            if (shiningScale == 1 && Math.random() < SHINING_FREQ) shining = true;
            if (shining) shiningScale += (MAX_SHINING_SCALE - 1) * dt / SHINING_TIME;
            else shiningScale -= (MAX_SHINING_SCALE - 1) * dt / SHINING_TIME;
            if (shiningScale > MAX_SHINING_SCALE) {
                shiningScale = MAX_SHINING_SCALE;
                shining = false;
            } else if (shiningScale < 1) shiningScale = 1;
        }

        void render(SpriteBatch batch) {
            batch.draw(texture, position.x - textureW / 2f, position.y - textureH / 2f,
                    textureW / 2, textureH / 2, textureW, textureH, scale * shiningScale, scale * shiningScale, 0);
        }
    }
}