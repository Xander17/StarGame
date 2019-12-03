package com.star.app.game.asteroids;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.star.app.game.GameController;
import com.star.app.game.helpers.Collisional;
import com.star.app.game.helpers.Poolable;
import com.star.app.game.pilots.PlayerStatistic;

import static com.star.app.screen.ScreenManager.SCREEN_HEIGHT;
import static com.star.app.screen.ScreenManager.SCREEN_WIDTH;

public class Asteroid implements Poolable, Collisional {
    private final int HEALTH_POINTS_MIN = 8;
    private final int HEALTH_POINTS_MAX = 10;
    private final float BASE_SPEED_MIN = 100f;
    private final float BASE_SPEED_MAX = 30f;
    private final float BASE_SCALE_MIN = 0.8f;
    private final float BASE_SCALE_MAX = 1.0f;
    private final float ROTATION_BASE_SPEED_MIN = 60f;
    private final float ROTATION_BASE_SPEED_MAX = 10f;
    private final float ANGLE_NO_CREATE = 15f;
    private final float SIZE_BOTTOM_LIMIT = 60f;
    private final float DESTROY_DOWNSCALE = 0.15f;
    private final int PARTS_COUNT_MIN = 3;
    private final int PARTS_COUNT_MAX = 5;
    private final float DROP_CHANCE_MAX = 0.1f;
    private final float DROP_CHANCE_MIN = 0.01f;

    private GameController gameController;
    private TextureRegion texture;
    private Vector2 position;
    private Vector2 velocity;
    private float maxHealth;
    private float health;
    private float rotationAngle;
    private float rotationSpeed;
    private float scale;
    private int textureW;
    private int textureH;
    private boolean isActive;
    private Circle hitBox;

    Asteroid(GameController gameController) {
        this.gameController = gameController;
        position = new Vector2(0, 0);
        velocity = new Vector2(0, 0);
        hitBox = new Circle();
        isActive = false;
    }

    void activate(TextureRegion texture) {
        float speed = MathUtils.random(BASE_SPEED_MIN, BASE_SPEED_MAX);
        float angle = 0;
        while (angle % 90 < ANGLE_NO_CREATE || angle % 90 > (90 - ANGLE_NO_CREATE)) {
            angle = MathUtils.random(0, 360);
        }
        float scale = MathUtils.random(BASE_SCALE_MIN, BASE_SCALE_MAX);
        float health = MathUtils.random(HEALTH_POINTS_MIN, HEALTH_POINTS_MAX);
        activate(texture, getRandomStartPoint(scale), scale,
                MathUtils.randomSign() * MathUtils.cosDeg(angle) * speed,
                MathUtils.randomSign() * MathUtils.sinDeg(angle) * speed, health);
    }

    void activate(TextureRegion texture, float x, float y, float scale, float health) {
        float speed = MathUtils.random(BASE_SPEED_MIN, BASE_SPEED_MAX);
        float angle = MathUtils.random(360);
        activate(texture, x, y, scale, MathUtils.randomSign() * MathUtils.cosDeg(angle) * speed,
                MathUtils.randomSign() * MathUtils.sinDeg(angle) * speed, health);
    }

    private void activate(TextureRegion texture, Vector2 position, float scale, float velocityX, float velocityY, float health) {
        activate(texture, position.x, position.y, scale, velocityX, velocityY, health);
    }

    void activate(TextureRegion texture, float x, float y, float scale, float velocityX, float velocityY, float health) {
        this.texture = texture;
        this.textureW = texture.getRegionWidth();
        this.textureH = texture.getRegionHeight();
        this.position.set(x, y);
        this.scale = scale;
        this.velocity.set(velocityX, velocityY);
        this.rotationAngle = MathUtils.random(0, 360);
        this.rotationSpeed = MathUtils.randomSign() * MathUtils.random(ROTATION_BASE_SPEED_MIN, ROTATION_BASE_SPEED_MAX);
        this.maxHealth = health;
        this.health = health;
        this.isActive = true;
    }

    private void deactivate() {
        isActive = false;
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, position.x - textureW / 2f, position.y - textureH / 2f,
                textureW / 2f, textureH / 2f, textureW, textureH, scale, scale, rotationAngle);
    }

    void update(float dt) {
        position.x += velocity.x * dt;
        position.y += velocity.y * dt;
        if (position.x < -textureW / 2f * scale) position.x = SCREEN_WIDTH + textureW / 2f * scale;
        else if (position.x > SCREEN_WIDTH + textureW / 2f * scale) position.x = -textureW / 2f * scale;
        if (position.y < -textureH / 2f * scale) position.y = SCREEN_HEIGHT + textureH / 2f * scale;
        else if (position.y > SCREEN_HEIGHT + textureH / 2f * scale) position.y = -textureH / 2f * scale;

        rotationAngle += rotationSpeed * dt;
    }

    private Vector2 getRandomStartPoint(float scale) {
        if (MathUtils.randomBoolean()) return new Vector2(MathUtils.random(0, SCREEN_WIDTH), -textureH / 2f * scale);
        else return new Vector2(-textureW / 2f * scale, MathUtils.random(0, SCREEN_HEIGHT));
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    @Override
    public boolean takeDamage(float amount) {
        health -= amount;
        gameController.getPlayer().getPlayerStatistic().add(PlayerStatistic.Stats.DAMAGE_OVERALL, amount);
        if (health <= 0) {
            destroy();
            return true;
        }
        return false;
    }

    @Override
    public void destroy() {
        gameController.getPlayer().getPlayerStatistic().inc(PlayerStatistic.Stats.ASTEROIDS);
        deactivate();
        getAsteroidPieces();
        dropItem();
    }

    private void getAsteroidPieces() {
        float newScale = scale - MathUtils.random(0.9f, 1.1f) * DESTROY_DOWNSCALE;
        if (newScale * textureW < SIZE_BOTTOM_LIMIT) return;
        for (int i = 1; i < MathUtils.random(PARTS_COUNT_MIN, PARTS_COUNT_MAX); i++)
            gameController.getAsteroidController().createNew(position.x, position.y, newScale, (int) (maxHealth * (1 - DESTROY_DOWNSCALE)));
    }

    private void dropItem() {
        gameController.getDropController().getRandom(position, getDropChance());
    }

    private float getDropChance() {
        return DROP_CHANCE_MIN + (DROP_CHANCE_MAX - DROP_CHANCE_MIN) * (scale - SIZE_BOTTOM_LIMIT) / (BASE_SCALE_MAX - SIZE_BOTTOM_LIMIT);
    }

    @Override
    public Circle getHitBox() {
        hitBox.set(position.x, position.y, textureW / 2f * scale * 0.9f);
        return hitBox;
    }

    public float getMaxHealth() {
        return maxHealth;
    }

    public Vector2 getPosition() {
        return position;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    @Override
    public float getMassFactor() {
        return (float) Math.ceil(scale * 10 / 3);
    }
}