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
    public enum GameStatus {
        START, ACTIVE, DEAD, GAME_OVER, PAUSED, LEVEL_COMPLETE, WIN;
    }

    private final int ASTEROIDS_SCORE = 100;
    private final float TIME_TO_RESPAWN = 3f;
    private final float GAME_OVER_MESSAGE_TIME = 3f;
    private final float TIME_TO_LEVEL_MESSAGE = 2f;

    private Background background;
    private Player player;
    private AsteroidController asteroidController;
    private BulletController bulletController;
    private DropController dropController;
    private ParticleController particleController;
    private InfoOverlay infoOverlay;
    private GamePauseOverlay gamePauseOverlay;
    private float timeToRespawn;
    private float timeToGameOver;
    private float timeToStart;
    private GameStatus gameStatus;
    private int level;

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

    public int getLevel() {
        return level;
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
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
        gameStatus = GameStatus.START;
        timeToRespawn = 0;
        timeToGameOver = 0;
        timeToStart = 0;
        level = 1;
    }

    public void update(float dt) {
        if (gameStatus == GameStatus.PAUSED) {
            gamePauseOverlay.update();
            return;
        }
        betweenLevels(dt);
        checkRespawn(dt);
        gameOverCountDown(dt);
        background.update(dt);
        if (gameStatus != GameStatus.GAME_OVER && gameStatus != GameStatus.WIN) player.update(dt);
        bulletController.update(dt);
        asteroidController.update(dt);
        dropController.update(dt);
        particleController.update(dt);
        infoOverlay.update(dt);
        checkBulletsCollisions();
        if (!player.isDead()) checkPlayerCollisions(dt);
    }

    private void betweenLevels(float dt) {
        if (gameStatus == GameStatus.LEVEL_COMPLETE) {
            timeToStart += dt;
            if (timeToStart >= TIME_TO_LEVEL_MESSAGE) {
                timeToStart = 0;
                level++;
                gameStatus = GameStatus.START;
            }
        }
        if (gameStatus == GameStatus.START) {
            timeToStart += dt;
            if (timeToStart >= TIME_TO_LEVEL_MESSAGE) {
                timeToStart = 0;
                gameStatus = GameStatus.ACTIVE;
                startNewLevel();
            }
        }
    }

    private void checkRespawn(float dt) {
        if (gameStatus != GameStatus.DEAD) return;
        timeToRespawn += dt;
        if (timeToRespawn >= TIME_TO_RESPAWN) {
            timeToRespawn = 0;
            player.setDeadStatus(false);
            gameStatus = GameStatus.ACTIVE;
        }
    }

    private void gameOverCountDown(float dt) {
        if (gameStatus != GameStatus.GAME_OVER) return;
        timeToGameOver += dt;
        if (timeToGameOver >= GAME_OVER_MESSAGE_TIME) {
            ScreenManager.getInstance().getGameOverScreen().uploadStatistic(player.getPlayerStatistic());
            ScreenManager.getInstance().changeScreen(ScreenManager.ScreenType.GAMEOVER);
        }
    }

    private void startNewLevel() {
        for (int i = 0; i < 1 + level / 3; i++) asteroidController.createNew();
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
