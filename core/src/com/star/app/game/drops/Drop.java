package com.star.app.game.drops;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.star.app.game.GameController;
import com.star.app.game.helpers.GameTimer;
import com.star.app.game.helpers.Poolable;
import com.star.app.game.helpers.RenderPosition;
import com.star.app.game.particles.ParticleLayouts;

public class Drop implements Poolable {
    private final float SHINE_DELAY = 0.1f;
    private final float SHINE_ANGLE_STEP = 20;
    private final float TIME_TO_LIFE = 10f;
    private final float SCALE_MAX = 1f;
    private final float SCALE_MIN = 0.8f;
    private final float SCALE_TIME = 1f;
    private final float RADIUS_ONCOMING_FACTOR = 3f;
    private final float SPEED = 20f;

    private GameController gameController;
    private TextureRegion texture;
    private int textureW, textureH;
    private boolean isActive;
    private DropType type;
    private Vector2 position;
    private RenderPosition renderPosition;
    private Vector2 velocity;
    private DropController controller;
    private Circle hitBox;
    private float[] rgb;
    private float shineCurrentDelay;
    private float shineCurrentAngle;
    private GameTimer lifeTimer;
    private float scale;
    private float scaleSign;

    @Override
    public boolean isActive() {
        return isActive;
    }

    public Drop(GameController gameController, DropController controller) {
        this.gameController = gameController;
        this.controller = controller;
        this.isActive = false;
        this.position = new Vector2(0, 0);
        this.renderPosition = new RenderPosition(position);
        this.velocity = new Vector2(0, 0);
        this.hitBox = new Circle();
        this.shineCurrentDelay = SHINE_DELAY;
        this.shineCurrentAngle = MathUtils.random(0, 360);
        this.scale = MathUtils.random(SCALE_MIN, SCALE_MAX);
        this.scaleSign = MathUtils.randomSign();
        this.lifeTimer = new GameTimer(TIME_TO_LIFE);
    }

    public void activate(Vector2 position, DropType type, TextureRegion texture, float[] rgb) {
        this.type = type;
        this.texture = texture;
        this.textureW = texture.getRegionWidth();
        this.textureH = texture.getRegionHeight();
        this.position.set(position);
        this.renderPosition.recalculate(gameController,textureW/2f,textureH/2f);
        this.rgb = rgb;
        this.hitBox.set(this.position, textureW / 2);
        this.isActive = true;
        lifeTimer.reset();
    }

    private void activateParticles(float dt) {
        if (shineCurrentDelay >= SHINE_DELAY) {
            gameController.getParticleController().getEffectBuilder().circleShine(ParticleLayouts.TOP, position, textureW / 2f * (1 - lifeTimer.percent()), shineCurrentAngle, rgb[0], rgb[1], rgb[2]);
            shineCurrentAngle += SHINE_ANGLE_STEP;
            shineCurrentDelay = 0;
        } else shineCurrentDelay += dt;
    }

    private void deactivate(boolean isConsume) {
        isActive = false;
        if (!isConsume)
            gameController.getParticleController().getEffectBuilder().circleBlast(ParticleLayouts.TOP, position, rgb[0], rgb[1], rgb[2], 400f, 0.1f);
    }

    public void update(float dt) {
        lifeTimer.update(dt);
        if (lifeTimer.isReady()) deactivate(false);
        position.mulAdd(velocity, dt);
        renderPosition.recalculate(gameController, textureW / 2f, textureH / 2f);
        checkShipOncoming();
        activateParticles(dt);
        setNewScale(dt);
    }

    private void checkShipOncoming() {
        if (!renderPosition.isRenderable()) return;
        float[] playerPosition = gameController.getPlayer().getShip().getTextureCenterRealCS();
        float distanceSquare = (playerPosition[0] - renderPosition.x) * (playerPosition[0] - renderPosition.x) +
                (playerPosition[1] - renderPosition.y) * (playerPosition[1] - renderPosition.y);
        float angle = MathUtils.atan2((playerPosition[1] - renderPosition.y), (playerPosition[0] - renderPosition.x));
        float radius = RADIUS_ONCOMING_FACTOR * hitBox.radius;
        if (distanceSquare <= radius * radius)
            velocity.set(SPEED * MathUtils.cos(angle), SPEED * MathUtils.sin(angle));
        else velocity.set(0, 0);
    }

    public void render(SpriteBatch batch) {
        if (!renderPosition.isRenderable()) return;
        batch.draw(texture, renderPosition.x - textureW / 2f, renderPosition.y - textureH / 2f,
                textureW / 2f, textureH / 2f, textureW, textureH, scale, scale, 0);
    }

    private void setNewScale(float dt) {
        scale += scaleSign * (SCALE_MAX - SCALE_MIN) / SCALE_TIME * dt;
        if (scale >= SCALE_MAX) {
            scale = SCALE_MAX;
            scaleSign *= -1;
        } else if (scale <= SCALE_MIN) {
            scale = SCALE_MIN;
            scaleSign *= -1;
        }
    }

    public void consume() {
        controller.getEffect(type);
        gameController.getParticleController().getEffectBuilder().circleBlast(ParticleLayouts.TOP, position, rgb[0], rgb[1], rgb[2], 200f, 1f);
        deactivate(true);
    }

    public Circle getHitBox() {
        hitBox.set(renderPosition, textureW / 2);
        return hitBox;
    }
}
