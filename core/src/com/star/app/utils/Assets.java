package com.star.app.utils;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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

    private final String GAME_PACK_PATH = "images/game.atlas";
    private final String MENU_PACK_PATH = "images/menu.atlas";
    private final String GAMEOVER_PACK_PATH = "images/gameover.atlas";
    private final String DEFAULT_FONT = "fonts/good times rg.ttf";


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
                createFont(DEFAULT_FONT, 14, "font");
                createFont(DEFAULT_FONT, 22, "font");
                createFont(DEFAULT_FONT, 32, "font");
                createFont(DEFAULT_FONT, 64, "font");
                break;
            case MENU:
                assetManager.load(MENU_PACK_PATH, TextureAtlas.class);
                createFont(DEFAULT_FONT, 24, "font");
                createFont(DEFAULT_FONT, 18, "font");
                createFont(DEFAULT_FONT, 64, "font");
                break;
            case GAMEOVER:
                assetManager.load(GAMEOVER_PACK_PATH, TextureAtlas.class);
                createFont(DEFAULT_FONT, 24, "font");
                createFont(DEFAULT_FONT, 16, "font");
                createFont(DEFAULT_FONT, 64, "font");
                break;
        }
    }

    private void createFont(String filename, int size, String prefix) {
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

    public BitmapFont getFont(String filename, int size) {
        createFont(filename, size, "tmp");
        assetManager.finishLoading();
        return assetManager.get("fonts/tmp" + size + ".ttf");
    }

    public BitmapFont getInstanceFont(String fontPath, int size) {
        BitmapFont tmpFont = getFont(fontPath, size);
        return new BitmapFont(tmpFont.getData(), new TextureRegion(new Texture(tmpFont.getRegion().getTexture().getTextureData())), true);
    }

    public void makeLinks() {
        switch (ScreenManager.getInstance().getTargetScreenType()) {
            case GAME:
                textureAtlas = assetManager.get(GAME_PACK_PATH, TextureAtlas.class);
                break;
            case MENU:
                textureAtlas = assetManager.get(MENU_PACK_PATH, TextureAtlas.class);
                break;
            case GAMEOVER:
                textureAtlas = assetManager.get(GAMEOVER_PACK_PATH, TextureAtlas.class);
                break;
        }
    }

    public void clear() {
        assetManager.clear();
    }
}
