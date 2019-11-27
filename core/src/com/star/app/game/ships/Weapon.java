package com.star.app.game.ships;

import com.star.app.game.GameController;

import java.util.ArrayList;
import java.util.List;

public class Weapon {

    private GameController gameController;
    private Ship ship;
    private List<Gun> guns;
    private int groupsCount;
    private int currentGunGroup;
    private float shootDelay;
    private float currentDelay;
    private int maxBullets;
    private int bullets;

    public Weapon(GameController gameController, Ship ship, float shootDelay, int bullets) {
        this.gameController = gameController;
        this.ship = ship;
        this.currentGunGroup = 0;
        this.shootDelay = shootDelay;
        this.currentDelay = shootDelay;
        this.guns = new ArrayList<>();
        this.bullets = bullets;
        this.maxBullets = bullets;
    }

    public void setGunGroup(Gun[] guns) {
        for (int i = 0; i < guns.length; i++) {
            Gun gun = guns[i];
            gun.setGroupIndex(groupsCount);
            this.guns.add(gun);
        }
        groupsCount++;
    }

    public void setGunSeparate(Gun gun) {
        gun.setGroupIndex(groupsCount);
        this.guns.add(gun);
        groupsCount++;
    }

    public void update(float dt) {
        if (currentDelay < shootDelay) currentDelay += dt;
    }

    void fire() {
        if (currentDelay < shootDelay) return;
        for (int i = 0; i < guns.size(); i++) {
            if (bullets == 0) break;
            if (guns.get(i).getGroupIndex() == currentGunGroup) {
                guns.get(i).fire(gameController, ship);
                bullets--;
            }
        }
        currentDelay = 0;
        if (currentGunGroup == groupsCount) currentGunGroup = 0;
        else currentGunGroup++;
    }

    public int getBullets() {
        return bullets;
    }

    public void addBullets(int amount) {
        this.bullets += amount;
        //if (bullets > maxBullets) bullets = maxBullets;
    }
}
