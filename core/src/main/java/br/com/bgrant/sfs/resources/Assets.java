package br.com.bgrant.sfs.resources;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.BitmapFontLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;

public class Assets {
    //asset manager
    public final AssetManager assetManager = new AssetManager();

    //Game play Assets
    public static final String BACKGROUND_TEXTURE = "textures/Background.png";
    public static final String FRONT_ROPES_TEXTURE = "textures/FrontRopes.png";
    public static final String IDLE_SPRITE_SHEET = "sprites/IdleSpriteSheet.png";
    public static final String WALK_SPRITE_SHEET = "sprites/WalkSpriteSheet.png";
    public static final String PUNCH_SPRITE_SHEET = "sprites/PunchSpriteSheet.png";
    public static final String KICK_SPRITE_SHEET = "sprites/KickSpriteSheet.png";
    public static final String HURT_SPRITE_SHEET = "sprites/HurtSpriteSheet.png";
    public static final String BLOCK_SPRITE_SHEET = "sprites/BlockSpriteSheet.png";
    public static final String WIN_SPRITE_SHEET = "sprites/WinSpriteSheet.png";
    public static final String LOSE_SPRITE_SHEET = "sprites/LoseSpriteSheet.png";
    public static final String GAMEPLAY_BUTTONS_ATLAS = "textures/GameplayButtons.atlas";
    public static final String BLOOD_ATLAS = "textures/Blood.atlas";

    // fonts
    public static final String ROBOTO_REGULAR = "fonts/Roboto-Regular.ttf";
    public static final String SMALL_FONT = "smallFont.ttf";
    public static final String MEDIUM_FONT = "mediumFont.ttf";
    public static final String LARGE_FONT = "largeFont.ttf";

    // audio assets
    public static final String BLOCK_SOUND = "audio/block.mp3";
    public static final String BOO_SOUND = "audio/boo.mp3";
    public static final String CHEER_SOUND = "audio/cheer.mp3";
    public static final String CLICK_SOUND = "audio/click.mp3";
    public static final String HIT_SOUND = "audio/hit.mp3";
    public static final String MUSIC = "audio/music.ogg";

    // menu assets
    public static final String MENU_ITEMS_ATLAS = "textures/MenuItems.atlas";

    public void load() {
        //load all assets
        this.loadGamePlayAssets();
    }

    private void loadGamePlayAssets() {
        this.assetManager.load(BACKGROUND_TEXTURE, Texture.class);
        this.assetManager.load(FRONT_ROPES_TEXTURE, Texture.class);
        this.assetManager.load(IDLE_SPRITE_SHEET, Texture.class);
        this.assetManager.load(WALK_SPRITE_SHEET, Texture.class);
        this.assetManager.load(PUNCH_SPRITE_SHEET, Texture.class);
        this.assetManager.load(KICK_SPRITE_SHEET, Texture.class);
        this.assetManager.load(HURT_SPRITE_SHEET, Texture.class);
        this.assetManager.load(BLOCK_SPRITE_SHEET, Texture.class);
        this.assetManager.load(WIN_SPRITE_SHEET, Texture.class);
        this.assetManager.load(LOSE_SPRITE_SHEET, Texture.class);
        //Atlas
        this.assetManager.load(GAMEPLAY_BUTTONS_ATLAS, TextureAtlas.class);
        this.assetManager.load(BLOOD_ATLAS, TextureAtlas.class);
        //load fonts
        this.loadFonts();
    }

    private void loadFonts() {
        FileHandleResolver resolver = new InternalFileHandleResolver();//this.assetManager.getFileHandleResolver();
        assetManager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        assetManager.setLoader(BitmapFont.class,".ttf", new FreetypeFontLoader(resolver)  );

        //Load Small fonts first
        FreetypeFontLoader.FreeTypeFontLoaderParameter smallFont = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        smallFont.fontFileName = ROBOTO_REGULAR;
        smallFont.fontParameters.size = 32;
        assetManager.load(SMALL_FONT,BitmapFont.class, smallFont );

        //Load Medium fonts next
        FreetypeFontLoader.FreeTypeFontLoaderParameter mediumFont = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        mediumFont.fontFileName = ROBOTO_REGULAR;
        mediumFont.fontParameters.size = 106;
        mediumFont.fontParameters.borderWidth = 4;
        mediumFont.fontParameters.borderColor = new com.badlogic.gdx.graphics.Color(Color.BLACK);
        assetManager.load(MEDIUM_FONT,BitmapFont.class, mediumFont);

        //Load Large fonts next
        FreetypeFontLoader.FreeTypeFontLoaderParameter largeFont = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        largeFont.fontFileName = ROBOTO_REGULAR;
        largeFont.fontParameters.size = 150;
        largeFont.fontParameters.borderWidth = 6;
        largeFont.fontParameters.borderColor = new com.badlogic.gdx.graphics.Color(Color.BLACK);
        assetManager.load(LARGE_FONT,BitmapFont.class, largeFont);
    }

    public void dispose() {
        this.assetManager.dispose();
    }
}
