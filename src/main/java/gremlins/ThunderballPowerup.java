package gremlins;

import processing.core.PImage;

import java.util.Random;

/**
 * The powerup that allows the player to shoot thunderballs that pass through walls and travel much faster
 */
public class ThunderballPowerup extends Powerup {
    private char weaponChar;
    private Weapon weapon;
    private static int powerupCooldown = 10;
    private static int availabilityCooldown = 20;

    /**
     * The constructor for the ThunderballPowerup class
     * @param xPos The pixel x-coordinate of the powerup
     * @param yPos The pixel y-coordinate of the powerup
     * @param spriteSet The array of sprites for the powerup
     * @param weaponChar The weapon associated with the powerup's weapon
     * @param weapon The weapon given by the powerup
     * @param rand The random number generator for determining the powerup's respawn cooldown
     */
    public ThunderballPowerup(int xPos, int yPos, PImage[] spriteSet, char weaponChar, Weapon weapon, Random rand) {
        super(xPos, yPos, spriteSet, powerupCooldown, availabilityCooldown, PowerupType.THUNDERBALLPOWERUP, rand);
        this.weaponChar = weaponChar;
        this.weapon = weapon;
    }

    /**
     * Activates the powerup
     * @param level The level that the powerup exists in
     */
    @Override
    public void activatePowerup(Level level) {
        Player player = level.getPlayer();

        // If the player has the same powerup already active, it is disabled and the different instance of the same powerup is re-enabled
        if (player.getWeapons().containsKey(weaponChar)) {
            for (Powerup powerup : player.getPowerups()) {
                if (powerup.getPowerupType() == powerupType && powerup.isActive()) {
                    powerup.disablePowerup(level);
                    powerup.setPowerupTimer(0);
                }
            }
        }
        
        // The powerup gives the weapon to the player
        level.getPlayer().addWeapon(weaponChar, weapon);
    }

    /**
     * Disables the powerup
     * @param level The level that the powerup exists in
     */
    @Override
    public void disablePowerup(Level level) {
        level.getPlayer().removeWeapon(weaponChar);
    }
}
