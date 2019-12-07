package com.star.app.game.asteroids;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.star.app.game.GameController;
import com.star.app.game.helpers.Collisional;
import com.star.app.game.helpers.Poolable;
import com.star.app.game.overlays.DebugOverlay;
import com.star.app.game.pilots.PlayerStatistic;

import static com.star.app.screen.ScreenManager.*;

public class Asteroid implements Poolable, Collisional {
    private final float HEALTH_POINTS_MIN = 8;
    private final float HEALTH_POINTS_MAX = 10;
    private final float HEALTH_LEVEL_FACTOR = 0.1f;
    private final float MASS_LEVEL_FACTOR = 0.02f;
    private final float BASE_SPEED_MIN = 30f;
    private final float BASE_SPEED_MAX = 100f;
    private final float SPEED_LEVEL_FACTOR = 0.05f;
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
    private int[] visibleIndex;
    private boolean trackable;
    private Vector2 arrowPosition;
    private float arrowAngle;
    private float arrowScale;

    Asteroid(GameController gameController) {
        this.gameController = gameController;
        position = new Vector2(0, 0);
        velocity = new Vector2(0, 0);
        arrowPosition = new Vector2(0, 0);
        hitBox = new Circle();
        isActive = false;
    }

    void activate(TextureRegion texture) {
        float speed = getRandomOnLevel(BASE_SPEED_MIN, BASE_SPEED_MAX, SPEED_LEVEL_FACTOR);
        float angle = getOutboundsRandomAngle();
        float scale = MathUtils.random(BASE_SCALE_MIN, BASE_SCALE_MAX);
        float health = getRandomOnLevel(HEALTH_POINTS_MIN, HEALTH_POINTS_MAX, HEALTH_LEVEL_FACTOR);
        this.textureW = texture.getRegionWidth();
        this.textureH = texture.getRegionHeight();
        activate(texture, getRandomStartPoint(scale), scale,
                MathUtils.randomSign() * MathUtils.cosDeg(angle) * speed,
                MathUtils.randomSign() * MathUtils.sinDeg(angle) * speed, health);
    }

    void activate(TextureRegion texture, float x, float y, float scale, float health) {
        float speed = getRandomOnLevel(BASE_SPEED_MIN, BASE_SPEED_MAX, SPEED_LEVEL_FACTOR);
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
        this.trackable = false;
    }

    private void deactivate() {
        isActive = false;
    }

    private float getRandomOnLevel(float min, float max, float factor) {
        int level = gameController.getLevel();
        return MathUtils.random((1 + factor * level) * min, (1 + factor * level) * max);
    }

    private float getOutboundsRandomAngle() {
        float angle = 0;
        while (angle % 90 < ANGLE_NO_CREATE || angle % 90 > (90 - ANGLE_NO_CREATE)) {
            angle = MathUtils.random(0, 360);
        }
        return angle;
    }

    private Vector2 getRandomStartPoint(float scale) {
        Vector2 playerPosition = gameController.getPlayer().getShip().getPosition();
        float x = playerPosition.x;
        float y = playerPosition.y;
        while (Math.abs(x - position.x) <= SCREEN_HALF_WIDTH + textureW / 2f * scale ||
                Math.abs(y - position.y) <= SCREEN_HALF_HEIGHT + textureH / 2f * scale) {
            x = MathUtils.random(0, gameController.SPACE_WIDTH);
            y = MathUtils.random(0, gameController.SPACE_HEIGHT);
        }
        return new Vector2(x, y);
    }

    void update(float dt) {
        position.x += velocity.x * dt;
        gameController.seamlessTranslate(position);
        position.y += velocity.y * dt;
        rotationAngle += rotationSpeed * dt;
        visibleIndex = gameController.getSeamlessVisibleIndex(position, textureW / 2f * scale, textureH / 2f * scale);
        checkTrack();
    }

    private void checkTrack() {
        Vector2 playerPosition = gameController.getPlayer().getShip().getPosition();
        int[] index = gameController.getSeamlessNearestIndex(position);
        float calcX = position.x + gameController.SPACE_WIDTH * index[0];
        float calcY = position.y + gameController.SPACE_HEIGHT * index[1];
        float dst = Vector2.dst(playerPosition.x, playerPosition.y, calcX, calcY);
        trackable = (dst <= gameController.getPlayer().getShip().getSCAN_DISTANCE()) &&
                (Math.abs(playerPosition.x - calcX) > SCREEN_HALF_WIDTH + textureW / 2f) ||
                (Math.abs(playerPosition.y - calcY) > SCREEN_HALF_HEIGHT + textureH / 2f);
        if (trackable) {
            arrowAngle = (float) Math.toDegrees(Math.atan2(calcY - playerPosition.y, calcX - playerPosition.x));
            if (arrowAngle < 0) arrowAngle += 360;
            arrowPosition.set(playerPosition.x + 400 * MathUtils.cosDeg(arrowAngle), playerPosition.y + 400 * MathUtils.sinDeg(arrowAngle));
            arrowScale = 1.2f - 0.9f * dst / gameController.getPlayer().getShip().getSCAN_DISTANCE();
        }
    }

    public void render(SpriteBatch batch) {
        if (visibleIndex == null) return;
        batch.draw(texture, position.x - textureW / 2f + gameController.SPACE_WIDTH * visibleIndex[0],
                position.y - textureH / 2f + gameController.SPACE_HEIGHT * visibleIndex[1],
                textureW / 2f, textureH / 2f, textureW, textureH, scale, scale, rotationAngle);
    }

    public void renderArrow(SpriteBatch batch, TextureRegion texture) {
        if (trackable) {
            int textureW = texture.getRegionWidth();
            int textureH = texture.getRegionHeight();
            batch.draw(texture, arrowPosition.x - textureW, arrowPosition.y - textureH / 2f,
                    textureW, textureH / 2f, textureW, textureH, arrowScale, arrowScale, arrowAngle);
        }
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
        float downscale = MathUtils.random(0.9f, 1.1f) * DESTROY_DOWNSCALE;
        float newScale = scale - downscale;
        if (newScale * textureW < SIZE_BOTTOM_LIMIT) return;
        for (int i = 1; i < MathUtils.random(PARTS_COUNT_MIN, PARTS_COUNT_MAX); i++)
            gameController.getAsteroidController().createNew(position.x, position.y, newScale, (int) (maxHealth * (1 - downscale)));
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
        return (float) Math.ceil(scale * 10 / 3 * (1 + MASS_LEVEL_FACTOR * gameController.getLevel()));
    }

}