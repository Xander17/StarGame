package com.star.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import static com.star.game.ScreenManager.SCREEN_HEIGHT;
import static com.star.game.ScreenManager.SCREEN_WIDTH;

public class Background {
    private final int STARS_COUNT = 600;
    private final float BG_PARALLAX_PERCENT = 0.1f;

    private StarGame game;
    private Texture textureCosmos;
    private Star[] stars;
    private float bgSrcX, bgSrcY;
    private int bgSrcW, bgSrcH;
    private float bgParallaxStepX, bgParallaxStepY;

    public Background(StarGame game) {
        this.game = game;
        this.textureCosmos = new Texture("background.jpg");
        parallaxSettings();
        this.stars = new Star[STARS_COUNT];
        for (int i = 0; i < stars.length; i++) {
            stars[i] = new Star();
        }
    }

    private void parallaxSettings() {
        bgSrcX = BG_PARALLAX_PERCENT * textureCosmos.getWidth();
        bgSrcY = BG_PARALLAX_PERCENT * textureCosmos.getHeight();
        bgSrcW = (int) (textureCosmos.getWidth() - 2 * bgSrcX);
        bgSrcH = (int) (textureCosmos.getHeight() - 2 * bgSrcY);
        bgParallaxStepX = bgSrcX / (SCREEN_WIDTH / 2);
        bgParallaxStepY = bgSrcY / (SCREEN_HEIGHT / 2);
    }

    public void render(SpriteBatch batch) {
        batch.draw(textureCosmos, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT,
                (int) bgSrcX, (int) bgSrcY, bgSrcW, bgSrcH, false, false);
        for (Star star : stars) star.render(batch);
    }

    public void update(float dt) {
        bgSrcX += game.getHero().getLastDisplacement().x * bgParallaxStepX;
        bgSrcY -= game.getHero().getLastDisplacement().y * bgParallaxStepY;
        for (Star star : stars) star.update(dt);
    }

    private class Star {
        private final float X_SPEED_MIN = -40f;
        private final float X_SPEED_MAX = -5f;
        private final float MIN_SIZE_SCALLING = 0.4f;
        private final float MAX_SIZE_SCALLING = 0.8f;
        private final float SCREEN_PADDING = 200;
        private final float MAX_SHINING_SCALE = 1.5f;
        private final float SHINING_TIME = 0.2f;
        private final double SHINING_FREQ = 0.005;
        private final int DISPLACEMENT_FACTOR = 15;

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
            scale = Math.abs(velocity.x / X_SPEED_MIN) * MathUtils.random(MIN_SIZE_SCALLING, MAX_SIZE_SCALLING);
            texture = starTypes[MathUtils.random(starTypes.length - 1)];
            shining = false;
            shiningScale = 1;
        }

        void update(float dt) {
            position.x += (velocity.x - game.getHero().getLastDisplacement().x * DISPLACEMENT_FACTOR) * dt;
            position.y += (velocity.y - game.getHero().getLastDisplacement().y * DISPLACEMENT_FACTOR) * dt;
            if (position.x < -SCREEN_PADDING) {
                position.x = SCREEN_WIDTH + SCREEN_PADDING;
                position.y = MathUtils.random(-SCREEN_PADDING, SCREEN_HEIGHT + SCREEN_PADDING);
                newStarInit();
            }

            if (shiningScale == 1 && Math.random() < SHINING_FREQ) shining = true;
            if (shining) shiningScale += (MAX_SHINING_SCALE - 1) * dt / SHINING_TIME;
            else shiningScale -= (MAX_SHINING_SCALE - 1) * dt / SHINING_TIME;
            if (shiningScale > MAX_SHINING_SCALE) shining = false;
            else if (shiningScale < 1) shiningScale = 1;

        }

        void render(SpriteBatch batch) {
            int textureW = texture.getWidth();
            int textureH = texture.getHeight();
            batch.draw(texture, position.x - textureW / 2, position.y - textureH / 2,
                    textureW / 2, textureH / 2, textureW, textureH, scale * shiningScale, scale * shiningScale, 0,
                    0, 0, textureW, textureH, false, false);
        }
    }
}