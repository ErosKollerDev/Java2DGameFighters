package br.com.bgrant.sfs.screens;

import br.com.bgrant.sfs.SFSGame;
import br.com.bgrant.sfs.objects.Fighter;
import br.com.bgrant.sfs.resources.Assets;
import br.com.bgrant.sfs.resources.GlobalVariables;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.*;

public class GameScreen implements Screen, InputProcessor {

    private final SFSGame game;
    //    private final OrthographicCamera camera;
    private final ExtendViewport viewport;

    //game
    private GlobalVariables.Difficult difficulty = GlobalVariables.Difficult.EASY;

    //rounds
    private int roundsWon = 0, roundsLost = 0;
    private static final float MAX_ROUND_TIME = 99.99F;
    private float roundTimer = MAX_ROUND_TIME;

    //fonts
    private BitmapFont smallFont, mediumFont, largeFont;
    //Color
    private static final Color DEFAULT_FONT_COLOR = Color.WHITE;

    //HUD Head Up Display
    private static final Color HEALTH_BAR_COLOR = Color.RED;
    private static final Color HEALTH_BAR_BACKGROUND_COLOR = Color.YELLOW;

    //Background/ring
    private Texture backgroundTexture;
    private Texture frontRopesTexture;

    //boundaries for the fighters
    private static final float RING_MIN_X = 7F;
    private static final float RING_MAX_X = 60F;
    private static final float RING_MIN_Y = 4F;
    private static final float RING_MAX_Y = 22f;
    private static final float RING_SLOPE = 3.16f;

    // fighters initial positions
    private static final float PLAYER_START_POSITION_X = 16f;
    private static final float OPPONENT_START_POSITION_X = 51f;
    private static final float FIGHTER_START_POSITION_Y = 15f;
    private static final float FIGHTER_CONTACT_DISTANCE_X = 7.5f;
    private static final float FIGHTER_CONTACT_DISTANCE_Y = 1.5f;

    public GameScreen(SFSGame game) {
        this.game = game;
        //SetUp camera
//        this.camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//        this.camera = new OrthographicCamera(GlobalVariables.WORLD_WIDTH, GlobalVariables.WORLD_HEIGHT);
//        this.camera = new OrthographicCamera();
//        this.camera.translate(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0f);
//        this.camera.update();

        // ExtendViewport maintains the world aspect ratio without black bars
        // by growing the world in one direction to take up the extra screen space
        // Parameters: (minWorldWidth, minWorldHeight, maxWorldWidth, maxWorldHeight)
        this.viewport = new ExtendViewport(GlobalVariables.WORLD_WIDTH, GlobalVariables.MIN_WORLD_HEIGHT,
            GlobalVariables.WORLD_WIDTH, 0);
//        this.stretchViewport.setCamera(this.camera);

        //create the game area
        this.createGameArea();

        //set up fonts
        setUpFonts();


        // get the fighters ready
        this.game.player.getReady(PLAYER_START_POSITION_X, FIGHTER_START_POSITION_Y);
        this.game.opponent.getReady(OPPONENT_START_POSITION_X, FIGHTER_START_POSITION_Y);
    }

    private void createGameArea() {
        //get the ring texture from the asset manager
        this.backgroundTexture = game.assets.assetManager.get(Assets.BACKGROUND_TEXTURE);
        this.frontRopesTexture = game.assets.assetManager.get(Assets.FRONT_ROPES_TEXTURE);
    }

    private void setUpFonts() {
        this.smallFont = game.assets.assetManager.get(Assets.SMALL_FONT);
        this.smallFont.getData().setScale(GlobalVariables.WORLD_SCALE);
        this.smallFont.setColor(DEFAULT_FONT_COLOR);
        this.smallFont.setUseIntegerPositions(false);

        this.mediumFont = game.assets.assetManager.get(Assets.MEDIUM_FONT);
        this.mediumFont.getData().setScale(GlobalVariables.WORLD_SCALE);
        this.mediumFont.setColor(DEFAULT_FONT_COLOR);
        this.mediumFont.setUseIntegerPositions(false);

        this.largeFont = game.assets.assetManager.get(Assets.LARGE_FONT);
        this.largeFont.getData().setScale(GlobalVariables.WORLD_SCALE);
        this.largeFont.setColor(DEFAULT_FONT_COLOR);
        this.largeFont.setUseIntegerPositions(false);

    }

    @Override
    public void show() {
        // process user input
        //
        Gdx.input.setInputProcessor(this);
//        this.game.opponent.block();
    }

    @Override
    public void render(float delta) {
        //clear the screen
        ScreenUtils.clear(0, 0, 0, 1);

        //update the game logic
        this.update(delta);
//        ScreenUtils.clear(1, 0, 0, 1);
        //Set the sprite batch and the shape renderer viewport's camera
//        this.game.batch.setProjectionMatrix(camera.combined);
        this.game.batch.setProjectionMatrix(viewport.getCamera().combined);
        this.game.shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);

        //begin drawing
        game.batch.begin();

        //draw the background texture
        game.batch.draw(backgroundTexture, 0, 0,
            backgroundTexture.getWidth() * GlobalVariables.WORLD_SCALE,
            backgroundTexture.getHeight() * GlobalVariables.WORLD_SCALE);

        //draw the fighters
        renderFighters();

        //draw the front ropes texture
        game.batch.draw(frontRopesTexture, 0, 0,
            frontRopesTexture.getWidth() * GlobalVariables.WORLD_SCALE,
            frontRopesTexture.getHeight() * GlobalVariables.WORLD_SCALE);

        //draw the HUD
        renderHUD();

        //end drawing
        game.batch.end();
    }

    private void renderFighters() {
        // use the y coordinates to decide which fighter to render first.
        if (this.game.player.getPosition().y > this.game.opponent.getPosition().y) {
            //draw the player
            this.game.player.render(game.batch);
            //draw the opponent
            this.game.opponent.render(game.batch);
        } else {
            //draw the opponent
            this.game.opponent.render(game.batch);
            //draw the player
            this.game.player.render(game.batch);
        }
    }

    private void renderHUD() {
        float hudMargin = 1f;

        //draw the rounds won to lost ration
        smallFont.draw(game.batch, String.format("WINS: %d - LOST: %d", roundsWon, roundsLost),
            hudMargin, viewport.getWorldHeight() - hudMargin);
        //draw the difficulty settings
        String text = " DIFFICULTY: ";
        switch (this.difficulty) {
            case EASY:
                text += "EASY";
                break;
            case MEDIUM:
                text += "MEDIUM";
                break;
            case HARD:
                text += "HARD";
                break;
        }
        smallFont.draw(game.batch, text, viewport.getWorldWidth() - hudMargin, viewport.getWorldHeight() - hudMargin, 0, Align.topRight, false);
        //set up the layout sizes and positioning
        float healthBarPadding = 0.5f;
        float healthBarHeight = smallFont.getCapHeight() + healthBarPadding * 2f;
        float healthBarMaxWidth = 32f;
        float healthBarBackgroundPadding = 0.2f;
        float healthBarBackgroundHeight = healthBarHeight + healthBarBackgroundPadding * 2f;
        float healthBarBackgroundWidth = healthBarMaxWidth + healthBarBackgroundPadding * 2f;
        float healthBarBackgroundMarginTop = 0.8f;
        float healthBarBackgroundPositionY = viewport.getWorldHeight() - hudMargin - smallFont.getCapHeight() - healthBarBackgroundMarginTop - healthBarBackgroundHeight;
        float healthBarPositionY = healthBarBackgroundPositionY + healthBarBackgroundPadding;
        float fighterNamePositionY = healthBarPositionY + healthBarHeight - healthBarPadding;
        game.batch.end();
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        //draw the fighters health bar background rectangles
        game.shapeRenderer.setColor(HEALTH_BAR_BACKGROUND_COLOR);
        game.shapeRenderer.rect(hudMargin, healthBarBackgroundPositionY, healthBarBackgroundWidth, healthBarBackgroundHeight);
        game.shapeRenderer.rect(viewport.getWorldWidth() - hudMargin - healthBarBackgroundWidth, healthBarBackgroundPositionY, healthBarBackgroundWidth, healthBarBackgroundHeight);

        //draw the fighters health bar rectangles
        game.shapeRenderer.setColor(HEALTH_BAR_COLOR);
        float healthBarWidth = healthBarMaxWidth * game.player.getLife() / Fighter.MAX_LIFE - healthBarPadding;
        game.shapeRenderer.rect(hudMargin+healthBarPadding, healthBarPositionY, healthBarWidth, healthBarHeight);
        healthBarWidth =  healthBarMaxWidth * game.opponent.getLife() / Fighter.MAX_LIFE;
        game.shapeRenderer.rect(viewport.getWorldWidth() - hudMargin - healthBarBackgroundPadding - healthBarWidth,
            healthBarPositionY , healthBarWidth , healthBarHeight );
        game.shapeRenderer.end();
        game.batch.begin();
    }

    private void update(float deltaTime) {
        this.game.player.update(deltaTime);
        this.game.opponent.update(deltaTime);

        // make sure they face each other
        if (game.player.getPosition().x <= game.opponent.getPosition().x) {
            game.player.faceRight();
            game.opponent.faceLeft();
        } else {
            game.player.faceLeft();
            game.opponent.faceRight();
        }

        // keep the fighters within the bounds of the ring.
        this.keepWithinRingBounds(game.player.getPosition());
        this.keepWithinRingBounds(game.opponent.getPosition());

        //check if fighters are within contact distance.
        if (areWithinContactDistance(game.player.getPosition(), game.opponent.getPosition())) {
            if (game.player.isAttackActive()) {
                //if the player is attacking, then the opponent should get hit.
                game.opponent.getHit(Fighter.HIT_STRENGTH);
//                System.out.printf("Opponent's life: %f\n", game.opponent.getLife());
                // deactivate player's attack
                game.player.makeContact();

                //check if the opponent has lost
                if (this.game.opponent.hasLost()) {
                    this.game.player.win();
                }
            }
        }
    }

    private void keepWithinRingBounds(Vector2 position) {
        if (position.y < RING_MIN_Y) {
            position.y = RING_MIN_Y;
        } else if (position.y > RING_MAX_Y) {
            position.y = RING_MAX_Y;
        }
        if (position.x < position.y / RING_SLOPE + RING_MIN_X) {
            position.x = position.y / RING_SLOPE + RING_MIN_X;
        } else if (position.x > position.y / -RING_SLOPE + RING_MAX_X) {
            position.x = position.y / -RING_SLOPE + RING_MAX_X;
        }
    }

    private boolean areWithinContactDistance(Vector2 position1, Vector2 position2) {
        //determine if the positions are within the distance in which contact is possible
        float xDistance = Math.abs(position1.x - position2.x);
        float yDistance = Math.abs(position1.y - position2.y);
        return xDistance <= FIGHTER_CONTACT_DISTANCE_X && yDistance <= FIGHTER_CONTACT_DISTANCE_Y;
    }

    @Override
    public void resize(int width, int height) {
        //update the viewport everytime the screen is resized.
        this.viewport.update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    @Override
    public boolean keyDown(int keycode) {
        //check if player has pressed a movement key
//        System.out.println("keyDown called ..........");
        if (keycode == Input.Keys.LEFT || keycode == Input.Keys.A) {
            game.player.moveLeft();
        } else if (keycode == Input.Keys.RIGHT || keycode == Input.Keys.D) {
            game.player.moveRight();
        }

        if (keycode == Input.Keys.UP || keycode == Input.Keys.W) {
            game.player.moveUp();
        } else if (keycode == Input.Keys.DOWN || keycode == Input.Keys.S) {
            game.player.moveDown();
        }

        //check if player has press the BLOCK or ATTACK key
        if (keycode == Input.Keys.B) {
            this.game.player.block();
        } else if (keycode == Input.Keys.F) {
            this.game.player.punch();
        } else if (keycode == Input.Keys.V) {
            this.game.player.kick();
        }


        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        // if a player has released a movement key, stop moving.
        if (keycode == Input.Keys.LEFT || keycode == Input.Keys.A) {
            game.player.stopMovingLeft();
        } else if (keycode == Input.Keys.RIGHT || keycode == Input.Keys.D) {
            game.player.stopMovingRight();
        }

        if (keycode == Input.Keys.UP || keycode == Input.Keys.W) {
            game.player.stopMovingUp();
        } else if (keycode == Input.Keys.DOWN || keycode == Input.Keys.S) {
            game.player.stopMovingDown();
        }
        // if the player has released the block key, stop blocking.
        if (keycode == Input.Keys.B) {
            game.player.stopBlocking();
        }
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
