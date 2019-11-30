package com.star.app.game.pilots;

import java.util.Properties;

public class KeyControls {
    int forward;
    int backward;
    int left;
    int right;
    int fire;

    public KeyControls(Properties properties, String prefix) {
        forward = Integer.parseInt(properties.getProperty(prefix + "_FORWARD"));
        backward = Integer.parseInt(properties.getProperty(prefix + "_BACKWARD"));
        left = Integer.parseInt(properties.getProperty(prefix + "_LEFT"));
        right = Integer.parseInt(properties.getProperty(prefix + "_RIGHT"));
        fire = Integer.parseInt(properties.getProperty(prefix + "_FIRE"));
    }
}
