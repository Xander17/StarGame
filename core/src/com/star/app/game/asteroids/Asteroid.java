package com.star.app.game.asteroids;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.star.app.game.GameController;
import com.star.app.game.helpers.Collisional;
import com.star.app.game.helpers.GameTimer;
import com.star.app.game.helpers.Poolable;
import com.star.app.game.helpers.RenderPosition;
import com.star.app.game.particles.ParticleLayouts;
import com.star.app.game.pilots.PlayerStatistic;

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
    private final float SIZE_BOTTOM_LIMIT = 60f;
    private final float DESTROY_DOWNSCALE = 0.15f;
    private final int PARTS_COUNT_MIN = 3;
    private final int PARTS_COUNT_MAX = 5;
    private final float DROP_CHANCE_MAX = 0.1f;
    private final float DROP_CHANCE_MIN = 0.01f;
    private final float BLAST_TIME = 0.5f;

    private GameController gameController;
    private TextureRegion texture;
    private Vector2 position;
    private RenderPosition renderPosition;
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
    private boolean trackable;
    private Vector2 arrowPosition;
    private float arrowAngle;
    private float arrowScale;
    private GameTimer activateTimer;

    Asteroid(GameController gameController) {
        this.gameController = gameController;
        position = new Vector2(0, 0);
        renderPosition = new RenderPosition(position);
        velocity = new Vector2(0, 0);
        arrowPosition = new Vector2(0, 0);
        hitBox = new Circle();
        activateTimer = new GameTimer(BLAST_TIME / 2);
        isActive = false;
    }

    void activate(TextureRegion texture, boolean delayed) {
        float speed = getRandomOnLevel(BASE_SPEED_MIN, BASE_SPEED_MAX, SPEED_LEVEL_FACTOR);
        float angle = MathUtils.random(0, 359);
        float scale = MathUtils.random(BASE_SCALE_MIN, BASE_SCALE_MAX);
        float maxHealth = getRandomOnLevel(HEALTH_POINTS_MIN, HEALTH_POINTS_MAX, HEALTH_LEVEL_FACTOR);
        this.textureW = texture.getRegionWidth();
        this.textureH = texture.getRegionHeight();
        activate(texture, gameController.getRandomStartPoint(textureW / 2f * scale, textureH / 2f * scale), scale,
                MathUtils.randomSign() * MathUtils.cosDeg(angle) * speed,
                MathUtils.randomSign() * MathUtils.sinDeg(angle) * speed, maxHealth, delayed);
    }

    void activate(TextureRegion texture, float x, float y, float scale, float maxHealth, boolean delayed) {
        float speed = getRandomOnLevel(BASE_SPEED_MIN, BASE_SPEED_MAX, SPEED_LEVEL_FACTOR);
        float angle = MathUtils.random(360);
        activate(texture, x, y, scale, MathUtils.randomSign() * MathUtils.cosDeg(angle) * speed,
                MathUtils.randomSign() * MathUtils.sinDeg(angle) * speed, maxHealth, delayed);
    }

    private void activate(TextureRegion texture, Vector2 position, float scale, float velocityX, float velocityY, float maxHealth, boolean delayed) {
        activate(texture, position.x, position.y, scale, velocityX, velocityY, maxHealth, delayed);
    }

    void activate(TextureRegion texture, float x, float y, float scale, float velocityX, float velocityY, float maxHealth, boolean delayed) {
        this.texture = texture;
        textureW = texture.getRegionWidth();
        textureH = texture.getRegionHeight();
        position.set(x, y);
        renderPosition.recalculate(gameController, textureW / 2f, textureH / 2f);
        this.scale = scale;
        velocity.set(velocityX, velocityY);
        this.rotationAngle = MathUtils.random(0, 360);
        this.rotationSpeed = MathUtils.randomSign() * MathUtils.random(ROTATION_BASE_SPEED_MIN, ROTATION_BASE_SPEED_MAX);
        this.maxHealth = maxHealth;
        health = maxHealth;
        isActive = true;
        trackable = false;
        if (delayed) {
            activateTimer.reset();
        } else {
            activateTimer.disable();
        }
    }

    private void deactivate() {
        isActive = false;
    }

    private float getRandomOnLevel(float min, float max, float factor) {
        int level = gameController.getLevel();
        return MathUtils.random((1 + factor * level) * min, (1 + factor * level) * max);
    }

    void update(float dt) {
        activateTimer.update(dt);
        if (!activateTimer.isReady()) return;
        position.mulAdd(velocity, dt);
        gameController.seamlessTranslate(position);
        rotationAngle += rotationSpeed * dt;
        renderPosition.recalculate(gameController, textureW / 2f, textureH / 2f);
        checkTrack();
    }

    // TODO: 08.12.2019 перенести в отдельный трекер
    private void checkTrack() {
        Vector2 playerPosition = gameController.getPlayer().getShip().getPosition();
        float dst = playerPosition.dst(renderPosition);
        trackable = (dst <= gameController.getPlayer().getShip().getSCAN_DISTANCE()) && !renderPosition.isRenderable();
        if (trackable) {
            arrowAngle = (float) Math.toDegrees(Math.atan2(renderPosition.y - playerPosition.y, renderPosition.x - playerPosition.x));
            if (arrowAngle < 0) arrowAngle += 360;
            arrowPosition.set(playerPosition.x + 400 * MathUtils.cosDeg(arrowAngle), playerPosition.y + 400 * MathUtils.sinDeg(arrowAngle));
            arrowScale = 1.2f - 0.9f * dst / gameController.getPlayer().getShip().getSCAN_DISTANCE();
        }
    }

    public void render(SpriteBatch batch) {
        if (!activateTimer.isReady()) return;
        if (!renderPosition.isRenderable()) return;
        batch.draw(texture, renderPosition.x - textureW / 2f, renderPosition.y - textureH / 2f,
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
    public boolean takeImpulseDamage(float power, float angle, float amount) {
        if (takeDamage(amount)) return true;
        velocity.x += power * MathUtils.cosDeg(angle) / getMassFactor();
        velocity.y += power * MathUtils.sinDeg(angle) / getMassFactor();
        return false;
    }

    @Override
    public void destroy() {
        gameController.getPlayer().getPlayerStatistic().inc(PlayerStatistic.Stats.ASTEROIDS);
        gameController.getParticleController().getEffectBuilder().bigBlast(ParticleLayouts.TOP, position, textureW / 2f * scale, 0.5f, 0.5f, 0.5f, 0.2f, 0.2f, 0.2f);
        deactivate();
        getAsteroidPieces();
        dropItem();
    }

    private void getAsteroidPieces() {
        float downscale = MathUtils.random(0.9f, 1.1f) * DESTROY_DOWNSCALE;
        float newScale = scale - downscale;
        if (newScale * textureW < SIZE_BOTTOM_LIMIT) return;
        int maxParts = MathUtils.random(PARTS_COUNT_MIN, PARTS_COUNT_MAX);
        for (int i = 0; i < maxParts; i++)
            gameController.getAsteroidController().createNew(position.x, position.y, newScale, (int) (maxHealth * (1 - downscale)), true);
    }

    private void dropItem() {
        gameController.getDropController().getRandom(position, getDropChance());
    }

    private float getDropChance() {
        return DROP_CHANCE_MIN + (DROP_CHANCE_MAX - DROP_CHANCE_MIN) * (scale - SIZE_BOTTOM_LIMIT) / (BASE_SCALE_MAX - SIZE_BOTTOM_LIMIT);
    }

    @Override
    public Circle getHitBox() {
        hitBox.set(renderPosition, textureW / 2f * scale * 0.9f);
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