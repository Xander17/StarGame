package com.star.app.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.star.app.game.asteroids.Asteroid;
import com.star.app.game.asteroids.AsteroidController;
import com.star.app.game.bullets.Bullet;
import com.star.app.game.bullets.BulletController;
import com.star.app.game.drops.Drop;
import com.star.app.game.drops.DropController;
import com.star.app.game.overlays.InfoOverlay;
import com.star.app.game.overlays.GamePauseOverlay;
import com.star.app.game.particles.ParticleController;
import com.star.app.game.pilots.Player;
import com.star.app.game.pilots.PlayerStatistic;
import com.star.app.screen.ScreenManager;

import java.util.List;

public class GameController {
    private final int ASTEROIDS_START_COUNT = 3;
    private final int ASTEROIDS_SCORE = 100;
    private final float TIME_TO_RESPAWN = 3f;
    private final float GAMEOVER_MESSAGE_TIME = 3f;

    private Background background;
    private Player player;
    private AsteroidController asteroidController;
    private BulletController bulletController;
    private DropController dropController;
    private ParticleController particleController;
    private InfoOverlay infoOverlay;
    private GamePauseOverlay gamePauseOverlay;
    private float timeToRespawn;
    private float timeToGameover;
    private boolean isGameOver;
    private boolean isWin;
    private boolean paused;

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

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public boolean isPaused() {
        return paused;
    }

    public GamePauseOverlay getGamePauseOverlay() {
        return gamePauseOverlay;
    }

    public GameController(SpriteBatch batch) {
        background = new Background(this);
        player = new Player(this, 1);
        bulletController = new BulletController();
        asteroidController = new AsteroidController(this);
        dropController = new DropController(this);
        particleController = new ParticleController(this);
        infoOverlay = new InfoOverlay(this);
        gamePauseOverlay = new GamePauseOverlay(this, batch);
        timeToRespawn = 0;
        isWin = false;
        paused = false;
        for (int i = 0; i < ASTEROIDS_START_COUNT; i++) asteroidController.createNew();
    }

    public void setWin(boolean win) {
        isWin = win;
        if (isWin) player.getShip().setVelocity(0, 0);
    }

    public boolean isWin() {
        return isWin;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public void update(float dt) {
        if (paused) {
            gamePauseOverlay.update();
            return;
        }
        if (!isGameOver) checkRespawn(dt);
        else timeToGameover += dt;
        if (timeToGameover >= GAMEOVER_MESSAGE_TIME) {
            ScreenManager.getInstance().getGameOverScreen().uploadStatistic(player.getPlayerStatistic());
            ScreenManager.getInstance().changeScreen(ScreenManager.ScreenType.GAMEOVER);
        }
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

    public void dispose() {
        background.dispose();
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
                        getPlayer().getPlayerStatistic().add(PlayerStatistic.Stats.SCORE, ASTEROIDS_SCORE * asteroid.getMaxHealth());
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
