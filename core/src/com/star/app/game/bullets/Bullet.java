package com.star.app.game.bullets;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.star.app.game.GameController;
import com.star.app.game.helpers.Collisional;
import com.star.app.game.helpers.Poolable;
import com.star.app.game.particles.ParticleLayouts;
import com.star.app.utils.Assets;

public class Bullet implements Poolable {
    private final float MAX_DISTANCE_TOLERANCE = 0.05f;

    private TextureRegion texture;
    private int textureW;
    private int textureH;
    private Vector2 position;
    private float angle;
    private Vector2 velocity;
    private float damage;
    private float distancePassed;
    private float maxDistance;
    private boolean isActive;
    private float[] visibleIndex;
    private boolean playerOwner;

    @Override
    public boolean isActive() {
        return isActive;
    }

    Bullet() {
        this.texture = Assets.getInstance().getTextureAtlas().findRegion("bullet");
        this.textureW = texture.getRegionWidth();
        this.textureH = texture.getRegionHeight();
        this.position = new Vector2(0, 0);
        this.velocity = new Vector2(0, 0);
        this.angle = 0;
        this.isActive = false;
    }

    void activate(float x, float y, float angle, float velocityX, float velocityY, float damage, float maxDistance, boolean playerOwner) {
        this.position.set(x, y);
        this.angle = angle;
        this.velocity.set(velocityX, velocityY);
        this.damage = damage;
        this.distancePassed = 0;
        this.maxDistance = maxDistance * (1 + MathUtils.random(-MAX_DISTANCE_TOLERANCE, MAX_DISTANCE_TOLERANCE));
        this.isActive = true;
        this.playerOwner = playerOwner;
    }

    private void deactivate() {
        isActive = false;
    }

    public void update(float dt, GameController gameController) {
        position.mulAdd(velocity, dt);
        gameController.seamlessTranslate(position);
        checkPassedDistance(dt, gameController);
        visibleIndex = gameController.getSeamlessVisibleIndex(position, textureW / 2f, textureH / 2f);
    }

    private void checkPassedDistance(float dt, GameController gameController) {
        distancePassed += velocity.len() * dt;
        if (distancePassed >= maxDistance) {
            gameController.getParticleController().getEffectBuilder().bulletDeactivate(ParticleLayouts.SHIP, getHitPointX(), getHitPointY(), velocity, textureW, textureH);
            deactivate();
        }
    }

    void render(SpriteBatch batch, GameController gameController) {
        if (visibleIndex == null) return;
        batch.draw(texture, position.x - textureW / 2f + gameController.SPACE_WIDTH * visibleIndex[0],
                position.y - textureH / 2f + gameController.SPACE_HEIGHT * visibleIndex[1],
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

    public boolean isPlayerOwner() {
        return playerOwner;
    }
}
