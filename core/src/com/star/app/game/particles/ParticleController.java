package com.star.app.game.particles;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.star.app.game.GameController;
import com.star.app.game.helpers.ObjectPool;
import com.star.app.game.overlays.DebugOverlay;
import com.star.app.utils.Assets;

public class ParticleController extends ObjectPool<Particle> {
    private final float RANDOM_SCALE_COEFFICIENT = 3f;
    private final float RANDOM_SCALE_CHANCE = 0.03f;

    GameController gameController;
    private TextureRegion texture;
    private int textureW, textureH;
    private EffectBuilder effectBuilder;

    public ParticleController(GameController gameController) {
        this.gameController = gameController;
        texture = Assets.getInstance().getTextureAtlas().findRegion("particle1");
        this.textureW = texture.getRegionWidth();
        this.textureH = texture.getRegionHeight();
        effectBuilder = new EffectBuilder();
    }

    @Override
    public Particle getNew() {
        return new Particle(gameController);
    }

    public void setup(ParticleLayouts layout, float x, float y, float vx, float vy, float timeMax, float r1, float g1, float b1, float a1, float size1, float r2, float g2, float b2, float a2, float size2) {
        getActive().activate(texture, layout, x, y, vx, vy, timeMax, r1, g1, b1, a1, size1, r2, g2, b2, a2, size2);
    }

    public void update(float dt) {
        for (int i = 0; i < activeList.size(); i++) {
            activeList.get(i).update(dt);
        }
        checkFreeObjects();
    }

    public void render(SpriteBatch batch, ParticleLayouts layout) {
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        for (int i = 0; i < activeList.size(); i++) {
            Particle p = activeList.get(i);
            if (p.getLayout() == layout) p.render(batch, 1);
        }
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
        for (int i = 0; i < activeList.size(); i++) {
            float scaleCoefficient = 1;
            if (MathUtils.random() < RANDOM_SCALE_CHANCE && gameController.getGameStatus() != GameController.GameStatus.PAUSED) {
                scaleCoefficient = RANDOM_SCALE_COEFFICIENT;
            }
            Particle p = activeList.get(i);
            if (p.getLayout() == layout) p.render(batch, scaleCoefficient);
        }
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public EffectBuilder getEffectBuilder() {
        return effectBuilder;
    }

    public class EffectBuilder {
        public void exhaust(ParticleLayouts layout, float x, float y, Vector2 v, float angle, float size, float r, float g, float b) {
            float newSize;
            float spread = 0.3f;
            for (int i = 0; i < 5; i++) {
                newSize = (size / textureW) * MathUtils.random(0.8f, 1.3f);
                setup(layout, x + MathUtils.random(-spread * size, spread * size), y + MathUtils.random(-spread * size, spread * size),
                        -(v.len() * MathUtils.cosDeg(angle)) * MathUtils.random(0.3f, 0.5f) + MathUtils.random(-0.5f * size, 0.5f * size),
                        -(v.len() * MathUtils.sinDeg(angle)) * MathUtils.random(0.3f, 0.5f) + MathUtils.random(-0.5f * size, 0.5f * size),
                        0.3f, r, g, b, 1, newSize, 1f, 0f, 0f, 0.1f, newSize * 0.1f);
            }
        }

        public void circleShine(ParticleLayouts layout, Vector2 position, float radius, float angle, float r, float g, float b) {
            float speed = 10;
            setup(layout, position.x + radius * MathUtils.cosDeg(angle), position.y + radius * MathUtils.sinDeg(angle),
                    speed * MathUtils.cosDeg(angle), speed * MathUtils.sinDeg(angle), 1f, r, g, b, 1, 0.5f, r, g, b, 0.7f, 0.1f);
            setup(layout, position.x + radius * MathUtils.cosDeg(angle + 180), position.y + radius * MathUtils.sinDeg(angle + 180),
                    speed * MathUtils.cosDeg(angle + 180), speed * MathUtils.sinDeg(angle + 180), 1f, r, g, b, 1, 0.5f, r, g, b, 0.7f, 0.1f);
        }

        public void circleBlast(ParticleLayouts layout, Vector2 position, float r, float g, float b, float speed, float time) {
            for (int i = 0; i < 360; i += 18) {
                setup(layout, position.x, position.y, speed * MathUtils.cosDeg(i), speed * MathUtils.sinDeg(i), time,
                        r, g, b, 1f, 0.5f, 1f, 1f, 1f, 0.3f, 0.1f);
            }
        }

        public void bulletDeactivate(ParticleLayouts layout, float posX, float posY, Vector2 velocity, float width, float height) {
            height *= 0.9f;
            width *= 0.2f;
            float angle = velocity.angle();
            int count = 10;
            for (int i = 0; i < count; i++) {
                float lenH = -height / 2 + MathUtils.random(i * (height / count), (i + 1) * (height / count));
                float lenW = -width / 2 + MathUtils.random(width);
                setup(layout, posX + lenH * MathUtils.cosDeg(angle + 90) + lenW * MathUtils.cosDeg(angle),
                        posY + lenH * MathUtils.sinDeg(angle + 90) + lenW * MathUtils.sinDeg(angle),
                        velocity.x, velocity.y, 0.3f,
                        1f, 0.8f, 0f, 1f, 0.1f,
                        1f, 0.4f, 0f, 0.3f, 0.1f);
            }
        }

        public void bigBlast(ParticleLayouts layout, Vector2 position, float radius, float r1, float g1, float b1, float r2, float g2, float b2) {
            float time = 0.5f;
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 360; j += 36) {
                    float vx = radius / (2 * time) - (i * 10) * MathUtils.cosDeg(j);
                    float vy = radius / (2 * time) - (i * 10) * MathUtils.sinDeg(j);
                    setup(layout, position.x, position.y, vx, vy, time, r1, g1, b1, 1f, 1f, r2, g2, b2, 0, radius / 4);
                }
            }
        }
    }
}
