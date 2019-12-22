package com.star.app.game.particles;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.star.app.game.GameController;
import com.star.app.game.helpers.Poolable;
import com.star.app.game.helpers.RenderPosition;

public class Particle implements Poolable {
    private GameController gameController;
    private Vector2 position;
    private RenderPosition renderPosition;
    private Vector2 velocity;
    private TextureRegion texture;
    private int textureW, textureH;
    private float r1, g1, b1, a1, size1;
    private float r2, g2, b2, a2, size2;
    private float time;
    private float timeMax;
    private boolean isActive;
    private ParticleLayouts layout;

    public Particle(GameController gameController) {
        this.gameController = gameController;
        this.position = new Vector2(0, 0);
        this.renderPosition = new RenderPosition(this.position);
        this.velocity = new Vector2(0, 0);
        this.isActive = false;
    }

    public void activate(TextureRegion texture, ParticleLayouts layout, float x, float y, float vx, float vy, float timeMax, float r1, float g1, float b1, float a1, float size1, float r2, float g2, float b2, float a2, float size2) {
        this.layout = layout;
        this.texture = texture;
        this.textureW = texture.getRegionWidth();
        this.textureH = texture.getRegionHeight();
        this.position.set(x, y);
        this.renderPosition.recalculate(gameController, textureW / 2f, textureH / 2f);
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

    public void update(float dt) {
        time += dt;
        position.mulAdd(velocity, dt);
        gameController.seamlessTranslate(position);
        renderPosition.recalculate(gameController, textureW / 2f, textureH / 2f);
        if (time > timeMax) deactivate();
    }

    public void render(SpriteBatch batch, float scaleCoefficient) {
        if (!renderPosition.isRenderable()) return;
        float t = time / timeMax;
        float scale = lerp(size1, size2, t) * scaleCoefficient;
        batch.setColor(lerp(r1, r2, t), lerp(g1, g2, t), lerp(b1, b2, t), lerp(a1, a2, t));
        batch.draw(texture, renderPosition.x - textureW / 2f, renderPosition.y - textureH / 2f,
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
