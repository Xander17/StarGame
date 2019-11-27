package com.star.app.game.particles;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.star.app.game.helpers.ObjectPool;
import com.star.app.utils.Assets;

public class ParticleController extends ObjectPool<Particle> {
    private final float RANDOM_SCALE_COEFFICIENT = 3f;
    private final float RANDOM_SCALE_CHANCE = 0.03f;

    private TextureRegion texture;
    private int textureW, textureH;
    private EffectBuilder effectBuilder;

    public ParticleController() {
        texture = Assets.getInstance().getTextureAtlas().findRegion("particle1");
        this.textureW = texture.getRegionWidth();
        this.textureH = texture.getRegionHeight();
        effectBuilder = new EffectBuilder();
    }

    @Override
    public Particle getNew() {
        return new Particle();
    }

    public void setup(float x, float y, float vx, float vy, float timeMax, float r1, float g1, float b1, float a1, float size1, float r2, float g2, float b2, float a2, float size2) {
        getActive().activate(texture, x, y, vx, vy, timeMax, r1, g1, b1, a1, size1, r2, g2, b2, a2, size2);
    }

    public void update(float dt) {
        for (int i = 0; i < activeList.size(); i++) {
            activeList.get(i).update(dt);
        }
    }

    public void render(SpriteBatch batch) {
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        for (int i = 0; i < activeList.size(); i++) {
            activeList.get(i).render(batch, 1);
        }
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
        for (int i = 0; i < activeList.size(); i++) {
            float scaleCoefficient = 1;
            if (MathUtils.random() < RANDOM_SCALE_CHANCE) {
                scaleCoefficient = RANDOM_SCALE_COEFFICIENT;
            }
            activeList.get(i).render(batch, scaleCoefficient);
        }
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public EffectBuilder getEffectBuilder() {
        return effectBuilder;
    }

    public class EffectBuilder {
        public void exhaust(float x, float y, Vector2 v, float angle, float size, float r, float g, float b) {
            float newSize;
            for (int i = 0; i < 5; i++) {
                newSize = (size / textureW) * MathUtils.random(0.9f, 1.1f);
                setup(x + MathUtils.random(-0.3f * size, 0.3f * size), y + MathUtils.random(-0.3f * size, 0.3f * size),
                        -(v.len() * MathUtils.cosDeg(angle)) * MathUtils.random(0.3f, 0.5f) + MathUtils.random(-0.5f * size, 0.5f * size),
                        -(v.len() * MathUtils.sinDeg(angle)) * MathUtils.random(0.3f, 0.5f) + MathUtils.random(-0.5f * size, 0.5f * size),
                        0.3f, r, g, b, 1, newSize, 1f, 0f, 0f, 0.1f, newSize * 0.1f);
            }
        }

        public void circleShine(Vector2 position, float radius, float angle, float r, float g, float b) {
            float speed = 10;
            setup(position.x + radius * MathUtils.cosDeg(angle), position.y + radius * MathUtils.sinDeg(angle),
                    speed * MathUtils.cosDeg(angle), speed * MathUtils.sinDeg(angle), 0.5f, r, g, b, 1, 0.5f, r, g, b, 0.7f, 0.1f);
            setup(position.x + radius * MathUtils.cosDeg(angle + 180), position.y + radius * MathUtils.sinDeg(angle + 180),
                    speed * MathUtils.cosDeg(angle + 180), speed * MathUtils.sinDeg(angle + 180), 0.5f, r, g, b, 1, 0.5f, r, g, b, 0.7f, 0.1f);
        }

        public void circleBlast(Vector2 position, float r, float g, float b) {
            float speed = 200;
            for (int i = 0; i < 360; i += 18) {
                setup(position.x, position.y, speed * MathUtils.cosDeg(i), speed * MathUtils.sinDeg(i), 1f,
                        r, g, b, 1f, 0.5f, 1f, 1f, 1f, 0.3f, 0.1f);
            }
        }
    }
}
