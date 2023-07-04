package gremlins;

import processing.core.PImage;

import java.util.Random;

/**
 * The most basic type of enemy
 */
public class GreenGremlin extends Enemy {
    private static int speed = 1;
    private static int etherealness = 0;

    /**
     * The constructor for the GreenGremlin class
     * @param xPos The pixel x-coordinate of the enemy
     * @param yPos The pixel y-coordinate of the enemy
     * @param spriteSet The array of sprites for the enemy
     * @param weapon The weapon of the enemy
     * @param rand The random number generator for the enemy
     * @param tileMap The tile map for the level where the enemy is placed
     */
    public GreenGremlin(int xPos, int yPos, PImage[] spriteSet, Weapon weapon, Random rand, Tile[][] tileMap) {
        super(xPos, yPos, spriteSet, speed, etherealness, weapon, rand, tileMap);
    }
}
