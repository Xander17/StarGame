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
 * 5. 3 sec invulnerability after death
 * 6. Weapon shooting style
 * 7. Drop lifetime
 * 8. Loading line on Loading Screen
 * 9. Checking proper options
 * 10. Animation
 * */

// Домашнее задание:
// 1. Разбор кода
// 2. Кнопка паузы и выхода в меню на игровом экране
// 3. Сделайте Game Over Screen. Если у корабля < 0 HP, то игра
// переходит на Game Over Screen, и отображает там статистику игры
// (допустим пока только счет). По нажатию на экране Game Over Screen'а
// возвращаемся в меню
// 4. * Настройки со сменой управления

//Почему текст не отображается на loading screen

public class StarGame extends Game {
    private SpriteBatch batch;

    @Override
    public void create() {
        batch = new SpriteBatch();
        if (!Options.isOptionsExists()) {
            Options.createDefaultProperties();
        }
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
