package com.star.app.game.bullets;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.star.app.game.GameController;
import com.star.app.game.helpers.Collisional;
import com.star.app.game.helpers.Poolable;
import com.star.app.game.helpers.RenderPosition;
import com.star.app.game.particles.ParticleLayouts;
import com.star.app.utils.Assets;

public class Bullet implements Poolable {
    private final float MAX_DISTANCE_TOLERANCE = 0.05f;

    private GameController gameController;
    private TextureRegion texture;
    private int textureW;
    private int textureH;
    private Vector2 position;
    private RenderPosition renderPosition;
    private float angle;
    private Vector2 velocity;
    private float damage;
    private float distancePassed;
    private float maxDistance;
    private boolean isActive;
    private boolean playerIsOwner;

    Bullet(GameController gameController) {
        this.gameController = gameController;
        this.texture = Assets.getInstance().getTextureAtlas().findRegion("bullet");
        this.textureW = texture.getRegionWidth();
        this.textureH = texture.getRegionHeight();
        this.position = new Vector2(0, 0);
        this.renderPosition = new RenderPosition(position);
        this.velocity = new Vector2(0, 0);
        this.angle = 0;
        this.isActive = false;
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    void activate(float x, float y, float angle, float velocityX, float velocityY, float damage, float maxDistance, boolean playerOwner) {
        this.position.set(x, y);
        this.angle = angle;
        this.velocity.set(velocityX, velocityY);
        this.renderPosition.recalculate(gameController, textureW / 2f, textureH / 2f);
        this.damage = damage;
        this.distancePassed = 0;
        this.maxDistance = maxDistance * (1 + MathUtils.random(-MAX_DISTANCE_TOLERANCE, MAX_DISTANCE_TOLERANCE));
        this.isActive = true;
        this.playerIsOwner = playerOwner;
    }

    private void deactivate() {
        isActive = false;
    }

    public void update(float dt) {
        position.mulAdd(velocity, dt);
        gameController.seamlessTranslate(position);
        checkPassedDistance(dt);
        renderPosition.recalculate(gameController, textureW / 2f, textureH / 2f);
    }

    private void checkPassedDistance(float dt) {
        distancePassed += velocity.len() * dt;
        if (distancePassed >= maxDistance) {
            gameController.getParticleController().getEffectBuilder().bulletDeactivate(ParticleLayouts.SHIP, getHitPointX(), getHitPointY(), velocity, textureW, textureH);
            deactivate();
        }
    }

    void render(SpriteBatch batch) {
        if (!renderPosition.isRenderable()) return;
        batch.draw(texture, renderPosition.x - textureW / 2f, renderPosition.y - textureH / 2f,
                textureW / 2f, textureH / 2f, textureW, textureH,
                1, 1, angle);
    }

    public boolean checkHit(Collisional obj) {
        return obj.getHitBox().contains(getHitPointX(), getHitPointY());
    }

    public boolean damageTarget(Collisional obj) {
        deactivate();
        return obj.takeDamage(damage);
    }

    private float getHitPointX() {
        return position.x + MathUtils.cosDeg(angle) * textureW / 2f;
    }

    private float getHitPointY() {
        return position.y + MathUtils.sinDeg(angle) * textureW / 2f;
    }

    public boolean isPlayerIsOwner() {
        return playerIsOwner;
    }
}
