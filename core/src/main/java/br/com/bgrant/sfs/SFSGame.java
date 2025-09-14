package br.com.bgrant.sfs;

import br.com.bgrant.sfs.objects.Fighter;
import br.com.bgrant.sfs.resources.Assets;
import br.com.bgrant.sfs.screens.GameScreen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms.
 * This game class manages the main game loop and rendering pipeline using LibGDX framework.
 * SpriteBatch is a crucial LibGDX class that optimizes 2D sprite rendering by batching
 * multiple draw calls together, reducing GPU overhead. It manages the efficient drawing
 * of textures, sprites, and other 2D graphics elements to the screen.
 */
public class SFSGame extends Game {

    /**
     * SpriteBatch handles efficient rendering of 2D sprites and textures.
     * It batches multiple sprite draw calls together to minimize GPU state changes
     * and optimize rendering performance. Must be disposed when no longer needed.
     */
    /**
     * In computer graphics, a sprite is a two-dimensional (2D) bitmap that represents a graphical object.
     * SpriteBatch is a LibGDX utility class that efficiently renders 2D sprites by batching multiple draw calls together.
     * It minimizes GPU state changes and texture bindings, significantly improving rendering performance.
     * The batch must be properly disposed when no longer needed to prevent memory leaks.
     */
    public SpriteBatch batch;
    public ShapeRenderer shapeRenderer;
    public Assets assets;
    //    private Texture image;
    //Screens
    public GameScreen gameScreen;

    //fighters
    public Fighter player, opponent;


    @Override
    public void create() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
//        image = new Texture("textures/Background.png");
        this.assets = new Assets();
        this.assets.load();
        this.assets.assetManager.finishLoading();
        //initializing fighters
        this.player = new Fighter(this,"Slim Stallone", Color.SCARLET);
        this.opponent = new Fighter(this,"Thin Schwarzenegger", Color.ROYAL);
        //initialize the game screen
        this.gameScreen = new GameScreen(this);
        this.setScreen(this.gameScreen);
    }

    @Override
    public void render() {
//        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
//        batch.begin();
////        batch.draw(image, 140, 210);
////        batch.draw(image, Gdx.graphics.getWidth() / 2f - image.getWidth() / 2f, Gdx.graphics.getHeight() / 2f - image.getHeight() / 2f);
//        batch.draw(image, Gdx.graphics.getWidth() / 2f - image.getWidth() / 2f, Gdx.graphics.getHeight() / 2f - image.getHeight() / 2f);
//        batch.end();
        super.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
//        image.dispose();
        this.assets.dispose();
    }
}
