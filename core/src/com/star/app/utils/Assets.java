package com.star.app.utils;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.star.app.screen.ScreenManager;

public class Assets {
    private static final Assets instance;

    static {
        instance = new Assets();
    }

    public static Assets getInstance() {
        return instance;
    }

    private final String GAME_PACK_PATH = "images/game.pack";

    private AssetManager assetManager;
    private TextureAtlas textureAtlas;

    private Assets() {
        assetManager = new AssetManager();
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    public TextureAtlas getTextureAtlas() {
        return textureAtlas;
    }

    public void loadAssets(ScreenManager.ScreenType type) {
        switch (type) {
            case GAME:
                assetManager.load(GAME_PACK_PATH, TextureAtlas.class);
                createStandardFont("fonts/good times rg.ttf", 22, "font");
                createStandardFont("fonts/good times rg.ttf", 64, "font");
                createStandardFont("fonts/fragile bombers.ttf", 12, "debug");
                assetManager.finishLoading();
                textureAtlas = assetManager.get(GAME_PACK_PATH, TextureAtlas.class);
                break;
        }
    }

    private void createStandardFont(String filename, int size, String prefix) {
        FileHandleResolver resolver = new InternalFileHandleResolver();
        assetManager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        assetManager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));
        FreetypeFontLoader.FreeTypeFontLoaderParameter parameter = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        parameter.fontFileName = filename;
        parameter.fontParameters.size = size;
        parameter.fontParameters.color = Color.WHITE;
        parameter.fontParameters.shadowOffsetX = 1;
        parameter.fontParameters.shadowOffsetY = 1;
        parameter.fontParameters.shadowColor = Color.DARK_GRAY;
        assetManager.load("fonts/" + prefix + size + ".ttf", BitmapFont.class, parameter);
    }

    public void clear() {
        assetManager.clear();
    }
}