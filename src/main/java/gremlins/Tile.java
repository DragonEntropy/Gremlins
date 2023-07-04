package gremlins;

import processing.core.PImage;

/**
 * The pieces that make up the level's map, including walls, bricks, exits and powerups
 */
public class Tile extends GameObject {
    private int strength;
    private int spriteTimer = 0;
    private int totalDestroyStages;
    private TileType tileType;

    private boolean isDestroyed = false;

    /**
     * The constructor for the Tile class
     * @param xPos The pixel x-coordinate of the tile
     * @param yPos The pixel y-coordinate of the tile
     * @param spriteSet The array of sprites for the tile
     * @param tileType The type of tile
     */
    Tile(int xPos, int yPos, PImage[] spriteSet, TileType tileType) {
        super(xPos, yPos, spriteSet);
        this.strength = tileType.strength;
        this.tileType = tileType;

        // Calculates after how many cycles the tile breaks (only applicable for bricks)
        totalDestroyStages = spriteSet.length - 1;
    }

    /**
     * Gets the tile's strength
     * @return The strength of the tile
     */
    public int getStrength() {
        return strength;
    }

    /**
     * Marks the tile as destroyed
     */
    public void destroy() {
        isDestroyed = true;
    }

    /**
     * Gets the tile's type
     * @return The type of the tile
     */
    public TileType getTileType() {
        return tileType;
    }

    /**
     * Determines what happens when an object makes contact with the tile
     * @param level The level which contains the tile
     * @return false always for most tiles
     */
    public boolean contact(Level level) {
        return false;
    }

    /**
     * Updates the tile every frame
     * @return false if the tile's destruction is complete (otherwise returns true)
     */
    @Override
    public boolean tick() {
        // If the tile is destroyed, it resumes the destruction sequence
        if (isDestroyed) {

            // Checks if the tile's destruction is complete
            if (spriteTimer == App.DESTORYFRAMES * totalDestroyStages) {
                return false;
            }

            // Checks if the tile's sprite should progress to the next destruction phase
            if (spriteTimer % App.DESTORYFRAMES == 0) {
                updateSprite(spriteTimer / App.DESTORYFRAMES + 1);
            }
            
            spriteTimer++;
        }
        
        return true;
    }
}