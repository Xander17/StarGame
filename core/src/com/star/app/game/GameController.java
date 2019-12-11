package com.star.app.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.star.app.game.asteroids.Asteroid;
import com.star.app.game.asteroids.AsteroidController;
import com.star.app.game.bullets.Bullet;
import com.star.app.game.bullets.BulletController;
import com.star.app.game.drops.Drop;
import com.star.app.game.drops.DropController;
import com.star.app.game.overlays.InfoOverlay;
import com.star.app.game.overlays.GamePauseOverlay;
import com.star.app.game.particles.ParticleController;
import com.star.app.game.pilots.Enemy;
import com.star.app.game.pilots.EnemyController;
import com.star.app.game.pilots.Player;
import com.star.app.game.pilots.PlayerStatistic;
import com.star.app.game.ships.Ship;
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
    private final int ENEMY_SCORE = 500;
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
    private EnemyController enemyController;
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

    public EnemyController getEnemyController() {
        return enemyController;
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
        enemyController = new EnemyController(this);
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
        }
        enemyController.update(dt);
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
            ScreenManager.getInstance().changeScreen(ScreenManager.ScreenType.GAME_OVER);
        }
    }

    private void startNewLevel() {
        for (int i = 0; i < 10 + level / 3; i++) asteroidController.createNew();
        for (int i = 0; i < 0 + level / 5; i++) enemyController.createNew();
    }

    public Vector2 getRandomStartPoint(float textureRealSizeHalfW, float textureRealSizeHalfH) {
        Vector2 playerPosition = player.getShip().getPosition();
        float x = playerPosition.x;
        float y = playerPosition.y;
        while (Math.abs(x - playerPosition.x) <= SCREEN_HALF_WIDTH + textureRealSizeHalfW ||
                Math.abs(y - playerPosition.y) <= SCREEN_HALF_HEIGHT + textureRealSizeHalfH) {
            x = MathUtils.random(0, SPACE_WIDTH);
            y = MathUtils.random(0, SPACE_HEIGHT);
        }
        return new Vector2(x, y);
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
            if (!bullet.isActive()) break;
            List<Asteroid> asteroids = getAsteroidController().getActiveList();
            for (int j = 0; j < asteroids.size(); j++) {
                Asteroid asteroid = asteroids.get(j);
                if (!asteroid.isActive()) break;
                if (bullet.checkHit(asteroid)) {
                    if (bullet.damageTarget(asteroid) && bullet.isPlayerIsOwner())
                        getPlayer().getPlayerStatistic().add(PlayerStatistic.Stats.SCORE, ASTEROIDS_SCORE * asteroid.getMaxHealth());
                    break;
                }
            }
            List<Enemy> enemies = getEnemyController().getActiveList();
            for (int j = 0; j < enemies.size(); j++) {
                if (!enemies.get(j).isActive()) break;
                Ship ship = enemies.get(j).getShip();
                if (bullet.checkHit(ship) && bullet.isPlayerIsOwner()) {
                    if (bullet.damageTarget(ship))
                        getPlayer().getPlayerStatistic().add(PlayerStatistic.Stats.SCORE, ENEMY_SCORE * ship.getMaxDurability());
                    break;
                }
            }
            Ship ship = player.getShip();
            if (!ship.isShipDestroyed() && !bullet.isPlayerIsOwner() && bullet.checkHit(ship))
                bullet.damageTarget(ship);
        }
    }

    private void checkPlayerCollisions(float dt) {
        List<Asteroid> asteroids = getAsteroidController().getActiveList();
        List<Enemy> enemies = getEnemyController().getActiveList();
        for (int i = 0; i < asteroids.size(); i++) {
            getPlayer().getShip().checkCollision(asteroids.get(i), dt);
//            for (int j = 0; j < enemies.size(); j++) {
//                enemies.get(j).getShip().checkCollision(asteroids.get(i), dt);
//            }
        }
        for (int i = 0; i < enemies.size(); i++) {
            getPlayer().getShip().checkCollision(enemies.get(i).getShip(), dt);
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
