package com.star.app.game.drops;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.star.app.game.GameController;
import com.star.app.game.helpers.Poolable;

import static com.star.app.screen.ScreenManager.SCREEN_HEIGHT;
import static com.star.app.screen.ScreenManager.SCREEN_WIDTH;

public class Drop implements Poolable {
    private final float SHINE_DELAY = 0.1f;
    private final float SHINE_ANGLE_STEP = 20;

    private GameController gameController;
    private TextureRegion texture;
    private int textureW, textureH;
    private boolean isActive;
    private DropType type;
    private Vector2 position;
    private DropController controller;
    private Circle hitBox;
    private float[] rgb;
    private float shineCurrentDelay;
    private float shineCurrentAngle;

    @Override
    public boolean isActive() {
        return isActive;
    }

    public Drop(GameController gameController, DropController controller) {
        this.gameController = gameController;
        this.controller = controller;
        this.isActive = false;
        this.position = new Vector2(0, 0);
        this.hitBox = new Circle();
        this.shineCurrentDelay = SHINE_DELAY;
        this.shineCurrentAngle = MathUtils.random(0, 360);
    }

    public void activate(Vector2 position, DropType type, TextureRegion texture, float[] rgb) {
        this.position.set(position);
        this.type = type;
        this.texture = texture;
        this.textureW = texture.getRegionWidth();
        this.textureH = texture.getRegionHeight();
        checkBounds();
        this.rgb = rgb;
        hitBox.set(this.position, textureW / 2);
        isActive = true;
    }

    private void activateParticles(float dt) {
        if (shineCurrentDelay >= SHINE_DELAY) {
            gameController.getParticleController().getEffectBuilder().circleShine(position, textureW / 2, shineCurrentAngle, rgb[0], rgb[1], rgb[2]);
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
    }

    public void update(float dt) {
        activateParticles(dt);
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, position.x - textureW / 2f, position.y - textureH / 2f);
    }

    public void consume() {
        controller.getEffect(type);
        gameController.getParticleController().getEffectBuilder().circleBlast(position, rgb[0], rgb[1], rgb[2]);
        deactivate();
    }

    public Circle getHitBox() {
        return hitBox;
    }
}
