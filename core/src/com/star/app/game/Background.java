package com.star.app.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import static com.star.game.ScreenManager.SCREEN_HEIGHT;
import static com.star.game.ScreenManager.SCREEN_WIDTH;

public class Background {
    private final int STARS_COUNT = 600;
    private final float PARALLAX_PERCENT = 0.1f;

    private GameController gameController;
    private Texture textureCosmos;
    private Star[] stars;
    private float srcX, srcY;
    private int srcW, srcH;
    private float parallaxStepX, parallaxStepY;

    public Background(GameController gameController) {
        this.gameController = gameController;
        this.textureCosmos = new Texture("background.jpg");
        parallaxSettings();
        this.stars = new Star[STARS_COUNT];
        for (int i = 0; i < stars.length; i++) {
            stars[i] = new Star();
        }
    }

    private void parallaxSettings() {
        srcX = PARALLAX_PERCENT * textureCosmos.getWidth();
        srcY = PARALLAX_PERCENT * textureCosmos.getHeight();
        srcW = (int) (textureCosmos.getWidth() - 2 * srcX);
        srcH = (int) (textureCosmos.getHeight() - 2 * srcY);
        parallaxStepX = srcX / (SCREEN_WIDTH / 2f);
        parallaxStepY = srcY / (SCREEN_HEIGHT / 2f);
    }

    public void render(SpriteBatch batch) {
        batch.draw(textureCosmos, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT,
                (int) srcX, (int) srcY, srcW, srcH, false, false);
        for (int i = 0; i < stars.length; i++) stars[i].render(batch);
    }

    public void update(float dt) {
        srcX += gameController.getHero().getVelocity().x * dt * parallaxStepX;
        srcY -= gameController.getHero().getVelocity().y * dt * parallaxStepY;
        for (int i = 0; i < stars.length; i++) stars[i].update(dt);
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

        private Texture[] starTypes = {
                new Texture("stars/star_white.png"),
                new Texture("stars/star_blue.png"),
                new Texture("stars/star_green.png"),
                new Texture("stars/star_purple.png"),
                new Texture("stars/star_red.png"),
                new Texture("stars/star_yellow.png")
        };

        private Vector2 position;
        private Vector2 velocity;
        private float scale;
        private Texture texture;
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
            shining = false;
            shiningScale = 1;
        }

        void update(float dt) {
            position.x += (velocity.x - gameController.getHero().getVelocity().x * DISPLACEMENT_FACTOR) * dt;
            position.y += (velocity.y - gameController.getHero().getVelocity().y * DISPLACEMENT_FACTOR) * dt;
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
            int textureW = texture.getWidth();
            int textureH = texture.getHeight();
            batch.draw(texture, position.x - textureW / 2f, position.y - textureH / 2f,
                    textureW / 2, textureH / 2, textureW, textureH, scale * shiningScale, scale * shiningScale, 0,
                    0, 0, textureW, textureH, false, false);
        }
    }
}