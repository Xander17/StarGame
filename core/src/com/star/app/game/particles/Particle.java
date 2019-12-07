package com.star.app.game.particles;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.star.app.game.GameController;
import com.star.app.game.helpers.Poolable;

public class Particle implements Poolable {
    private Vector2 position;
    private Vector2 velocity;
    private TextureRegion texture;
    private int textureW, textureH;
    private float r1, g1, b1, a1, size1;
    private float r2, g2, b2, a2, size2;
    private float time;
    private float timeMax;
    private boolean isActive;
    private ParticleLayouts layout;

    public Particle() {
        position = new Vector2(0, 0);
        velocity = new Vector2(0, 0);
        isActive = false;
    }

    public void activate(TextureRegion texture, ParticleLayouts layout, float x, float y, float vx, float vy, float timeMax, float r1, float g1, float b1, float a1, float size1, float r2, float g2, float b2, float a2, float size2) {
        this.layout = layout;
        this.texture = texture;
        this.textureW = texture.getRegionWidth();
        this.textureH = texture.getRegionHeight();
        this.position.set(x, y);
        this.velocity.set(vx, vy);
        this.time = 0f;
        this.timeMax = timeMax;
        this.r1 = r1;
        this.r2 = r2;
        this.g1 = g1;
        this.g2 = g2;
        this.b1 = b1;
        this.b2 = b2;
        this.a1 = a1;
        this.a2 = a2;
        this.size1 = size1;
        this.size2 = size2;
        this.isActive = true;
    }

    public void deactivate() {
        isActive = false;
    }

    public void update(float dt, GameController gameController) {
        time += dt;
        position.mulAdd(velocity, dt);
        gameController.seamlessTranslate(position);
        if (time > timeMax) deactivate();
    }

    public void render(SpriteBatch batch, GameController gameController, float scaleCoefficient) {
        float t = time / timeMax;
        float scale = lerp(size1, size2, t) * scaleCoefficient;
        int[] index = gameController.getSeamlessVisibleIndex(position, textureW / 2f * scale, textureH / 2f * scale);
        if (index == null) return;
        batch.setColor(lerp(r1, r2, t), lerp(g1, g2, t), lerp(b1, b2, t), lerp(a1, a2, t));
        batch.draw(texture, position.x - textureW / 2f + gameController.SPACE_WIDTH * index[0],
                position.y - textureH / 2f + gameController.SPACE_HEIGHT * index[1],
                textureW / 2f, textureH / 2f, textureW, textureH,
                scale, scale, 0);
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    public float lerp(float value1, float value2, float point) {
        return value1 + (value2 - value1) * point;
    }

    public ParticleLayouts getLayout() {
        return layout;
    }
}
