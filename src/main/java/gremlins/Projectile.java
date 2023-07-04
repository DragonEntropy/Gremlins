package gremlins;

import processing.core.PImage;

/**
 * The class representing the projectiles fired by players or enemies
 */
public class Projectile extends MovingObject {
    private ProjectileType type;
    private Direction vector;
    private int collisions;

    /**
     * The constructor for the Projectile class
     * @param xPos The pixel x-coordinate of the projectile
     * @param yPos The pixel y-coordinate of the projectile
     * @param spriteSet The array of sprites for the projectile
     * @param type The type of the projectile
     * @param vector The direction the projectile is fired in
     */
    public Projectile(int xPos, int yPos, PImage[] spriteSet, ProjectileType type, Direction vector) {
        super(xPos, yPos, spriteSet, type.speed, type.etherealness, type.collisionType);
        this.type = type;
        this.vector = vector;
        this.collisions = type.collisions;
    }

    /**
     * Gets the projectile's current vector
     * @return The projectile's vector
     */
    public Direction getVector() {
        return vector;
    }

    /**
     * Unused, but must be defined
     */
    @Override
    public boolean tick() {
        return true;
    }

    /**
     * Updates the projectile every frame
     * @param tileMap The tile map for the level where the projectile exists
     * @return true if the projectile is still active (false otherwise)
     */
    public boolean tick(Tile[][] tileMap) {
        // First checks if the projectile is currently moving
        if (incrementMove()) {
            return true;
        }

        // Then checks if the projectile does not hit a tile
        else if (startMove(vector, tileMap, false)) {
            return true;
        }
        
        // Then checks if the projectile should be destroyed
        return manageTileCollision(tileMap);
    }

    /**
     * Determines what happens when a projectile hits a tile
     * @param tileMap The tile map for the level where the projectile exists
     * @return true if the projectile should remain active (otherwise returns false)
     */
    public boolean manageTileCollision(Tile[][] tileMap) {
        int tileCol = pixelToGrid(xPos) + vector.getX();
        int tileRow = pixelToGrid(yPos) + vector.getY();
        Tile collidingTile = tileMap[tileRow][tileCol];

        boolean isDestruction = false;

        // Checks if the projectile breaks the tile
        if (type.penetration >= collidingTile.getStrength() && collidingTile.getStrength() > 0) {
            collidingTile.destroy();
            isDestruction = true;
        }
        
        // Check if the projectile still has remaining collisions
        if (collisions > 0) {
            collisions--;

            // If the projectile can bounce, it flips direction
            if (type.bounce) {
                vector = vector.flip();
            }

            // If the projectile bounces or destroys the brick, it tries to move again
            if (isDestruction || type.bounce) {
                if (type.bounce) {
                    return tick(tileMap);
                }
                startMove(vector, tileMap, true);
                return true;
            }
        }

        return false;
    }
}
