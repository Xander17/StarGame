package com.star.app.game.pilots;

import java.util.Properties;

public class KeyControls {
    int forward, reverse, left, right, fire, mine;

    public KeyControls(Properties properties, String prefix) {
        forward = Integer.parseInt(properties.getProperty(prefix + "_FORWARD"));
        reverse = Integer.parseInt(properties.getProperty(prefix + "_REVERSE"));
        left = Integer.parseInt(properties.getProperty(prefix + "_LEFT"));
        right = Integer.parseInt(properties.getProperty(prefix + "_RIGHT"));
        fire = Integer.parseInt(properties.getProperty(prefix + "_FIRE"));
        mine = Integer.parseInt(properties.getProperty(prefix + "_MINE"));
    }
}
