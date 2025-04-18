package ctu.game.isometric.view.renderer;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import ctu.game.isometric.model.entity.Character;
import ctu.game.isometric.util.AssetManager;
import ctu.game.isometric.util.AnimationManager;

public class CharacterRenderer {
    private Character character;
    private AssetManager assetManager;
    private AnimationManager animationManager;
    private MapRenderer mapRenderer;

    public CharacterRenderer(Character character, AssetManager assetManager, MapRenderer mapRenderer) {
        this.character = character;
        this.assetManager = assetManager;
        this.mapRenderer = mapRenderer;
        this.animationManager = assetManager.getAnimationManager();

        // Initialize animations with both sprite sheets
        this.animationManager.loadCharacterAnimations("characters/idle.png", "characters/walk.png");
    }

    public void render(SpriteBatch batch) {
        float gridX = character.getGridX();
        float gridY = character.getGridY();

        float[] screenPos = mapRenderer.toIsometric(gridX, gridY);

        float isoX = screenPos[0];
        float isoY = screenPos[1];

        // Get animation frame with translated direction
        String direction = translateDirection(character.getDirection());

        // Always use walk animation when moving
        TextureRegion currentFrame = animationManager.getCharacterFrame(
                direction,
                character.isMoving(),
                character.getAnimationTime()
        );

        // Center the character sprite
        float offsetPlayerX = -24 + 32;
        float offsetPlayerY = -32 + 24;

        batch.draw(currentFrame, isoX + offsetPlayerX, isoY + offsetPlayerY);
    }

    // Convert simplified direction to sprite sheet direction
    private String translateDirection(String direction) {
        // Map all 8-way directions to our available 6 sprite directions
        switch(direction) {
            case "up":
                return "up";
            case "down":
                return "down";
            case "left":
                return "left_down"; // In isometric, pure left maps to left_down
            case "right":
                return "right_down"; // In isometric, pure right maps to right_down
            case "left_up":
                return "left_up";
            case "right_up":
                return "right_up";
            case "left_down":
                return "left_down";
            default:
                return "right_down";
        }
    }
}