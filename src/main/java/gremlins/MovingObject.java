package gremlins;

import processing.core.PImage;

/**
 * The abstract class representing all objects that require movement
 */
public abstract class MovingObject extends GameObject {
    protected int speed;
    protected int moveRemaining = 0;
    protected Direction vector = null;
    protected int etherealness;
    protected boolean passThroughBroken;
    protected CollisionType collisionType;

    /**
     * The constructor for the MovingObject class
     * @param xPos The pixel x-coordinate of the object
     * @param yPos The pixel y-coordinate of the object
     * @param spriteSet The array of sprites for the object
     * @param speed The speed of the object in pixels per frame
     * @param etherealness The highest level of wall the object can pass through
     * @param collisionType The type that defines how the object handles collisions with other objects
     */
    public MovingObject(int xPos, int yPos, PImage[] spriteSet, int speed, int etherealness, CollisionType collisionType) {
        super(xPos, yPos, spriteSet);
        this.speed = speed;
        this.etherealness = etherealness;
        this.collisionType = collisionType;
    }

    /**
     * Checks if the object can move in the provided direction
     * @param vector The direction the object attempts to move in
     * @param tileMap The tile map for the level where the object exists
     * @return true if the move can be done (otherwise returns false)
     */
    public boolean validateMove(Direction vector, Tile[][] tileMap) {
        if (vector == null) {
            return false;
        }

        int tempCol = pixelToGrid(xPos) + vector.getX(); 
        int tempRow = pixelToGrid(yPos) + vector.getY();

        // Checks if the attempted movement stays inside the map
        if (tempCol >= App.COLS || tempCol < 0 || tempRow >= App.ROWS || tempRow < 0) {
            return false;
        }

        // Checks if the movement occurs into a valid tile
        Tile tile = tileMap[tempRow][tempCol];
        if (tile != null) {
            if (tile.getStrength() == 0 || etherealness >= 0 && tile.getStrength() > etherealness) {
                return false;
            }
        }

        return true;
    }

    /**
     * Commences a move in the provided direction (can check for validity first)
     * @param newVector The direction the object attempts to move in
     * @param tileMap The tile map for the level where the object exists
     * @param isForced If true, move validation is ignored
     * @return true if the move occurs (otherwise returns false)
     */
    public boolean startMove(Direction newVector, Tile[][] tileMap, boolean isForced) {
        // If the object is currently performing a move, it will not perform another one
        if (moveRemaining <= 0) {

            // Regardless if the move is valid, the direction changes (to change player sprite)
            vector = newVector;
            if (validateMove(vector, tileMap) || isForced) {
                moveRemaining = App.SPRITESIZE;
                return true;
            }
        }
        
        return false;
    }

    /**
     * Increments the current move
     * @return true if the move is still incomplete (otherwise returns false)
     */
    public boolean incrementMove() {
        if (moveRemaining > 0) {
            xPos += speed * vector.getX();
            yPos += speed * vector.getY();
            moveRemaining -= speed;

            return true;
        }

        return false;
    }

    /**
     * Updates the object direction vector
     * @param newVector The new direction for the object
     */
    public void startMove(Direction newVector) {
        vector = newVector;
        moveRemaining = App.SPRITESIZE;
    }

    /**
     * Ends the current move forcefully
     */
    public void resetMove() {
        moveRemaining = 0;
    }

    /**
     * Gets the object's collision type
     * @return The collision type of the object
     */
    public CollisionType getCollisionType() {
        return collisionType;
    }

    /**
     * Checks if the object collides with another object
     * @param other The other object being checked for collision
     * @return true if the objects are colliding (otherwise returns false)
     */
    public boolean checkCollision(MovingObject other) {
        // Checks for collision by checking if sprites overlap
        if (Math.abs(xPos - other.getX()) < App.SPRITESIZE && Math.abs(yPos - other.getY()) < App.SPRITESIZE) {
            return true;
        }

        return false;
    }
}
