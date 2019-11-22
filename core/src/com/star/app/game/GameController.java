package com.star.app.game;

import com.star.app.game.asteroids.Asteroid;
import com.star.app.game.asteroids.AsteroidController;
import com.star.app.game.bullets.Bullet;
import com.star.app.game.bullets.BulletController;
import com.star.app.game.pilots.Player;

import java.util.List;

public class GameController {
    private final int ASTEROIDS_START_COUNT = 3;

    private Background background;
    private Player player;
    private AsteroidController asteroidController;
    private BulletController bulletController;
    private InfoOverlay infoOverlay;

    public Background getBackground() {
        return background;
    }

    public Player getPlayer() {
        return player;
    }

    public BulletController getBulletController() {
        return bulletController;
    }

    public AsteroidController getAsteroidController() {
        return asteroidController;
    }

    public InfoOverlay getInfoOverlay() {
        return infoOverlay;
    }

    public GameController() {
        background = new Background(this);
        player = new Player(this);
        bulletController = new BulletController(this);
        asteroidController = new AsteroidController();
        infoOverlay = new InfoOverlay(this);
        for (int i = 0; i < ASTEROIDS_START_COUNT; i++) asteroidController.createNew();
    }

    public void update(float dt) {
        background.update(dt);
        player.update(dt);
        bulletController.update(dt);
        asteroidController.update(dt);
        checkCollisions(dt);
    }

    private void checkCollisions(float dt) {
        List<Bullet> bullets = getBulletController().getActiveList();
        for (int i = 0; i < bullets.size(); i++) {
            Bullet b = bullets.get(i);
            b.update(dt);
            List<Asteroid> asteroids = getAsteroidController().getActiveList();
            for (int j = 0; j < asteroids.size(); j++) {
                Asteroid a = asteroids.get(j);
                if (a.getHitBox().contains(b.getHitPointX(), b.getHitPointY())) {
                    if (a.takeDamage(getBulletController().getBASE_DAMAGE()))
                        getPlayer().addScore(100 * a.getMaxHealth());
                    b.deactivate();
                    break;
                }
            }
        }
    }
}
