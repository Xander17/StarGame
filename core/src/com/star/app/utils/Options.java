package com.star.app.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class Options {
    private static final String OPTIONS_FILE_PATH = "options.properties";

    public static Properties loadProperties() {
        Properties properties = new Properties();
        try {
            if (!Gdx.files.local(OPTIONS_FILE_PATH).exists()) throw new FileNotFoundException();
            properties.load(Gdx.files.local(OPTIONS_FILE_PATH).read());
            if (!checkConsistent(properties)) throw new FileNotFoundException();
        } catch (FileNotFoundException e) {
            properties = createDefaultProperties();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error during loading " + OPTIONS_FILE_PATH);
        }
        return properties;
    }

    private static boolean checkConsistent(Properties properties) {
        DefaultOptions[] defaultOptions = DefaultOptions.values();
        for (int i = 0; i < defaultOptions.length; i++) {
            if (properties.get(defaultOptions[i].toString()) == null) return false;
        }
        return true;
    }

    private static Properties createDefaultProperties() {
        try {
            Properties properties = new Properties();
            DefaultOptions[] defaultOptions = DefaultOptions.values();
            for (int i = 0; i < defaultOptions.length; i++) {
                properties.put(defaultOptions[i].toString(), String.valueOf(defaultOptions[i].defaultKey));
            }
            properties.store(Gdx.files.local(OPTIONS_FILE_PATH).write(false), null);
            return properties;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error during creating " + OPTIONS_FILE_PATH);
        }
    }

    public static void saveProperties(Properties properties) {
        try {
            properties.store(Gdx.files.local(OPTIONS_FILE_PATH).write(false), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public enum DefaultOptions {
        PLAYER1_FORWARD(Input.Keys.UP),
        PLAYER1_REVERSE(Input.Keys.DOWN),
        PLAYER1_LEFT(Input.Keys.LEFT),
        PLAYER1_RIGHT(Input.Keys.RIGHT),
        PLAYER1_FIRE(Input.Keys.Z),
        PLAYER1_MINE(Input.Keys.X);

        int defaultKey;

        DefaultOptions(int defaultKey) {
            this.defaultKey = defaultKey;
        }
    }
}

