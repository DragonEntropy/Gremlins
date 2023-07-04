package gremlins;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PFont;
import processing.data.JSONObject;
import processing.data.JSONArray;

import java.util.Random;

import java.io.*;
import java.util.Map;
import java.util.HashMap;

/**
 * The main application for the game
 */
public class App extends PApplet {

    // Constants
    public static final int WIDTH = 720;
    public static final int HEIGHT = 720;
    public static final int BOTTOMBAR = 60;
    public static final int ROWS = 33;
    public static final int COLS = 36;
    
    public static final int SPRITESIZE = 20;

    public static final int ENDFONTSIZE = 64;

    public static final int FPS = 60;
    public static final int DESTORYFRAMES = 4; 

    public static final int PLAYERSPEED = 2;
    public static final int TELEPORTRANGE = 10;

    public static final String configPath = "config.json";

    public static final Character[] weaponChars = {' ', 'a', 's'};
    
    // Sprites
    public PImage[] brickwallSprites;
    public PImage[] stonewallSprites;
    public PImage[] doorSprites;

    public PImage[] gremlinSprites;
    public PImage[] ghostGremlinSprites;
    public PImage[] furyGremlinSprites;

    public PImage[] wizardSprites;

    public PImage[] fireballSprites;
    public PImage[] slimeballSprites;
    public PImage[] thunderballSprites;
    public PImage[] ghostballSprites;
    public PImage[] hydroballSprites;
    public PImage[] furyballSprites;

    public PImage[] thunderballPowerupSprites;
    public PImage[] hydroballPowerupSprites;
    
    // Other
    private Random rand;
    private int levelNumber = 0;
    private int levelCount;
    private JSONArray levels;
    private Level level;
    private JSONObject levelJSON;
    private Player player;
    private InfoBar infoBar;
    private int maxLives;

    private boolean rightPressed = false;
    private boolean leftPressed = false;
    private boolean upPressed = false;
    private boolean downPressed = false;
    private Character charPressed;

    private Map<String, Weapon> weaponList;

    private boolean isGameActive = true;
    private boolean isGameWon = false;
    private final int maxFinishCooldown = 1;
    private int finishCooldown;

    /**
     * The constructor for the App class
     */
    public App() {}

    /**
     * Configures the application size
     */
    public void settings() {
        size(WIDTH, HEIGHT);
    }

    /**
     * Sets up the application's initial state on launch. Creates the player, loads sprites, finds the levels, etc
     */
    public void setup() {
        frameRate(FPS);

        // Loading sprites
        stonewallSprites = new PImage[] {loadImage(this.getClass().getResource("stonewall.png").getPath().replace("%20", " "))};

        PImage brickwall = loadImage(this.getClass().getResource("brickwall.png").getPath().replace("%20", " "));
        PImage brickwallDestroyed0 = loadImage(this.getClass().getResource("brickwall_destroyed0.png").getPath().replace("%20", " "));
        PImage brickwallDestroyed1 = loadImage(this.getClass().getResource("brickwall_destroyed1.png").getPath().replace("%20", " "));
        PImage brickwallDestroyed2 = loadImage(this.getClass().getResource("brickwall_destroyed2.png").getPath().replace("%20", " "));
        PImage brickwallDestroyed3 = loadImage(this.getClass().getResource("brickwall_destroyed3.png").getPath().replace("%20", " "));
        brickwallSprites = new PImage[] {brickwall, brickwallDestroyed0, brickwallDestroyed1, brickwallDestroyed2, brickwallDestroyed3};

        doorSprites = new PImage[] {loadImage(this.getClass().getResource("door.png").getPath().replace("%20", " "))};

        gremlinSprites = new PImage[] {loadImage(this.getClass().getResource("gremlin.png").getPath().replace("%20", " "))};
        ghostGremlinSprites = new PImage[] {loadImage(this.getClass().getResource("ghost.png").getPath().replace("%20", " "))};
        furyGremlinSprites = new PImage[] {loadImage(this.getClass().getResource("fury.png").getPath().replace("%20", " "))};

        PImage wizardRight = loadImage(this.getClass().getResource("wizard1.png").getPath().replace("%20", " "));
        PImage wizardUp = loadImage(this.getClass().getResource("wizard2.png").getPath().replace("%20", " "));
        PImage wizardLeft = loadImage(this.getClass().getResource("wizard0.png").getPath().replace("%20", " "));
        PImage wizardDown = loadImage(this.getClass().getResource("wizard3.png").getPath().replace("%20", " "));
        wizardSprites = new PImage[] {wizardRight, wizardUp, wizardLeft, wizardDown};

        fireballSprites = new PImage[] {loadImage(this.getClass().getResource("fireball.png").getPath().replace("%20", " "))};
        slimeballSprites = new PImage[] {loadImage(this.getClass().getResource("slime.png").getPath().replace("%20", " "))};
        thunderballSprites = new PImage[] {loadImage(this.getClass().getResource("thunderball.png").getPath().replace("%20", " "))};
        hydroballSprites = new PImage[] {loadImage(this.getClass().getResource("hydroball.png").getPath().replace("%20", " "))};
        ghostballSprites = new PImage[] {loadImage(this.getClass().getResource("ghostball.png").getPath().replace("%20", " "))};
        furyballSprites = new PImage[] {loadImage(this.getClass().getResource("furyball.png").getPath().replace("%20", " "))};

        thunderballPowerupSprites = new PImage[] {loadImage(this.getClass().getResource("thunderball_powerup.png").getPath().replace("%20", " ")), null};
        hydroballPowerupSprites = new PImage[] {loadImage(this.getClass().getResource("hydroball_powerup.png").getPath().replace("%20", " ")), null};

        rand = new Random();

        // Reading JSON file
        JSONObject conf = loadJSONObject(new File(configPath));
        maxLives = conf.getInt("lives");
        levels = conf.getJSONArray("levels");
        levelCount = levels.size();

        // Other setup
        infoBar = new InfoBar(this, wizardRight);
        finishCooldown = maxFinishCooldown * FPS;

        // Starts the game by progressing the level
        progressLevel();
    }   

    /**
     * Manages key press events such as movement and weapon firing
     */
    public void keyPressed() {
        // If the game is active, key presses affect the player
        if (isGameActive) {  

            // Key presses that configure movement
            if (keyCode == RIGHT) {
                rightPressed = true;
            }
            if (keyCode == LEFT) {
                leftPressed = true;
            }
            if (keyCode == UP) {
                upPressed = true;
            }
            if (keyCode == DOWN) {
                downPressed = true;
            }

            // Other key presses are checked for associated weapons
            for (int i = 0; i < weaponChars.length; i++) {
                if (key == weaponChars[i]) {
                    // Only one weapon can be fired at a time
                    charPressed = weaponChars[i];
                    break;
                }
            }
        }

        // If the game has ended, a key press will reset the game (if the one second buffer elapsed)
        else if (finishCooldown <= 0) {
            reset();
        }
    }
    
    /**
     * Manages key release events to stop movement and stop firing weapons
     */
    public void keyReleased(){
        // Key presses that configure movement
        if (keyCode == RIGHT) {
            rightPressed = false;
        }
        if (keyCode == LEFT) {
            leftPressed = false;
        }
        if (keyCode == UP) {
            upPressed = false;
        }
        if (keyCode == DOWN) {
            downPressed = false;
        }
            
        // Other key presses are checked for associated weapons
        for (int i = 0; i < weaponChars.length; i++) {
            // Weapon only stops firing if the key released corresponds to the weapon
            if (key == weaponChars[i] && (Character)key == charPressed) {
                charPressed = null;
                break;
            }
        }
    }

    /**
     * Updates the game every frame
     */
    public void draw() {
        // If the game is active, the main game loop runs
        if (isGameActive) {   
            // Creating the beige background 
            fill(191, 153, 114);
            rect(-1, -1, WIDTH + 2, HEIGHT + 2);

            // Adding level elements
            level.tick();

            // Adding the info bar displays
            fill(255);
            infoBar.displayLives(player.getLives());
            infoBar.displayLevel(levelNumber,levelCount);
            infoBar.displayPowerups(player.getPowerups());
            infoBar.displayMana(player.getWeapons());
            
            // Handling player movement and weapon firing
            player.handleMovement(rightPressed, leftPressed, upPressed, downPressed, level);
            player.tick();
            player.useWeapon(charPressed, level.getProjectiles());
            
            // Handling player powerups
            for (Powerup powerup : player.getPowerups()) {
                if (!powerup.reduceTimers()) {
                    powerup.disablePowerup(level);
                }
            }

            // Drawing the player
            player.draw(this);
        }

        // Otherwise, the end screen loop is run
        else {
            // Reduces the restart game cooldown timer
            if (finishCooldown > 0) {
                finishCooldown -= 1;
            }

            // Adds end of game text
            PFont font = createFont("Arial", ENDFONTSIZE);
            textAlign(CENTER, CENTER);
            textFont(font, App.ENDFONTSIZE);
            fill(191, 153, 114);
            rect(-1, -1, WIDTH + 2, HEIGHT + 2);
            fill(255);

            if (isGameWon) {
                text("YOU WIN!", WIDTH / 2, HEIGHT / 2);
            }
            else {
                text("GAME OVER!", WIDTH / 2, HEIGHT / 2);
            }
        }
    }

    /**
     * Ends the current game
     * @param isGameWon Determines if the win or lose screen should be displayed
     */
    public void endGame(boolean isGameWon) {
        isGameActive = false;
        this.isGameWon = isGameWon;
    }

    /**
     * Tries to progress to the next level. If there is no next level, the game ends
     * @return true if there is a next level (otherwise returns false)
     */
    public boolean progressLevel() {
        // Progresses a level and checks that the max level count has not been reached
        levelNumber++;
        if (levelNumber <= levelCount) {
            // Gets the next level's data
            levelJSON = levels.getJSONObject(levelNumber - 1);
            
            double wizardCooldown = levelJSON.getDouble("wizard_cooldown");
            double enemyCooldown = levelJSON.getDouble("enemy_cooldown");

            // Configures the weapons for the level
            weaponList = new HashMap<String, Weapon>();
            weaponList.put("Fireball Launcher", new Weapon(ProjectileType.FIREBALL, fireballSprites, wizardCooldown));
            weaponList.put("Slimeball Launcher", new Weapon(ProjectileType.SLIMEBALL, slimeballSprites, enemyCooldown));
            weaponList.put("Thunderball Launcher", new Weapon(ProjectileType.THUNDERBALL, thunderballSprites, wizardCooldown));
            weaponList.put("Ghostball Launcher", new Weapon(ProjectileType.GHOSTBALL, ghostballSprites, enemyCooldown));
            weaponList.put("Hydroball Launcher", new Weapon(ProjectileType.HYDROBALL, hydroballSprites, wizardCooldown));
            weaponList.put("Furyball Launcher", new Weapon(ProjectileType.FURYBALL, furyballSprites, enemyCooldown));

            Map<Character, Weapon> playerWeapons = new HashMap<Character, Weapon>();
            playerWeapons.put(' ', weaponList.get("Fireball Launcher").copy());

            // Generates a new player for the next level (preserving the lives)
            int lives = maxLives;
            if (player != null) {
                lives = player.getLives();
            }
            player = new Player(0, 0, wizardSprites, PLAYERSPEED, lives, playerWeapons);
            level = new Level(this, player, levelJSON.getString("layout"), weaponList, rand);

            // Checks that the level is valid (otherwise skips it)
            if (!level.getValidity()) {
                progressLevel();
            }

            return true;
        }

        // If there are no more levels, the game is won
        else {
            endGame(true);
            return false;
        }
    }

    /**
     * Creates a new game after the game ends
     */
    public void reset() {
        // Resets game status variables to their default values
        isGameActive = true;
        isGameWon = false;

        infoBar = infoBar.copy();
        finishCooldown = maxFinishCooldown * FPS;
        levelNumber = 0;
        player = null;

        // Progresses to the first level
        progressLevel();
    }

    /*
     * Starts the application
     */
    public static void main(String[] args) {
        PApplet.main("gremlins.App");
    }

    // For testing only (not documented)
    public Player getPlayer() {
        return player;
    }
    public Level getLevel() {
        return level;
    }
    public InfoBar getInfoBar() {
        return infoBar;
    }
    public Random getRandom() {
        return rand;
    }
    public int getLevelNumber() {
        return levelNumber;
    }
    public int getLevelCount() {
        return levelCount;
    }
    public Map<String, Weapon> getWeaponsList() {
        return weaponList;
    }
    public boolean isLeftPressed() {
        return leftPressed;
    }
    public boolean isRightPressed() {
        return rightPressed;
    }
    public boolean isUpPressed() {
        return upPressed;
    }
    public boolean isDownPressed() {
        return downPressed;
    }
    public void setFinishCooldown(int cooldown) {
        finishCooldown = cooldown;
    }
}
