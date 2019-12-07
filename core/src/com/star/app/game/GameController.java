package com.star.app.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.star.app.game.asteroids.Asteroid;
import com.star.app.game.asteroids.AsteroidController;
import com.star.app.game.bullets.Bullet;
import com.star.app.game.bullets.BulletController;
import com.star.app.game.drops.Drop;
import com.star.app.game.drops.DropController;
import com.star.app.game.overlays.DebugOverlay;
import com.star.app.game.overlays.InfoOverlay;
import com.star.app.game.overlays.GamePauseOverlay;
import com.star.app.game.particles.ParticleController;
import com.star.app.game.pilots.Player;
import com.star.app.game.pilots.PlayerStatistic;
import com.star.app.screen.ScreenManager;

import java.util.List;

import static com.star.app.screen.ScreenManager.*;

public class GameController {
    public enum GameStatus {
        START("LEVEL %s STARTS!"), ACTIVE(null), DEAD("YOU ARE DEAD"), GAME_OVER("GAME OVER"), PAUSED(null), LEVEL_COMPLETE("LEVEL COMPLETE"), WIN("YOU WIN");

        private String msg;

        GameStatus(String msg) {
            this.msg = msg;
        }

        public String getMsg(Object... strings) {
            if (msg == null) return null;
            return String.format(msg, strings);
        }

        public String getMsg() {
            if (msg == null) return null;
            return msg;
        }
    }

    public final int SPACE_WIDTH = 4000;
    public final int SPACE_HEIGHT = 4000;
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
    private int[] seamlessMatrix;

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
        bulletController = new BulletController(this);
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
        seamlessMatrix = new int[]{0, 0};
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
        if (gameStatus != GameStatus.GAME_OVER && gameStatus != GameStatus.WIN) {
            player.update(dt);
            updateSeamlessMatrix();
        }
        bulletController.update(dt);
        asteroidController.update(dt);
        dropController.update(dt);
        particleController.update(dt);
        infoOverlay.update(dt);
        checkBulletsCollisions();
        if (!player.isDead()) checkPlayerCollisions(dt);
    }

    private void updateSeamlessMatrix() {
        Vector2 playerPosition = getPlayer().getShip().getPosition();
        if (playerPosition.x + SCREEN_HALF_WIDTH > SPACE_WIDTH) seamlessMatrix[0] = 1;
        else if (playerPosition.x - SCREEN_HALF_WIDTH < 0) seamlessMatrix[0] = -1;
        else seamlessMatrix[0] = 0;
        if (playerPosition.y + SCREEN_HALF_HEIGHT > SPACE_HEIGHT) seamlessMatrix[1] = 1;
        else if (playerPosition.y - SCREEN_HALF_HEIGHT < 0) seamlessMatrix[1] = -1;
        else seamlessMatrix[1] = 0;
    }

    public int[] getSeamlessVisibleIndex(Vector2 position, float halfWidth, float halfHeight) {
        Vector2 playerPosition = getPlayer().getShip().getPosition();
        int signX = (int) Math.signum(seamlessMatrix[0]);
        int signY = (int) Math.signum(seamlessMatrix[1]);
        for (int j = 0; j <= Math.abs(signY); j++) {
            for (int i = 0; i <= Math.abs(signX); i++) {
                if (Math.abs(playerPosition.x - (position.x + SPACE_WIDTH * i * signX)) <= SCREEN_HALF_WIDTH + halfWidth &&
                        Math.abs(playerPosition.y - (position.y + SPACE_HEIGHT * j * signY)) <= SCREEN_HALF_HEIGHT + halfHeight)
                    return new int[]{i * signX, j * signY};
            }
        }
        return null;
    }

    public int[] getSeamlessNearestIndex(Vector2 position) {
        Vector2 playerPosition = getPlayer().getShip().getPosition();
        float minDst = SPACE_WIDTH + SPACE_HEIGHT;
        int[] index = {0, 0};
        for (int j = -1; j <= 1; j++) {
            for (int i = -1; i <= 1; i++) {
                float dst = Vector2.dst(playerPosition.x, playerPosition.y, position.x + SPACE_WIDTH * i, position.y + SPACE_HEIGHT * j);
                if (dst < minDst) {
                    minDst = dst;
                    index[0] = i;
                    index[1] = j;
                }
            }
        }
        return index;
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
            ScreenManager.getInstance().changeScreen(ScreenManager.ScreenType.GAME_OVER);
        }
    }

    private void startNewLevel() {
        for (int i = 0; i < 10 + level / 3; i++) asteroidController.createNew();
    }

    public void seamlessTranslate(Vector2 position) {
        if (position.x < 0) position.x += SPACE_WIDTH;
        else if (position.x > SPACE_WIDTH) position.x -= SPACE_WIDTH;
        if (position.y < 0) position.y += SPACE_HEIGHT;
        else if (position.y > SPACE_HEIGHT) position.y -= SPACE_HEIGHT;
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

    public void dispose() {
        background.dispose();
    }
}
