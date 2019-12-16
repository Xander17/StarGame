package com.star.app.game.ships;

import com.star.app.game.GameController;

import java.util.HashMap;

public class Updates {
    public enum Types {
        MAX_HEALTH(20, "healupdate"),
        DAMAGE(20, "damageupdate"),
        ROTATION_SPEED(20, "rotationspeed"),
        FORWARD_SPEED(20, "forwardspeed");

        private int maxLevel;
        private String texturePrefix;

        Types(int maxLevel, String texturePrefix) {
            this.maxLevel = maxLevel;
            this.texturePrefix = texturePrefix;
        }

        public String getTexturePrefix() {
            return texturePrefix;
        }
    }

    private HashMap<Types, Integer> map;
    private GameController gameController;
    private int lastUpdateLevels;

    public Updates(GameController gameController) {
        map = new HashMap<>();
        this.gameController = gameController;
        Types[] types = Types.values();
        for (int i = 0; i < types.length; i++) {
            map.put(types[i], 0);
        }
    }

    public int improve(Types type, boolean full) {
        int currentLevel = map.get(type);
        if (currentLevel == type.maxLevel) return -1;
        if (full) lastUpdateLevels = type.maxLevel - currentLevel;
        else lastUpdateLevels = 1;
        map.put(type, currentLevel + lastUpdateLevels);
        return currentLevel + lastUpdateLevels;
    }

    public boolean isUpdatable(Types type) {
        return map.get(type) < type.maxLevel;
    }

    public int getCost(Types type) {
        if (isUpdatable(type)) return (map.get(type) + 1) * 10;
        else return -1;
    }

    public void applyUpdate(Types type) {
        int effect = getLevelEffect(type, lastUpdateLevels);
        switch (type) {
            case MAX_HEALTH:
                gameController.getPlayer().getShip().updateMaxDurability(effect);
                break;
            case DAMAGE:
                gameController.getPlayer().getShip().getWeapon().updateGroupDamage(effect);
                break;
            case ROTATION_SPEED:
                gameController.getPlayer().getShip().updateRotationSpeed(effect);
                break;
            case FORWARD_SPEED:
                gameController.getPlayer().getShip().updateForwardMaxSpeed(effect);
        }
    }

    public int getLevelEffect(Types type) {
        return getLevelEffect(type, 1);
    }

    public int getLevelEffect(Types type, boolean full) {
        if (full) return getLevelEffect(type, map.get(type));
        else return getLevelEffect(type, 1);
    }

    public int getLevelEffect(Types type, int levels) {
        switch (type) {
            case MAX_HEALTH:
                return levels * 10;
            case DAMAGE:
                return levels;
            case ROTATION_SPEED:
                return levels * 7;
            case FORWARD_SPEED:
                return levels * 6;
        }
        return 0;
    }
}