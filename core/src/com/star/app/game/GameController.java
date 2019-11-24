package com.star.app.game;

import com.star.app.game.asteroids.Asteroid;
import com.star.app.game.asteroids.AsteroidController;
import com.star.app.game.bullets.Bullet;
import com.star.app.game.bullets.BulletController;
import com.star.app.game.pilots.Player;

import java.util.List;

public class GameController {
    private final int ASTEROIDS_START_COUNT = 3;
    private final int ASTEROIDS_SCORE = 100;
    private final float TIME_TO_RESPAWN = 3f;

    private Background background;
    private Player player;
    private AsteroidController asteroidController;
    private BulletController bulletController;
    private InfoOverlay infoOverlay;
    private DebugOverlay debugOverlay;
    private Statistic statistic;
    private float timeToRespawn;
    private boolean isGameOver;
    private boolean isWin;

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

    public Statistic getStatistic() {
        return statistic;
    }

    public GameController() {
        background = new Background(this);
        player = new Player(this);
        bulletController = new BulletController();
        asteroidController = new AsteroidController(this);
        infoOverlay = new InfoOverlay(this);
        debugOverlay = new DebugOverlay();
        statistic = new Statistic();
        timeToRespawn = 0;
        for (int i = 0; i < ASTEROIDS_START_COUNT; i++) asteroidController.createNew();
        isWin = false;
    }

    public void setWin(boolean win) {
        isWin = win;
    }

    public boolean isWin() {
        return isWin;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public void update(float dt) {
        checkRespawn(dt);
        background.update(dt);
        statistic.update(dt);
        if (!isGameOver &&!isWin) player.update(dt);
        bulletController.update(dt);
        asteroidController.update(dt);
        checkBulletsCollisions();
        if (!player.isDead()) checkPlayerCollisions(dt);
    }

    private void checkRespawn(float dt) {
        if (player.isDead() && player.getLives() == 0) isGameOver = true;
        else if (player.isDead()) timeToRespawn += dt;
        if (timeToRespawn >= TIME_TO_RESPAWN) {
            timeToRespawn = 0;
            player.setDeadStatus(false);
        }
    }

    private void checkBulletsCollisions() {
        List<Bullet> bullets = getBulletController().getActiveList();
        for (int i = 0; i < bullets.size(); i++) {
            Bullet bullet = bullets.get(i);
            List<Asteroid> asteroids = getAsteroidController().getActiveList();
            for (int j = 0; j < asteroids.size(); j++) {
                Asteroid asteroid = asteroids.get(j);
                if (bullet.checkHit(asteroid)) {
                    if (bullet.damageTarget(asteroid))
                        getStatistic().addScore((int) (ASTEROIDS_SCORE * asteroid.getMaxHealth()));
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
