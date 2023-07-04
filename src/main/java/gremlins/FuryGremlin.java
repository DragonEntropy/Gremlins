package gremlins;

import processing.core.PImage;

import java.util.Random;

/**
 * The advanced enemies that shoot projectiles that break through walls and use a simple algorithm to chase the player
 */
public class FuryGremlin extends Enemy {
    private static int speed = 2;
    private static int etherealness = 0;

    /**
     * The constructor for the FuryGremlin class
     * @param xPos The pixel x-coordinate of the enemy
     * @param yPos The pixel y-coordinate of the enemy
     * @param spriteSet The array of sprites for the enemy
     * @param weapon The weapon of the enemy
     * @param rand The random number generator for the enemy
     * @param tileMap The tile map for the level where the enemy is placed
     */
    public FuryGremlin(int xPos, int yPos, PImage[] spriteSet, Weapon weapon, Random rand, Tile[][] tileMap) {
        super(xPos, yPos, spriteSet, speed, etherealness, weapon, rand, tileMap);
    }

    /**
     * Moves the enemy. Overriden to track down the player
     * @param level The level the enemy exists in
     * @return true if the enemy can move (otherwise returns false)
     */
    @Override
    public boolean handleMovement(Level level) {
        Tile[][] tileMap = level.getMap();
        Player player = level.getPlayer();

        if (vector == null) {
            return false;
        }

        int xSep = GameObject.pixelToGrid(player.getX() - xPos);
        int ySep = GameObject.pixelToGrid(player.getY() - yPos);

        // Finds the direction that brings the enemy closest to the player
        Direction vector1 = Direction.fromVector(xSep, ySep);
        Direction vector2;

        // Finds the second best direction
        if (Math.abs(ySep) > Math.abs(xSep)) {
            vector2 = Direction.fromVector(xSep, 0);
        }
        else {
            vector2 = Direction.fromVector(0, ySep);
        }
        if (vector2 == null) {
            if (rand.nextBoolean()) {
                vector2 = vector1.clockwise();
            }
            else {
                vector2 = vector1.anticlockwise();
            }
        }

        // Checks if each direction (from best to worst) is valid
        if (validateMove(vector1, tileMap)) {
            startMove(vector1);
        }
        else if (validateMove(vector2, tileMap)) {
            startMove(vector2);
        }
        else if (validateMove(vector2.flip(), tileMap)) {
            startMove(vector2.flip());
        }
        else {
            startMove(vector1.flip());
        }

        return true;
    }

    /**
     * Fires the enemy's weapon. Overriden to shoot in the player's direction
     * @param level The level the enemy exists in
     */
    @Override
    public void useWeapon(Level level) {
        Player player = level.getPlayer();

        // Finds the direction of the player from the enemy
        int xSep = GameObject.pixelToGrid(player.getX() - xPos);
        int ySep = GameObject.pixelToGrid(player.getY() - yPos);
        Direction weaponVector = Direction.fromVector(xSep, ySep);

        weapon.fire(xPos, yPos, weaponVector, level.getProjectiles());
    }
}

