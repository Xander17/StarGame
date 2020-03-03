package com.star.app.game.pilots;

import java.util.Comparator;
import java.util.TreeMap;

public class PlayerStatistic {
    private TreeMap<Stats, Float> map;

    public PlayerStatistic() {
        map = new TreeMap<>(new Comparator<Stats>() {
            @Override
            public int compare(Stats o1, Stats o2) {
                return o1.ordinal() - o2.ordinal();
            }
        });
        for (Stats stat : Stats.values()) {
            map.put(stat, 0f);
        }
    }

    public void add(Stats stat, float amount) {
        float newAmount = map.get(stat) + amount;
        if (newAmount < 0) newAmount = 0;
        map.put(stat, newAmount);
    }

    public void inc(Stats stat) {
        add(stat, 1);
    }

    public float get(Stats stat) {
        return map.get(stat);
    }

    public TreeMap<Stats, Float> getFull() {
        return map;
    }

    public enum Stats {
        ASTEROIDS("Asteroids destroyed"),
        BULLETS_SPENT("Bullets spent"),
        DAMAGE_OVERALL("Damage overall"),
        DAMAGE_TAKEN("Damage taken"),
        LIVES_LOST("Lives lost"),
        SCORE("Score");

        private String desc;

        Stats(String desc) {
            this.desc = desc;

        }

        @Override
        public String toString() {
            return desc;
        }
    }
}
