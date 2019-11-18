package com.star.app.game;

public class GameController {
    private Background background;
    private Hero hero;
    private Asteroid asteroid;

    public Background getBackground() {
        return background;
    }

    public Hero getHero() {
        return hero;
    }

    public Asteroid getAsteroid() {
        return asteroid;
    }

    public GameController() {
        background = new Background(this);
        hero = new Hero();
        asteroid = new Asteroid();
    }

    public void update(float dt) {
        background.update(dt);
        asteroid.update(dt);
        hero.update(dt);
    }
}
