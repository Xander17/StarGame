package com.star.app.game.ships.parts;

import com.star.app.game.GameController;
import com.star.app.game.helpers.GameTimer;
import com.star.app.game.pilots.PlayerStatistic;
import com.star.app.game.ships.Ship;

import java.util.ArrayList;
import java.util.List;

public class Weapon {
    private GameController gameController;
    private Ship ship;
    private List<Gun> guns;
    private int groupsCount;
    private float groupDamage;
    private int currentGunGroup;
    private GameTimer fireTimer;
    private int maxBullets;
    private int bullets;

    public Weapon(GameController gameController, Ship ship, float shootDelay, int bullets, float groupDamage) {
        this.gameController = gameController;
        this.ship = ship;
        this.currentGunGroup = 0;
        this.fireTimer = new GameTimer(shootDelay);
        this.guns = new ArrayList<>();
        this.bullets = bullets;
        this.maxBullets = bullets;
        this.groupDamage = groupDamage;
    }

    public void setGunGroup(Gun... guns) {
        for (int i = 0; i < guns.length; i++) {
            Gun gun = guns[i];
            addGun(gun, guns.length);
        }
        groupsCount++;
    }

    public void setGunSeparate(Gun gun) {
        addGun(gun, 1);
        groupsCount++;
    }

    private void addGun(Gun gun, int countInGroup) {
        gun.setGroupIndex(groupsCount);
        gun.setGroupDamage(groupDamage, countInGroup);
        this.guns.add(gun);
    }

    public void update(float dt) {
        fireTimer.update(dt);
    }

    public void fire(boolean playerIsOwner) {
        if (!fireTimer.isReady()) return;
        for (int i = 0; i < guns.size(); i++) {
            if (bullets == 0) break;
            if (guns.get(i).getGroupIndex() == currentGunGroup) {
                guns.get(i).fire(gameController, ship, playerIsOwner);
                bullets--;
                gameController.getPlayer().getPlayerStatistic().inc(PlayerStatistic.Stats.BULLETS_SPENT);
            }
        }
        fireTimer.reset();
        if (currentGunGroup == groupsCount) currentGunGroup = 0;
        else currentGunGroup++;
    }

    public int getBullets() {
        return bullets;
    }

    public void addBullets(int amount) {
        this.bullets += amount;
        if (bullets > maxBullets) bullets = maxBullets;
    }

    public int getMaxBullets() {
        return maxBullets;
    }

    public void updateGroupDamage(float amount) {
        this.groupDamage += amount;
        for (int i = 0; i < guns.size(); i++) {
            guns.get(i).updateGroupDamage(amount);
        }
    }
}
