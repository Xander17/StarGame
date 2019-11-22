package com.star.app.game;

public class GameController {
    private Background background;
    //private Hero hero;
    private Player player;
    private AsteroidController asteroidController;
    private BulletController bulletController;

    public Background getBackground() {
        return background;
    }

//    public Hero getHero() {
//        return hero;
//    }

    public Player getPlayer() {
        return player;
    }

    public BulletController getBulletController() {
        return bulletController;
    }

    public AsteroidController getAsteroidController() {
        return asteroidController;
    }

    public GameController() {
        background = new Background(this);
        //hero = new Hero(this);
        player=new Player(this);
        bulletController = new BulletController(this);
        asteroidController = new AsteroidController();
        asteroidController.createNew();
    }

    public void update(float dt) {
        background.update(dt);
        //hero.update(dt);
        player.update(dt);
        bulletController.update(dt);
        asteroidController.update(dt);
    }
}
