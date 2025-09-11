package br.com.bgrant.sfs.objects;

import br.com.bgrant.sfs.SFSGame;
import br.com.bgrant.sfs.resources.Assets;
import br.com.bgrant.sfs.resources.GlobalVariables;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Fighter {

    //number of frames rows and columns in each animation sprite sheet
    private static final int FRAME_ROWS = 2, FRAME_COLS = 3;
    //how fast a fighter can move
    public static final float MOVEMENT_SPEED = 10F;
    //maximum life a fighter can have;
    public static final float MAX_LIFE = 100f;
    //the amount of damage a fighter's hit will inflict
    public static final float HIT_STRENGTH = 5F;
    //factor decrease damage if a fighter gets hit while blocking.
    public static final float BLOCK_DAMAGE_FACTOR = 0.2f;

    //distinguishing details
    private String name;
    private Color color;


    private State state;
    private float stateTime;
    private State renderState;
    private float renderStateTime;
    private float life;
    private int facing;
    private boolean madeContact;

    //position and movement
    private final Vector2 position = new Vector2();
    private final Vector2 movementDirection = new Vector2();

    //animations
    private Animation<TextureRegion> blockAnimation;
    private Animation<TextureRegion> hurtAnimation;
    private Animation<TextureRegion> idleAnimation;
    private Animation<TextureRegion> kickAnimation;
    private Animation<TextureRegion> loseAnimation;
    private Animation<TextureRegion> punchAnimation;
    private Animation<TextureRegion> walkAnimation;
    private Animation<TextureRegion> winAnimation;

    //state
    public enum State {
        BLOCK, HURT, IDLE, KICK, LOSE, PUNCH, WALK, WIN
    }

    public Fighter(SFSGame game, String name, Color color) {
        this.name = name;
        this.color = color;

        //initializing the animations
        initializeBlockAnimation(game.assets.assetManager);
        initializeHurtAnimation(game.assets.assetManager);
        initializeIdleAnimation(game.assets.assetManager);
        initializeKickAnimation(game.assets.assetManager);
        initializeLoseAnimation(game.assets.assetManager);
        initializePunchAnimation(game.assets.assetManager);
        initializeWalkAnimation(game.assets.assetManager);
        initializeWinAnimation(game.assets.assetManager);
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Vector2 getPosition() {
        return this.position;
    }

    public float getLife(){
        return life;
    }

    public void getReady(float positionX, float positionY) {
        this.state = renderState = State.IDLE;
        this.stateTime = renderStateTime = 0f;
        this.position.set(positionX, positionY);
        this.movementDirection.set(0, 0);
        this.life = MAX_LIFE;
        this.madeContact = false;
//        this.facing = 1;
    }

    public void render(SpriteBatch batch) {
        // get the current animation frame
        TextureRegion currentFrame;
        switch (this.renderState) {
            case BLOCK:
                currentFrame = blockAnimation.getKeyFrame(renderStateTime, true);
                break;
            case HURT:
                currentFrame = hurtAnimation.getKeyFrame(renderStateTime, false);
                break;
            case IDLE:
                currentFrame = idleAnimation.getKeyFrame(renderStateTime, true);
                break;
            case KICK:
                currentFrame = kickAnimation.getKeyFrame(renderStateTime, false);
                break;
            case LOSE:
                currentFrame = loseAnimation.getKeyFrame(renderStateTime, false);
                break;
            case PUNCH:
                currentFrame = punchAnimation.getKeyFrame(renderStateTime, false);
                break;
            case WALK:
                currentFrame = walkAnimation.getKeyFrame(renderStateTime, true);
                break;
            default:
//            case WIN:
                currentFrame = winAnimation.getKeyFrame(renderStateTime, true);
        }
        batch.setColor(color);
        batch.draw(currentFrame, position.x, position.y,
            currentFrame.getRegionWidth() * 0.5f * GlobalVariables.WORLD_SCALE, 0,
            currentFrame.getRegionWidth() * GlobalVariables.WORLD_SCALE,
            currentFrame.getRegionHeight() * GlobalVariables.WORLD_SCALE,
            facing, 1, 0);
        batch.setColor(Color.WHITE);
    }

    public void update(float deltaTime) {
        //increment the state time by deltaTime
        this.stateTime += deltaTime;
        //only update the render state if delta time is greater than zero
        if (deltaTime > 0) {
            this.renderState = this.state;
            this.renderStateTime = this.stateTime;
        }
        if (this.state == State.WALK) {
            // if the fighter is walking, then move in the direction of the movement direction vector.
            this.position.x += this.movementDirection.x * MOVEMENT_SPEED * deltaTime;
            this.position.y += this.movementDirection.y * MOVEMENT_SPEED * deltaTime;
        } else if (
            (state == State.KICK && kickAnimation.isAnimationFinished(stateTime))
                || (punchAnimation.isAnimationFinished(stateTime) && state == State.PUNCH)
                || (state == State.HURT && hurtAnimation.isAnimationFinished(stateTime))) {
            // if animation is finished and the movement direction is set, start walking, otherwise, go to IDLE.
            if (movementDirection.x != 0 || movementDirection.y != 0) {
                changeState(State.WALK);
            } else {
                changeState(State.IDLE);
            }
        }
    }

    public void faceLeft() {
        this.facing = -1;
    }

    public void faceRight() {
        this.facing = 1;
    }

    private void changeState(State newState) {
        this.state = newState;
        this.stateTime = 0f;
    }

    private void setMovement(float x, float y) {
        this.movementDirection.set(x, y);
        if (state == State.WALK && x == 0 && y == 0) {
            this.changeState(State.IDLE);
        } else if (state == State.IDLE && (x != 0 || y != 0)) {
            this.changeState(State.WALK);
        }
    }

    public void moveLeft() {
        setMovement(-1, movementDirection.y);
    }

    public void moveRight() {
        setMovement(1, movementDirection.y);
    }

    public void moveUp() {
        setMovement(movementDirection.x, 1);
    }

    public void moveDown() {
        setMovement(movementDirection.x, -1);
    }

    public void stopMovingLeft() {
        if (movementDirection.x == -1) {
            setMovement(0, movementDirection.y);
        }
    }

    public void stopMovingRight() {
        if (movementDirection.x == 1) {
            setMovement(0, movementDirection.y);
        }
    }

    public void stopMovingUp() {
        if (movementDirection.y == 1) {
            setMovement(movementDirection.x, 0);
        }
    }

    public void stopMovingDown() {
        if (movementDirection.y == -1) {
            setMovement(movementDirection.x, 0);
        }
    }

    public void block() {
        if (state == State.IDLE || state == State.WALK) {
            changeState(State.BLOCK);
        }
    }

    public void stopBlocking() {
        if (state == State.BLOCK) {
            // if the movement direction is set, state equal WALKING, otherwise, go to IDLE.
            if (movementDirection.x != 0 || movementDirection.y != 0) {
                changeState(State.WALK);
            } else {
                changeState(State.IDLE);
            }
        }
    }

    public boolean isBlocking() {
        return state == State.BLOCK;
    }

    public void punch() {
        if (state == State.IDLE || state == State.WALK) {
            changeState(State.PUNCH);
            // just started attacking, so contact hasn't been made yet.
            this.madeContact = false;
        }
    }

    public void kick() {
        if (state == State.IDLE || state == State.WALK) {
            changeState(State.KICK);
            // just started attacking, so contact hasn't been made yet.
            this.madeContact = false;
        }
    }

    public void makeContact() {
        this.madeContact = true;
    }

    public boolean hasMadeContact() {
        return this.madeContact;
    }

    public boolean isAttacking() {
        return state == State.KICK || state == State.PUNCH;
    }

    public boolean isAttackActive() {
        //the attack is only active if the fighter has not yet made contact and the attack animation has not started.
        //or is almost finished.
        if (this.hasMadeContact()) {
            return false;
        } else if (state == State.PUNCH) {
            return stateTime > punchAnimation.getAnimationDuration() * 0.33f && stateTime < punchAnimation.getAnimationDuration() * 0.66f;
        } else if (state == State.KICK) {
            return stateTime > kickAnimation.getAnimationDuration() * 0.33f && stateTime < kickAnimation.getAnimationDuration() * 0.66f;
        } else {
            return false;
        }
    }

    public void getHit(float damage) {
        if (state == State.HURT || state == State.WIN || state == State.LOSE) return;
        // reduce the life by the damage inflicted by the full amount or a fraction of the damage if fighter is blocking.
        life -= state == State.BLOCK ? damage * BLOCK_DAMAGE_FACTOR : damage;
        if (life <= 0) {
            // if no life remains, lose
            this.lose();
        } else if (state != State.BLOCK) {
            this.changeState(State.HURT);
        }
    }

    public void lose() {
        this.changeState(State.LOSE);
        this.life = 0f;
    }

    public boolean hasLost(){
        return this.state == State.LOSE;
    }

    public void win(){
        this.changeState(State.WIN);
    }

    private void initializeWinAnimation(AssetManager assetManager) {
        Texture spriteSheet = assetManager.get(Assets.WIN_SPRITE_SHEET);
        TextureRegion[] frames = getAnimationFrames(spriteSheet);
        this.winAnimation = new Animation<TextureRegion>(0.05f, frames);
    }

    private void initializeWalkAnimation(AssetManager assetManager) {
        Texture spriteSheet = assetManager.get(Assets.WALK_SPRITE_SHEET);
        TextureRegion[] frames = getAnimationFrames(spriteSheet);
        this.walkAnimation = new Animation<TextureRegion>(0.08f, frames);
    }

    private void initializePunchAnimation(AssetManager assetManager) {
        Texture spriteSheet = assetManager.get(Assets.PUNCH_SPRITE_SHEET);
        TextureRegion[] frames = getAnimationFrames(spriteSheet);
        this.punchAnimation = new Animation<TextureRegion>(0.05f, frames);
    }

    private void initializeLoseAnimation(AssetManager assetManager) {
        Texture spriteSheet = assetManager.get(Assets.LOSE_SPRITE_SHEET);
        TextureRegion[] frames = getAnimationFrames(spriteSheet);
        this.loseAnimation = new Animation<TextureRegion>(0.05f, frames);
    }

    private void initializeKickAnimation(AssetManager assetManager) {
        Texture spriteSheet = assetManager.get(Assets.KICK_SPRITE_SHEET);
        TextureRegion[] frames = getAnimationFrames(spriteSheet);
        this.kickAnimation = new Animation<TextureRegion>(0.05f, frames);
    }

    private void initializeIdleAnimation(AssetManager assetManager) {
        Texture spriteSheet = assetManager.get(Assets.IDLE_SPRITE_SHEET);
        TextureRegion[] frames = getAnimationFrames(spriteSheet);
        this.idleAnimation = new Animation<TextureRegion>(0.1f, frames);
    }


    private void initializeHurtAnimation(AssetManager assetManager) {
        Texture spriteSheet = assetManager.get(Assets.HURT_SPRITE_SHEET);
        TextureRegion[] frames = getAnimationFrames(spriteSheet);
        this.hurtAnimation = new Animation<TextureRegion>(0.03f, frames);
    }

    private void initializeBlockAnimation(AssetManager assetManager) {
        Texture spriteSheet = assetManager.get(Assets.BLOCK_SPRITE_SHEET);
        TextureRegion[] frames = getAnimationFrames(spriteSheet);
        this.blockAnimation = new Animation<TextureRegion>(0.05f, frames);
    }

    private TextureRegion[] getAnimationFrames(Texture spriteSheet) {
        TextureRegion[][] tmp = TextureRegion.split(spriteSheet, spriteSheet.getWidth() / FRAME_COLS,
            spriteSheet.getHeight() / FRAME_ROWS);
        TextureRegion[] frames = new TextureRegion[FRAME_ROWS * FRAME_COLS];
        int index = 0;
        for (int i = 0; i < FRAME_ROWS; i++) {
            for (int j = 0; j < FRAME_COLS; j++) {
                frames[index] = tmp[i][j];
                index++;
            }
        }
        return frames;
    }
}
