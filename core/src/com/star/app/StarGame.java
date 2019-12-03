package com.star.app;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.star.app.screen.GameScreen;
import com.star.app.screen.ScreenManager;
import com.star.app.utils.Options;

/* TODO
 * 1. Stars split texture
 * 2. Asteroid split texture
 * 3. Elastic collisions
 * 4. Particles
 *   - exhaust rotate
 *   - bullet
 *   - bullet hit
 *   - (?) collision
 *   - gun smoke
 * 6. Weapon shooting styles
 * 10. Animation
 * 11. Stage controller in Menu Screen
 * */

public class StarGame extends Game {
    private SpriteBatch batch;

    @Override
    public void create() {
        batch = new SpriteBatch();
        ScreenManager.getInstance().init(batch, this);
        ScreenManager.getInstance().changeScreen(ScreenManager.ScreenType.MENU);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        float dt = Gdx.graphics.getDeltaTime();
        getScreen().render(dt);
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
