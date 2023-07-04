package gremlins;

import processing.core.PImage;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * The class representing the player
 */
public class Player extends MovingObject {
    private static int etherealness = 0;

    private int lives;
    private Map<Character, Weapon> weapons;
    private List<Powerup> powerups;

    /**
     * The constructor for the Player class
     * @param xPos The pixel x-coordinate of the player
     * @param yPos The pixel y-coordinate of the player
     * @param spriteSet The array of sprites for the player
     * @param speed The speed of the player in pixels per frame
     * @param lives The lives remaining for the player
     * @param weapons The map of the player's weapons
     */
    public Player(int xPos, int yPos, PImage[] spriteSet, int speed, int lives, Map<Character, Weapon> weapons) {
        super(xPos, yPos, spriteSet, speed, etherealness, CollisionType.PLAYER);
        this.lives = lives;
        this.weapons = weapons;
        this.vector = Direction.EAST;
        this.powerups = new ArrayList<Powerup>();
    }

    /**
     * Attempts to move the player based off keyboard inputs
     * @param rightPressed true if the right-arrow key is currently pressed
     * @param leftPressed true if the left-arrow key is currently pressed
     * @param upPressed true if the up-arrow key is currently pressed
     * @param downPressed true if the down-arrow key is currently pressed
     * @param currentLevel the level where the player exists
     */
    public void handleMovement(boolean rightPressed, boolean leftPressed, boolean upPressed, boolean downPressed, Level currentLevel) {
        if (rightPressed) {
            startMove(Direction.EAST, currentLevel.getMap(), false);
        }
        if (leftPressed) {
            startMove(Direction.WEST, currentLevel.getMap(), false);
        }
        if (upPressed) {
            startMove(Direction.NORTH, currentLevel.getMap(), false);
        }
        if (downPressed) {
            startMove(Direction.SOUTH, currentLevel.getMap(), false);
        }

        // Updates the player's sprite based on the player's direction vector
        updateSprite(vector.ordinal());
    }

    /**
     * Fires the player's triggered weapon
     * @param key The key corresponding to the weapon being fired
     * @param projectiles The list of projectiles in the level
     * @return true if the weapon is fired successfully (otherwise returns false)
     */
    public boolean useWeapon(Character key, List<Projectile> projectiles) {
        if (weapons.keySet().contains(key)) {
            weapons.get(key).fire(xPos, yPos, vector, projectiles);
            return true;
        }

        return false;
    }

    /**
     * Gets the tile that the player is currently on top of if they have completed their move
     * @param tileMap The tile map for the level where the player exists
     * @return The tile that the player is on (returns null if the player has not completed their move)
     */
    public Tile currentTile(Tile[][] tileMap) {
        if (moveRemaining == 0) {
            int col = pixelToGrid(xPos);
            int row = pixelToGrid(yPos);
            Tile tile = tileMap[row][col];
            if (tile != null) {    
                return tile;
            }
        }

        return null;
    }

    /**
     * Removes a life from the player
     * @param level The level where the player exists
     * @return true if the player has lives remaining (otherwise returns false)
     */
    public boolean removeLife(Level level) {
        lives--;

        // Also disables all active powerups
        for (Powerup powerup : powerups) {
            powerup.disablePowerup(level);
        }
        powerups = new ArrayList<Powerup>();

        if (lives > 0) {
            return true;
        }

        return false;
    }

    /**
     * Gets the player's remaining lives
     * @return The player's remaining lives
     */
    public int getLives() {
        return lives;
    }

    /**
     * Adds a weapon to the player's weapon map
     * @param c The character that fires the weapon
     * @param weapon The weapon being added
     * @return true if the player already has the weapon (returns false otherwise)
     */
    public boolean addWeapon(char c, Weapon weapon) {
        if (weapons.keySet().contains(c)) {
            return false;
        }
        else {
            weapons.put(c, weapon);
            return true;
        }
    }

    /**
     * Removes a weapon from the player's weapon map
     * @param c The character that fires the weapon
     * @return true if the player does not have the weapon (returns false otherwise)
     */
    public boolean removeWeapon(char c) {
        if (weapons.keySet().contains(c)) {
            weapons.remove(c);
            return true;
        }
        
        return false;
    }

    /**
     * Gets the player's weapons
     * @return The map of the player's weapons
     */
    public Map<Character, Weapon> getWeapons() {
        return weapons;
    }

    /**
     * Adds a powerup to the player's powerups
     * @param powerup The powerup being added
     */
    public void addPowerup(Powerup powerup) {
        powerups.add(powerup);
    }

    /**
     * Gets a list of the player's powerups
     * @return The list of the player's powerups
     */
    public List<Powerup> getPowerups() {
        return powerups;
    }

    /**
     * Updates the player every frame
     * @return true always
     */
    @Override
    public boolean tick() {
        incrementMove();

        for (Weapon weapon : weapons.values()) {
            weapon.reduceCooldown();
        }

        return true;
    }
}