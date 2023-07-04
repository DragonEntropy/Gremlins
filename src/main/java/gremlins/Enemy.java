package gremlins;

import processing.core.PImage;

import java.util.Random;

/**
 * The main class for enemies. Enemies kill players with their bodies and projectiles
 */
public class Enemy extends MovingObject {
    protected Weapon weapon;
    protected Random rand;

    /**
     * The constructor for the Enemy class
     * @param xPos The pixel x-coordinate of the enemy
     * @param yPos The pixel y-coordinate of the enemy
     * @param spriteSet The array of sprites for the enemy
     * @param speed The speed of the enemy in pixels per frame
     * @param etherealness The highest level of wall the enemy can pass through
     * @param weapon The weapon of the enemy
     * @param rand The random number generator for the enemy
     * @param tileMap The tile map for the level where the enemy is placed
     */
    public Enemy(int xPos, int yPos, PImage[] spriteSet, int speed, int etherealness, Weapon weapon, Random rand, Tile[][] tileMap) {
        super(xPos, yPos, spriteSet, speed, etherealness, CollisionType.ENEMY);
        this.weapon = weapon;
        this.rand = rand;

        chooseStartingDirection(tileMap);
    }

    /**
     * Chooses the starting direction for the enemy
     * @param tileMap The tile map for the level where the enemy exists
     * @return true if the enemy can be assigned a direction (otherwise returns false)
     */
    public boolean chooseStartingDirection(Tile[][] tileMap) {
        // First picks an ordinal corresponding to a possible direction
        int direction = rand.nextInt(4);

        // Cycles clockwise until a valid starting direction is found
        for (int i = 0; i < 4; i++) {
            Direction testVector = Direction.values()[(direction + i) % 4];
            if (validateMove(testVector, tileMap)) {
                vector = testVector;
                return true;
            }
        }

        // If no starting direction exists, the enemy doesn't move
        vector = null;
        return false;
    }

    /**
     * Moves the enemy
     * @param level The level the enemy exists in
     * @return true if the enemy finishes moving from one tile to another (otherwise returns false)
     */
    public boolean handleMovement(Level level) {
        Tile[][] tileMap = level.getMap();

        // If the enemy has no direction, it doesn't move
        if (vector == null) {
            return false;
        }

        // If the enemy has moved to the centre of a tile, it starts moving again
        if (moveRemaining <= 0) {

            // If possible, the enemy moves in the same direction
            if (validateMove(vector, tileMap)) {
                startMove(vector);
            }

            // Otherwise, the enemy picks a new direction
            else {
                boolean canTurnLeft = validateMove(vector.anticlockwise(), tileMap);
                boolean canTurnRight = validateMove(vector.clockwise(), tileMap);

                // If the enemy can't move left or right, it turns around
                if (!(canTurnLeft || canTurnRight)) {
                    startMove(vector.flip());
                }

                // If the enemy can move left or right, it randomly chooses a direction
                else if (canTurnLeft && canTurnRight) {
                    if (rand.nextBoolean()) {
                        startMove(vector.anticlockwise());
                    }
                    else {
                        startMove(vector.clockwise());
                    }
                }

                // Otherwise the enemy moves in the direction available
                else if (canTurnLeft) {
                    startMove(vector.anticlockwise());
                }
                else {
                    startMove(vector.clockwise());
                }
            }

            return true;
        }

        return false;
    }

    /**
     * Fires the enemy's weapon
     * @param level The level the enemy exists in
     */
    public void useWeapon(Level level) {
        weapon.fire(xPos, yPos, vector, level.getProjectiles());
    }

    /**
     * Teleports the enemy after being killed
     * @param player The player
     * @param tileMap The tileMap of the level the enemy exists in
     */
    public void teleport(Player player, Tile[][] tileMap) {
        // Keeps trying to find a valid tile to teleport until one is found
        boolean success = false;
        while (success == false) {    
            int testCol = rand.nextInt(App.COLS);
            int testRow = rand.nextInt(App.ROWS);
            int testXPos = gridToPixel(testCol);
            int testYPos = gridToPixel(testRow);

            // Checks if the enemy is far enough from the player
            if (Math.abs(player.xPos - testXPos) + Math.abs(player.yPos - testYPos) >= App.TELEPORTRANGE * App.SPRITESIZE) {

                // Checks if the tile is empty 
                if (tileMap[testRow][testCol] == null) {    
                    setCoords(testXPos, testYPos);
                    moveRemaining = 0;

                    success = true;
                }
            }
        }
    }

    /**
     * Unused, but must be defined
     */
    @Override
    public boolean tick() {
        return true;
    }

    /**
     * Updates the enemy every frame
     * @param level The level the enemy exists in
     * @return true always
     */
    public boolean tick(Level level) {
        weapon.reduceCooldown();
        if (!incrementMove()) {
            handleMovement(level);
        }
        return true;
    }
}
