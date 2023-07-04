package gremlins;

import processing.core.PApplet;
import processing.core.PImage;

/**
 * The abstract class representing all objects in the game that have coordinates and sprites
 */
public abstract class GameObject {
    protected int xPos;
    protected int yPos;

    private PImage sprite;
    private PImage[] spriteSet;
    private static int spriteSize = App.SPRITESIZE;

    /**
     * The constructor for the GameObject class
     * @param xPos The pixel x-coordinate of the object
     * @param yPos The pixel y-coordinate of the object
     * @param spriteSet The array of sprites for the object
     */
    public GameObject(int xPos, int yPos, PImage[] spriteSet) {
        this.xPos = xPos;
        this.yPos = yPos;
        sprite = spriteSet[0];
        this.spriteSet = spriteSet;
    }

    /**
     * Gets the x-coordinate of the object in pixels
     * @return The pixel x-coordinate of the object
     */
    public int getX() {
        return xPos;
    }

    /**
     * Gets the y-coordinate of the object in pixels
     * @return The pixel y-coordinate of the object
     */
    public int getY() {
        return yPos;
    }

    /**
     * Changes the sprite of the object
     * @param spriteId The id of the new sprite in the sprite set
     */
    public void updateSprite(int spriteId) {
        sprite = spriteSet[spriteId];
    }

    /**
     * Sets the cooridinates of the object in pixels
     * @param x The pixel x-coordinate of the object
     * @param y The pixel y-coordinate of the object
     */
    public void setCoords(int x, int y) {
        xPos = x;
        yPos = y;
    }

    /**
     * Converts from pixel units to grid units
     * @param pixel The measurement in pixels
     * @return The measurement in grid units
     */
    public static int pixelToGrid(int pixel) {
        return pixel / spriteSize;
    }

    /**
     * Converts from grid units to pixel units
     * @param gridPlace The measurement in grid units
     * @return The measurement in pixels
     */
    public static int gridToPixel(int gridPlace) {
        return gridPlace * spriteSize;
    }

    /**
     * Updates the object every frame
     * @return true or false (determined by the child class)
     */
    public abstract boolean tick();

    /**
     * Draws the object into the app
     * @param app The application the object exists in
     */
    public void draw(PApplet app) {
        if (sprite != null) {
            app.image(sprite, xPos, yPos);
        }
    }
}
