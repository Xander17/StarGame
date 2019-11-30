package com.star.app.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import java.io.IOException;
import java.util.Properties;

public class Options {
    public static Properties loadProperties() {
        try {
            Properties properties = new Properties();
            properties.load(Gdx.files.local("options.properties").read());
            return properties;
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Unable to read options.properties");
    }

    public static boolean isOptionsExists() {
        return Gdx.files.local("options.properties").exists();
    }

    public static void createDefaultProperties() {
        try {
            Properties properties = new Properties();
            properties.put("PLAYER1_FORWARD", String.valueOf(Input.Keys.UP));
            properties.put("PLAYER1_LEFT", String.valueOf(Input.Keys.LEFT));
            properties.put("PLAYER1_RIGHT", String.valueOf(Input.Keys.RIGHT));
            properties.put("PLAYER1_BACKWARD", String.valueOf(Input.Keys.DOWN));
            properties.put("PLAYER1_FIRE", String.valueOf(Input.Keys.Z));
            properties.store(Gdx.files.local("options.properties").write(false), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

