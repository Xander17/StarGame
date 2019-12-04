package com.star.app.game.ships.updates;

import com.star.app.game.GameController;
import com.star.app.game.ships.Ship;

import java.util.HashMap;

public class Updates {
    public enum Types {
        MAX_HEALTH(20),
        DAMAGE(20);

        private int maxLevel;

        Types(int maxLevel) {
            this.maxLevel = maxLevel;
        }
    }

    private HashMap<Types, Integer> map;
    private GameController gameController;

    public Updates(GameController gameController) {
        map = new HashMap<>();
        this.gameController = gameController;
        Types[] types = Types.values();
        for (int i = 0; i < types.length; i++) {
            map.put(types[i], 0);
        }
    }

    public int improve(Types type) {
        int currentLevel = map.get(type);
        if (currentLevel == type.maxLevel) return -1;
        map.put(type, currentLevel + 1);
        return currentLevel + 1;
    }

    public boolean isUpdatable(Types type) {
        return map.get(type) < type.maxLevel;
    }

    public int getCost(Types type) {
        return (map.get(type) + 1) * 10;
    }

    public void update(Types type) {
        int effect = getLevelEffect(type);
        switch (type) {
            case MAX_HEALTH:
                gameController.getPlayer().getShip().updateMaxDurability(effect);
                break;
            case DAMAGE:
                gameController.getPlayer().getShip().getWeapon().updateGroupDamage(effect);
                break;
        }
    }

    public int getLevelEffect(Types type) {
        return getLevelEffect(type, false);
    }

    public int getLevelEffect(Types type, boolean full) {
        int levels;
        if (full) levels = map.get(type);
        else levels = 1;
        switch (type) {
            case MAX_HEALTH:
                return levels * 10;
            case DAMAGE:
                return levels;
        }
        return 0;
    }
}