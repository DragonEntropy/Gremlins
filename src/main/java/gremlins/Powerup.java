package gremlins;

import processing.core.PImage;

import java.util.Random;

/**
 * The abstract class enabling the functionality of any powerup
 */
public abstract class Powerup extends Tile {
    private int powerupTimer = 0;
    private int availabilityTimer = 0;
    private int powerupCooldown;
    private int availabilityCooldown;
    private boolean isAvailable = false;

    protected Random rand;
    protected PowerupType powerupType;

    /**
     * The constructor for the Powerup class
     * @param xPos The pixel x-coordinate of the powerup
     * @param yPos The pixel y-coordinate of the powerup
     * @param spriteSet The array of sprites for the powerup
     * @param powerupCooldown The duration that the powerup is active for in seconds
     * @param availabilityCooldown The average time until the powerup respawns after expiring in seconds 
     * @param powerupType The type of powerup
     * @param rand The random number generator for determining the powerup's respawn cooldown
     */
    public Powerup(int xPos, int yPos, PImage[] spriteSet, int powerupCooldown, int availabilityCooldown, PowerupType powerupType, Random rand) {
        super(xPos, yPos, spriteSet, TileType.POWERUP);
        this.powerupCooldown = powerupCooldown;
        this.availabilityCooldown = availabilityCooldown;
        this.powerupType = powerupType;
        this.rand = rand;

        // Initially the powerup is unavailable for a set amount of time
        setAvailabilityTimer((availabilityCooldown - powerupCooldown) * App.FPS);
        updateSprite(1);
    }

    /**
     * Determines what happens when the player contacts the powerup tile
     * @param level The level where the powerup exists in
     * @return true if the powerup was activated (otherwise returns false)
     */
    @Override
    public boolean contact(Level level) {
        if (isAvailable) {    
            System.out.println("Powerup activated");
            isAvailable = false;

            activatePowerup(level);
            
            // Sets the powerup and availability cooldowns
            // The cooldown until the powerup becomes available again is within 50% above or below the default cooldown
            setPowerupTimer(powerupCooldown * App.FPS);
            setAvailabilityTimer((int)((rand.nextDouble() + 0.5) * (availabilityCooldown - powerupCooldown) * App.FPS));
            updateSprite(1);
    
            return true;
        }

        return false;
    }

    /**
     * Reduces the active timers of the powerup
     * @return false if the powerup expired (returns true otherwise)
     */
    public boolean reduceTimers() {
        // First reduces the powerup timer
        if (powerupTimer > 0) {
            powerupTimer--;
            if (powerupTimer == 0) {
                return false;
            }
        }

        // If the powerup timer is zero, reduces the availability timer
        else if (availabilityTimer > 0) {
            availabilityTimer--;
            if (availabilityTimer == 0) {
                isAvailable = true;
                updateSprite(0);
            }
        }

        return true;
    }

    /**
     * Checks if the powerup is available
     * @return true if the powerup is available (otherwise returns false)
     */
    public boolean getAvailability() {
        return isAvailable;
    }

    /**
     * Gets the powerup's type
     * @return The type of the powerup
     */
    public PowerupType getPowerupType() {
        return powerupType;
    }

    /**
     * Checks if the powerup is active
     * @return true if the powerup is active (otherwise returns false)
     */
    public boolean isActive() {
        return powerupTimer > 0;
    }

    /**
     * Gets the current powerup cooldown
     * @return The current powerup cooldown
     */
    public double getCooldownTimer() {
        return (double)powerupTimer / App.FPS;
    }

    /**
     * Sets the value of the current powerup cooldown
     * @param value The new current powerup cooldown
     */
    public void setPowerupTimer(int value) {
        powerupTimer = value;
    }

    /**
     * Sets the value of the current availability cooldown
     * @param value The new current availability cooldown
     */
    public void setAvailabilityTimer(int value) {
        availabilityTimer = value;
    }
    
    /**
     * Activates the powerup
     * @param level The level that the powerup exists in
     */
    public abstract void activatePowerup(Level level);

    /**
     * Disables the powerup
     * @param level The level that the powerup exists in
     */
    public abstract void disablePowerup(Level level);
}
