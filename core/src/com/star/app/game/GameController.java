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
    private float timeToRespawn;
    private boolean gameOver;

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
        timeToRespawn=0;
        for (int i = 0; i < ASTEROIDS_START_COUNT; i++) asteroidController.createNew();
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void update(float dt) {
        if (player.isDead()&&player.getLives()==0) gameOver=true;
        else if(player.isDead()) timeToRespawn+=dt;
        if(timeToRespawn>=TIME_TO_RESPAWN) {
            timeToRespawn=0;
            player.setDeadStatus(false);
        }
        background.update(dt);
        player.update(dt);
        bulletController.update(dt);
        asteroidController.update(dt);
        checkBulletsCollisions();
        if(!player.isDead()) checkPlayerCollisions(dt);
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
                        getPlayer().addScore((int) (ASTEROIDS_SCORE * asteroid.getMaxHealth()));
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
