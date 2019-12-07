package com.star.app.game.drops;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.star.app.game.GameController;
import com.star.app.game.helpers.Poolable;
import com.star.app.game.particles.ParticleLayouts;

import static com.star.app.screen.ScreenManager.SCREEN_HEIGHT;
import static com.star.app.screen.ScreenManager.SCREEN_WIDTH;

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
    private Vector2 velocity;
    private DropController controller;
    private Circle hitBox;
    private float[] rgb;
    private float shineCurrentDelay;
    private float shineCurrentAngle;
    private float timeToLife;
    private float scale;
    private float scaleTime;
    private float scaleSign;
    private float[] visibleIndex;

    @Override
    public boolean isActive() {
        return isActive;
    }

    public Drop(GameController gameController, DropController controller) {
        this.gameController = gameController;
        this.controller = controller;
        this.isActive = false;
        this.position = new Vector2(0, 0);
        this.velocity = new Vector2(0, 0);
        this.hitBox = new Circle();
        this.shineCurrentDelay = SHINE_DELAY;
        this.shineCurrentAngle = MathUtils.random(0, 360);
        this.scaleTime = 0;
        this.scale = MathUtils.random(SCALE_MIN, SCALE_MAX);
        this.scaleSign = MathUtils.randomSign();
    }

    public void activate(Vector2 position, DropType type, TextureRegion texture, float[] rgb) {
        this.position.set(position);
        this.type = type;
        this.texture = texture;
        this.textureW = texture.getRegionWidth();
        this.textureH = texture.getRegionHeight();
        checkBounds();
        this.rgb = rgb;
        this.hitBox.set(this.position, textureW / 2);
        this.isActive = true;
        this.timeToLife = TIME_TO_LIFE;
    }

    private void activateParticles(float dt) {
        if (shineCurrentDelay >= SHINE_DELAY) {
            gameController.getParticleController().getEffectBuilder().circleShine(ParticleLayouts.TOP, position, textureW / 2f * timeToLife / TIME_TO_LIFE, shineCurrentAngle, rgb[0], rgb[1], rgb[2]);
            shineCurrentAngle += SHINE_ANGLE_STEP;
            shineCurrentDelay = 0;
        } else shineCurrentDelay += dt;
    }

    private void checkBounds() {
        if (position.x < textureW / 2f) position.x = textureW / 2;
        else if (position.x > SCREEN_WIDTH - textureW / 2f) position.x = SCREEN_WIDTH - textureW / 2f;
        if (position.y < textureH / 2f) position.y = textureH / 2;
        else if (position.y > SCREEN_HEIGHT - textureH / 2f) position.y = SCREEN_HEIGHT - textureH / 2f;
    }

    private void deactivate() {
        isActive = false;
        if (timeToLife <= 0)
            gameController.getParticleController().getEffectBuilder().circleBlast(ParticleLayouts.TOP, position, rgb[0], rgb[1], rgb[2], 400f, 0.1f);
    }

    public void update(float dt) {
        timeToLife -= dt;
        if (timeToLife <= 0) deactivate();
        position.mulAdd(velocity, dt);
        hitBox.set(this.position, textureW / 2);
        visibleIndex = gameController.getSeamlessVisibleIndex(position, textureW / 2f, textureH / 2f);
        checkShipOncoming();
        activateParticles(dt);
        setNewScale(dt);
    }

    private void checkShipOncoming() {
        if (visibleIndex == null) {
            velocity.set(0, 0);
            return;
        }
        float[] playerPosition = gameController.getPlayer().getShip().getTextureCenterRealCS();
        float newX = position.x + gameController.SPACE_WIDTH * visibleIndex[0];
        float newY = position.y + gameController.SPACE_HEIGHT * visibleIndex[1];
        float distanceSquare = (playerPosition[0] - newX) * (playerPosition[0] - newX) +
                (playerPosition[1] - newY) * (playerPosition[1] - newY);
        float angle = MathUtils.atan2((playerPosition[1] - newY), (playerPosition[0] - newX));
        float radius = RADIUS_ONCOMING_FACTOR * hitBox.radius;
        if (distanceSquare <= radius * radius)
            velocity.set(SPEED * MathUtils.cos(angle), SPEED * MathUtils.sin(angle));
        else velocity.set(0, 0);
    }

    public void render(SpriteBatch batch) {
        if (visibleIndex == null) return;
        batch.draw(texture, position.x - textureW / 2f + gameController.SPACE_WIDTH * visibleIndex[0],
                position.y - textureH / 2f + gameController.SPACE_HEIGHT * visibleIndex[1],
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
        deactivate();
    }

    public Circle getHitBox() {
        return hitBox;
    }
}
