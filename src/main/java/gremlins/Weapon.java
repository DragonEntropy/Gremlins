package gremlins;

import processing.core.PImage;

import java.util.List;

/**
 * The weapons that the player and enemies use to fire projectiles
 */
public class Weapon {
    private ProjectileType type;
    private PImage[] spriteSet;
    private double cooldown;
    private double currentCooldown = 0;

    /**
     * The constructor for the Weapon class
     * @param type The type of projectile the weapon fires
     * @param spriteSet The array of sprites for the weapon's projectile
     * @param cooldown The cooldown for firing the weapon in seconds
     */
    public Weapon(ProjectileType type, PImage[] spriteSet, double cooldown) {
        this.type = type;
        this.cooldown = cooldown;
        this.spriteSet = spriteSet;
    }

    /**
     * Creates a duplicate of the weapon
     * @return A copy of the weapon
     */
    public Weapon copy() {
        return new Weapon(type, spriteSet, cooldown);
    }

    /**
     * Fires a projectile from the weapon if possible
     * @param xPos The pixel x-coordinate of the weapon
     * @param yPos The pixel y-coordinate of the weapon
     * @param direction The direction of the fired projectile
     * @param projectiles The list of active projectiles of the level
     * @return true if the weapon is fired (otherwise returns false)
     */
    public boolean fire(int xPos, int yPos, Direction direction, List<Projectile> projectiles) {
        // Only fires if the cooldown has elapsed
        if (currentCooldown <= 0) {

            // Fires from the centre of a tile
            int newXPos = xPos - xPos % App.SPRITESIZE;
            int newYPos = yPos - yPos % App.SPRITESIZE;
            projectiles.add(new Projectile(newXPos, newYPos, spriteSet, type, direction));
            currentCooldown = cooldown;

            return true;
        }
        
        return false;
    }

    /**
     * Reduces the weapon's cooldown
     */
    public void reduceCooldown() {
        if (currentCooldown > 0) {
            currentCooldown -= (double)1 / App.FPS;
        }
    }

    /**
     * Gets the weapon's current cooldown
     * @return The current cooldown of the weapon
     */
    public double getCurrentCooldown() {
        return currentCooldown;
    }

    /**
     * Gets the weapon's maximum cooldown
     * @return The maximum coodlwon of the weapon
     */
    public double getMaxCooldown() {
        return cooldown;
    }

    /**
     * Gets the type of projectile fired by the weapon
     * @return The projectile type the weapon fires
     */
    public ProjectileType getProjectileType() {
        return type;
    }
}
