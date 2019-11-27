package com.star.app.game.overlays;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.StringBuilder;
import com.star.app.utils.Assets;

import java.util.Map;
import java.util.TreeMap;


public class DebugOverlay {
    static BitmapFont font;
    static StringBuilder stringBuilder;
    static Map<String, String> debugParams;

    static {
        font = Assets.getInstance().getAssetManager().get("fonts/debug12.ttf", BitmapFont.class);
        debugParams = new TreeMap<>();
        stringBuilder = new StringBuilder();
    }

    public static void setParam(String name, String value) {
        debugParams.put(name, value);
    }

    public static void setParam(String name, float value) {
        debugParams.put(name, String.valueOf(value));
    }

    public static void render(SpriteBatch batch) {
        int size = debugParams.size();
        if (size == 0) return;
        int count = 0;
        for (Map.Entry<String, String> entry : debugParams.entrySet()) {
            stringBuilder.clear();
            String k = entry.getKey();
            String v = entry.getValue();
            font.draw(batch, stringBuilder.append(k).append(": ").append(v).toString(), 5, (size - count) * 12);
            count++;
        }
    }

}
