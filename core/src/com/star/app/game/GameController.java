package com.star.app.game;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.star.app.game.asteroids.Asteroid;
import com.star.app.game.asteroids.AsteroidController;
import com.star.app.game.bullets.Bullet;
import com.star.app.game.bullets.BulletController;
import com.star.app.game.pilots.Player;

import java.util.List;

public class GameController {
    private final int ASTEROIDS_START_COUNT = 3;
    private final int ASTEROIDS_SCORE = 100;

    private Background background;
    private Player player;
    private AsteroidController asteroidController;
    private BulletController bulletController;
    private InfoOverlay infoOverlay;
    private DebugOverlay debugOverlay;

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

    public DebugOverlay getDebugOverlay() {
        return debugOverlay;
    }

    public GameController() {
        background = new Background(this);
        player = new Player(this);
        bulletController = new BulletController(this);
        asteroidController = new AsteroidController();
        infoOverlay = new InfoOverlay(this);
        debugOverlay = new DebugOverlay();
        for (int i = 0; i < ASTEROIDS_START_COUNT; i++) asteroidController.createNew();
    }

    public void update(float dt) {
        background.update(dt);
        player.update(dt);
        bulletController.update(dt);
        asteroidController.update(dt);
        checkBulletsCollisions();
        checkPlayerCollisions(dt);
    }

    private void checkBulletsCollisions() {
        List<Bullet> bullets = getBulletController().getActiveList();
        for (int i = 0; i < bullets.size(); i++) {
            Bullet bullet = bullets.get(i);
            List<Asteroid> asteroids = getAsteroidController().getActiveList();
            for (int j = 0; j < asteroids.size(); j++) {
                Asteroid asteroid = asteroids.get(j);
                if (bullet.checkHit(asteroid)) {
                    if (bullet.damageTarget(asteroid)) getPlayer().addScore((int) (ASTEROIDS_SCORE * asteroid.getMaxHealth()));
                    break;
                }
            }
        }
    }

    private void checkPlayerCollisions(float dt) {
        List<Asteroid> asteroids = getAsteroidController().getActiveList();
        for (int j = 0; j < asteroids.size(); j++) {
            Asteroid asteroid = asteroids.get(j);
            getPlayer().getShip().checkCollision(asteroid, dt);
        }
    }
}
