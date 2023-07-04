package gremlins;

import processing.core.PImage;
import processing.core.PFont;

import java.util.Map;
import java.util.ArrayList;
import java.util.List;

/**
 * The region of the application that displays information about the game
 */
public class InfoBar {
    public static final int xPos = 0;
    public static final int yPos = App.SPRITESIZE * App.ROWS;
    public static final int width = App.WIDTH;
    public static final int height = App.BOTTOMBAR;

    private static final String livesText = "Lives:";
    private static final int livesTextOffsetX = 10;
    private static final int livesTextOffsetY = 37;
    private static final int livesOffsetX = 70;
    private static final int livesOffsetY = 20;

    private static final int levelTextOffsetX = 150;
    private static final int levelTextOffsetY = 37;

    private static final int cooldownOffsetX = 280;
    private static final int cooldownOffsetY = 25;
    private static final int cooldownSepY = 25;

    private static final int manaOffsetX = 606;
    private static final int manaOffsetY = 25;
    private static final int manaLengthX = 100;
    private static final int manaLengthY = 8;
    private static final int manaSepY = 12;

    private PImage playerImage;
    private App app;

    /**
     * The constructor for the InfoBar class
     * @param app The application that the InfoBar exists in
     * @param playerImage The sprite of the player used to display lives
     */
    public InfoBar(App app, PImage playerImage) {
        this.playerImage = playerImage;
        this.app = app;

        app.textAlign(App.LEFT);
        PFont font = app.createFont("Arial", App.SPRITESIZE);
        app.textFont(font, App.SPRITESIZE);
    }
    
    /**
     * Displays the player's lives pictographically in the InfoBar
     * @param lives The number of remaining lives the player has
     */
    public void displayLives(int lives) {
        app.text(livesText, xPos + livesTextOffsetX, yPos + livesTextOffsetY);

        for (int life = 0; life < lives; life++) {
            app.image(playerImage, xPos + livesOffsetX + life * App.SPRITESIZE, yPos + livesOffsetY);
        }
    }

    /**
     * Display's the current level as text in the InfoBar
     * @param levelNumber The current level number
     * @param totalLevels The total levels available
     */
    public void displayLevel(int levelNumber, int totalLevels) {
        String levelText = "Level: " + levelNumber + " / " + totalLevels;
        app.text(levelText, xPos + levelTextOffsetX, yPos + levelTextOffsetY);
    }

    /**
     * Displays instructions for using player's current powerups and their cooldowns
     * @param powerupList The list of the player's powerups
     */
    public void displayPowerups(List<Powerup> powerupList) {
        for (Powerup powerup : powerupList) {
            // Only displays instructions if the player's powerup is active
            if (powerup.isActive()) {

                // Instructions vary based on the current active powerups
                switch (powerup.getPowerupType()) {
                    case THUNDERBALLPOWERUP: 
                        app.fill(252,236,113);
                        app.text(String.format("Press A to shoot a thunderball  %.1f", powerup.getCooldownTimer()), xPos + cooldownOffsetX, yPos + cooldownOffsetY);
                        break;
                    
                    case HYDROBALLPOWERUP:
                        app.fill(176,252,252);
                        app.text(String.format("Press S to shoot a hydroball     %.1f", powerup.getCooldownTimer()), xPos + cooldownOffsetX, yPos + cooldownOffsetY + cooldownSepY);    
                        break;
                }
            }
        } 
    }

    /**
     * Displays a bar showing the recharge times of the player's weapons
     * @param weapons The map of the player's weapons
     */
    public void displayMana(Map<Character, Weapon> weapons) {
        List<Weapon> weaponList = new ArrayList<Weapon>(weapons.values());

        for (Weapon weapon : weaponList) {
            double cooldown = weapon.getCurrentCooldown();

            // Only shows the bar if there is an active cooldown
            if (cooldown > 0) {

                // Calculates the width of the bar in pixels based off the time remaining
                int barWidth = (int)(manaLengthX * (1 - cooldown / weapon.getMaxCooldown()));

                // Chooses the bars displayed based off the recharging weapons
                switch(weapon.getProjectileType()) {
                    case FIREBALL:     
                        app.fill(252,36,124);
                        app.rect(xPos + manaOffsetX, yPos + manaOffsetY - manaSepY, barWidth, manaLengthY);
                        break;
                    
                    case THUNDERBALL:
                        app.fill(252,171,59);
                        app.rect(xPos + manaOffsetX, yPos + manaOffsetY, barWidth, manaLengthY);
                        break;
    
                    case HYDROBALL:      
                        app.fill(39,130,252);
                        app.rect(xPos + manaOffsetX, yPos + manaOffsetY + manaSepY, barWidth, manaLengthY);
                        break;
                    
                    // This branch should never trigger since the player can't access enemy weapons
                    default:
                        break;
                }
            }
        }
    }

    /**
     * Creates a duplicate of the current InfoBar
     * @return A copy of the InfoBar
     */
    public InfoBar copy() {
        return new InfoBar(app, playerImage);
    }
}
