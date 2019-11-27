package com.star.app.game;

import com.star.app.game.asteroids.Asteroid;
import com.star.app.game.asteroids.AsteroidController;
import com.star.app.game.bullets.Bullet;
import com.star.app.game.bullets.BulletController;
import com.star.app.game.drops.Drop;
import com.star.app.game.drops.DropController;
import com.star.app.game.overlays.DebugOverlay;
import com.star.app.game.overlays.InfoOverlay;
import com.star.app.game.particles.ParticleController;
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
    private DropController dropController;
    private ParticleController particleController;
    private InfoOverlay infoOverlay;
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

    public DropController getDropController() {
        return dropController;
    }

    public ParticleController getParticleController() {
        return particleController;
    }

    public GameController() {
        background = new Background(this);
        player = new Player(this);
        bulletController = new BulletController();
        asteroidController = new AsteroidController(this);
        dropController = new DropController(this);
        particleController = new ParticleController();
        infoOverlay = new InfoOverlay(this);
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
        if (!isGameOver && !isWin) player.update(dt);
        bulletController.update(dt);
        asteroidController.update(dt);
        dropController.update(dt);
        particleController.update(dt);
        infoOverlay.update(dt);
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
                        getPlayer().addScore((int) (ASTEROIDS_SCORE * asteroid.getMaxHealth()));
                    break;
                }
            }
        }
    }

    private void checkPlayerCollisions(float dt) {
        List<Asteroid> asteroids = getAsteroidController().getActiveList();
        for (int i = 0; i < asteroids.size(); i++) {
            getPlayer().getShip().checkCollision(asteroids.get(i), dt);
        }
        List<Drop> drops = getDropController().getActiveList();
        for (int i = 0; i < drops.size(); i++) {
            getPlayer().getShip().checkDropItem(drops.get(i));
        }
    }
}
