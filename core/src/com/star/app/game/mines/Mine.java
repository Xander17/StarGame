package com.star.app.game.mines;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.star.app.game.GameController;
import com.star.app.game.asteroids.Asteroid;
import com.star.app.game.helpers.Collisional;
import com.star.app.game.helpers.GameTimer;
import com.star.app.game.helpers.Poolable;
import com.star.app.game.helpers.RenderPosition;
import com.star.app.game.particles.ParticleLayouts;
import com.star.app.game.pilots.Enemy;

import java.util.List;

public class Mine implements Poolable {

    private final float CHARGE_TIME = 5f;

    private GameController gameController;
    private Vector2 position;
    private RenderPosition renderPosition;
    private Vector2 tmpVector;
    private float angle;
    private float damage;
    private float blastPower;
    private float blastRadius;
    private TextureRegion[][] texture;
    private float textureW, textureH;
    private int textureIndex;
    private float frameTime;
    private GameTimer textureTimer;
    private boolean isActive;

    public Mine(GameController gameController, TextureRegion[][] texture) {
        this.gameController = gameController;
        this.texture = texture;
        textureW = texture[0][0].getRegionWidth();
        textureH = texture[0][0].getRegionHeight();
        frameTime = 1f / texture[0].length;
        position = new Vector2(0, 0);
        renderPosition = new RenderPosition(position);
        tmpVector = new Vector2(0, 0);
        textureTimer = new GameTimer(CHARGE_TIME);
        isActive = false;
    }

    public void activate(float x, float y, float damage, float blastPower, float blastRadius) {
        this.position.set(x, y);
        textureIndex = 0;
        angle = MathUtils.random(359);
        textureTimer.reset();
        this.damage = damage;
        this.blastPower = blastPower;
        this.blastRadius = blastRadius;
        isActive = true;
    }

    private void deactivate() {
        isActive = false;
    }

    public void update(float dt) {
        renderPosition.recalculate(gameController, textureW / 2, textureH / 2);
        textureTimer.update(dt);
        if (textureTimer.isReady()) {
            blast();
            deactivate();
        } else {
            textureIndex = (int) (textureTimer.percent() / frameTime);
        }
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture[0][textureIndex], renderPosition.x - textureW / 2, renderPosition.y - textureH / 2,
                textureW / 2, textureH / 2, textureW, textureH, 1, 1, angle);
    }

    private void blast() {
        List<Asteroid> asteroids = gameController.getAsteroidController().getActiveList();
        float size = asteroids.size();
        for (int i = 0; i < size; i++) {
            impulseDamage(asteroids.get(i));
        }
        List<Enemy> enemies = gameController.getEnemyController().getActiveList();
        size = enemies.size();
        for (int i = 0; i < size; i++) {
            impulseDamage(enemies.get(i).getShip());
        }
        impulseDamage(gameController.getPlayer().getShip());
        gameController.getParticleController().getEffectBuilder().mineBlast(ParticleLayouts.TOP, position, blastRadius);
    }

    private void impulseDamage(Collisional obj) {
        tmpVector.set(obj.getPosition()).sub(position);
        float dst = tmpVector.len() - obj.getHitBox().radius;
        if (dst <= blastRadius) {
            float powerFactor;
            if (dst > blastRadius / 2) powerFactor = 2 - dst / (blastRadius / 2);
            else powerFactor = 1;
            obj.takeImpulseDamage(blastPower * powerFactor, tmpVector.angle(), damage * powerFactor);
        }
    }

    @Override
    public boolean isActive() {
        return isActive;
    }
}
