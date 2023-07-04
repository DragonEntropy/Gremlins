package gremlins;

import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

/**
 * The class that encompasses a level of the game
 */
public class Level {
    private App app;
    private Player player;
    private Map<String, Weapon> weaponList;
    private Random rand;
    private String levelPath;

    private boolean isValid;

    private Tile[][] tileMap;
    private List<Enemy> enemies;
    private List<Projectile> projectiles;

    /**
     * The constructor for the Level class
     * @param app The app where the level exists
     * @param player The current player
     * @param levelPath The file path where the level's text file is contained
     * @param weaponList The list of all possible weapons
     * @param rand The random number generator for the level
     */
    public Level(App app, Player player, String levelPath, Map<String, Weapon> weaponList, Random rand) {
        this.app = app;
        this.player = player;
        this.levelPath = levelPath;
        this.weaponList = weaponList;
        this.rand = rand;

        isValid = initialiseLevel();
    }

    /**
     * Initialises the level, reading from the level's text file to place tiles, enemies, the player, powerups and enemies
     * @return true if the level is valid (otherwise returns false)
     */
    private boolean initialiseLevel() {

        // Tries to read the level's map from the specified file
        File levelFile = new File(levelPath);
        Scanner levelReader;
        try {
            levelReader = new Scanner(levelFile);
        }
        catch (FileNotFoundException e) {
            return false;
        }
        
        tileMap = new Tile[App.ROWS][App.COLS];
        enemies = new ArrayList<Enemy>();
        projectiles = new ArrayList<Projectile>();

        boolean containsStart = false;
        boolean containsExit = false;

        // Reads every row and character from the text file in the expected range
        for (int row = 0; row < App.ROWS; row++) {
            String inputLine = levelReader.nextLine();

            for (int col = 0; col < App.COLS; col++) {
                int rowPixel = GameObject.gridToPixel(row);
                int colPixel = GameObject.gridToPixel(col);

                // If a bordering tile is not a brick, the level is invalid
                char input = inputLine.charAt(col);
                if ((row == 0 || col == 0 || row == App.ROWS - 1 || col == App.COLS - 1) && input != 'X') {
                    levelReader.close();
                    return false;
                }

                // Decides what tile, player or enemy is at the location based off the character read
                switch (inputLine.charAt(col)) {
                    case 'X':
                        tileMap[row][col] = new Tile(colPixel, rowPixel, app.stonewallSprites, TileType.STONEWALL);
                        break;
                    case 'B':
                        tileMap[row][col] = new Tile(colPixel, rowPixel, app.brickwallSprites, TileType.BRICKWALL);
                        break;
                    case 'E':
                        tileMap[row][col] = new Tile(colPixel, rowPixel, app.doorSprites, TileType.EXIT);
                        containsExit = true;
                        break;
                    case 'W':
                        player.setCoords(colPixel, rowPixel);
                        player.resetMove();
                        containsStart = true;
                        break;
                    case 'G':
                        enemies.add(new GreenGremlin(colPixel, rowPixel, app.gremlinSprites, weaponList.get("Slimeball Launcher").copy(), rand, tileMap));
                        break;
                    case 'H':
                        enemies.add(new GhostGremlin(colPixel, rowPixel, app.ghostGremlinSprites, weaponList.get("Ghostball Launcher").copy(), rand, tileMap));
                        break;
                    case 'F':
                        enemies.add(new FuryGremlin(colPixel, rowPixel, app.furyGremlinSprites, weaponList.get("Furyball Launcher").copy(), rand, tileMap));
                        break;
                    case 'P':
                        Powerup powerup = generatePowerup(colPixel, rowPixel);
                        player.addPowerup(powerup);
                        tileMap[row][col] = powerup;
                        break;
                    default:
                        break;
                }
            }
        }

        levelReader.close();

        // Final validity check
        return containsStart && containsExit;
    }

    /**
     * Gets the validity of the level
     * @return true if the level is valid (otherwise returns false)
     */
    public boolean getValidity() {
        return isValid;
    }

    /**
     * Gets the tile map of the level
     * @return The 2D array of tiles (tile map) of the level
     */
    public Tile[][] getMap() {
        return tileMap;
    }

    /**
     * Gets the player inside the level
     * @return The level's player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Gets the enemies inside the level
     * @return The list of enemies in the level
     */
    public List<Enemy> getEnemies() {
        return enemies;
    }

    /**
     * Gets the projectiles inside the level
     * @return The list of projectiles in the level
     */
    public List<Projectile> getProjectiles() {
        return projectiles;
    }

    /**
     * Randomly chooses a powerup type for a powerup tile
     * @param colPixel The pixel x-coordinate of the powerup
     * @param rowPixel The pixel y-coordinate of the powerup
     * @return The generated powerup
     */
    public Powerup generatePowerup(int colPixel, int rowPixel) {
        switch (rand.nextInt(2)) {
            case 1:
                return new ThunderballPowerup(colPixel, rowPixel, app.thunderballPowerupSprites, App.weaponChars[1], weaponList.get("Thunderball Launcher").copy(), rand);
            default:
                return new HydroballPowerup(colPixel, rowPixel, app.hydroballPowerupSprites, App.weaponChars[2], weaponList.get("Hydroball Launcher").copy(), rand);
        }
    }

    /**
     * Draws the map in the application
     */
    public void drawMap() {
        for (int row = 0; row < App.ROWS; row++) {
            for (int col = 0; col < App.COLS; col++) {
                Tile tile = tileMap[row][col];
                if (tile != null) {
                    tileMap[row][col].draw(app);
                }
            }
        }
    }

    /**
     * Draws all enemies to the map and makes them use their weapon if possible
     */
    public void manageEnemies() {
        for (Enemy enemy : enemies) {
            enemy.useWeapon(this);
            enemy.tick(this);
        }

        for (Enemy enemy : enemies) {
            enemy.draw(app);
        }
    }

    /**
     * Makes all projectiles move forward and destroys inactive projectiles
     */
    public void progressProjectiles() {
        List<Projectile> inactive = new ArrayList<Projectile>();
        for (Projectile projectile : projectiles) {
            // The projectile becomes inactive if destroyed in a tile collision
            if (!projectile.tick(tileMap)) {
                inactive.add(projectile);
            }
            projectile.draw(app);
        }

        // Inactive projectiles are removed
        projectiles.removeAll(inactive);
    }

    /**
     * Broken tiles are made to undergo their destroy sequence
     */
    public void progressBrokenTiles() {
        for (int row = 0; row < App.ROWS; row++) {
            for (int col = 0; col < App.COLS; col++) {
                Tile tile = tileMap[row][col];
                if (tile != null) {

                    // Removes the tile if its destruction is complete
                    if (!tile.tick()) {
                        tileMap[row][col] = null;
                    }
                }
            }
        }
    }

    /**
     * Manages all key non-tile collision events in the level
     */
    public void manageCollisions() {
        List<Projectile> inactive = new ArrayList<Projectile>(); 
        List<Enemy> dead = new ArrayList<Enemy>();

        // Kills the player if they collide with an enemy
        for (Enemy enemy : enemies) {
            if (player.checkCollision(enemy)) {
                reset();
            }
        }

        for (Projectile projectile : projectiles) {
            
            // Kills the player if they collide with an enemy projectile
            if (projectile.getCollisionType() == CollisionType.ENEMYPROJECTILE) {
                if (player.checkCollision(projectile)) {
                    reset();
                }
            }
            
            else {

                // Kills the enemy if they collide with a player projectile
                for (Enemy enemy : enemies) {
                    if (enemy.checkCollision(projectile)) {
                        inactive.add(projectile);
                        dead.add(enemy);
                    }
                }

                // Destroys both projectiles when a player and enemy projectile collide
                for (Projectile other : projectiles) {
                    if (other.getCollisionType() == CollisionType.ENEMYPROJECTILE && projectile.checkCollision(other)) {
                        inactive.add(projectile);
                        inactive.add(other);
                    }
                }
            }
        }

        // Removes all inactive projectiles and teleports dead enemies
        projectiles.removeAll(inactive);
        for (Enemy enemy : dead) {
            enemy.teleport(player, tileMap);
        }
    }

    /**
     * Checks if the player walks on an important tile and activates its effect
     * @return true if the tile is an important tile (otherwise returns false)
     */
    public boolean checkTileEvent() {
        // Returns false if the player is on a null tile
        Tile currentTile = player.currentTile(tileMap);
        if (currentTile == null) {
            return false;
        }

        TileType currentTileType = currentTile.getTileType();

        // Progresses to the next level if the player is at an exit
        if (currentTileType == TileType.EXIT) {
            System.out.println("At exit");
            app.progressLevel();
        }

        // Makes contact with the powerup if the player is on a powerup tile
        else if (currentTileType == TileType.POWERUP) {
            currentTile.contact(this);
        }
        return true;
    }

    /**
     * Resets the level if the player has lives remaining or ends the game otherwise
     * @return true if the game has not ended (returns false otherwise)
     */
    public boolean reset() {
        if (player.removeLife(this)) {
            initialiseLevel();
            return true;
        }
        else {
            app.endGame(false);
            return false;
        }
    }   

    /**
     * Performs all key level events every frame
     */
    public void tick() {
        drawMap();

        progressProjectiles();
        progressBrokenTiles();

        checkTileEvent();
        manageCollisions();
        manageEnemies();
    }
}
