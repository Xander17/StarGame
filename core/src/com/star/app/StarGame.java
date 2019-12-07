package com.star.app;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.star.app.screen.GameScreen;

public class StarGame extends Game {
    private SpriteBatch batch;
    private Screen screen;

    @Override
    public void create() {
        batch = new SpriteBatch();
        screen=new GameScreen(batch);
        setScreen(screen);
    }

    @Override
    public void render() {
        float dt = Gdx.graphics.getDeltaTime();
        getScreen().render(dt);
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
