package gremlins;

import processing.core.PApplet;
import processing.core.PImage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;

public class TestCases {
    App app;
    Player player;
    Level level;
    PImage[] testSprites;
    InfoBar infoBar;

    // Creates an app before every testcases is run
    @BeforeEach
    public void appSetup() {
        app = new App();
        app.loop();
        PApplet.runSketch(new String[] { "App" }, app);
        app.setup();
        PImage sprite = app.brickwallSprites[1];
        assertNotNull(sprite);
        testSprites = new PImage[] {null, sprite, null, null, null};
        
        player = app.getPlayer();
        level = app.getLevel();
        infoBar = app.getInfoBar();
    }

    // Performs tests on the tile class
    @Test
    public void tileTest() {
        assertNotNull(testSprites);
        Tile brick = new Tile(0, 1, testSprites, TileType.BRICKWALL);

        // Tests the destroy sequence of the brick
        brick.draw(app);
        assertTrue(brick.tick());
        brick.draw(app);
        brick.destroy();
        for (int i = 0; i < 16; i++) {
            brick.tick();
        }

        // Checks that the brick is destroyed in the correct frame
        assertFalse(brick.tick());

        // Tests changing the tile's coordinates
        assertEquals(0, brick.getX());
        assertEquals(1, brick.getY());

        brick.setCoords(3, 5);
        
        assertEquals(3, brick.getX());
        assertEquals(5, brick.getY());

        // Tests other tile methods
        assertEquals(TileType.BRICKWALL.strength, brick.getStrength());
        assertEquals(TileType.BRICKWALL, brick.getTileType());
        assertEquals(false, brick.contact(level));
        assertEquals(12, Tile.pixelToGrid(240));
        assertEquals(360, Tile.gridToPixel(18));
    }

    // Tests that the player can recognise an exit tile
    @Test
    public void exitTest() {
        player.setCoords(31 * App.SPRITESIZE, 31 * App.SPRITESIZE);
        assertTrue(level.checkTileEvent());
    }

    // Performs tests on the weapon class
    @Test
    public void weaponTest() {
        Weapon weapon = player.getWeapons().get(' ');
        
        // Tries firing a weapon with and without a cooldown
        assertTrue(weapon.fire(player.getX(), player.getY(), player.vector, level.getProjectiles()));
        assertFalse(weapon.fire(player.getX(), player.getY(), player.vector, level.getProjectiles()));
        infoBar.displayMana(player.getWeapons());
    }

    // Performs tests on the direction enum
    @Test
    public void directionTest() {
        Direction vector = Direction.NORTH;
        // Testing change in direction methods
        assertEquals(Direction.EAST, vector.clockwise());
        assertEquals(Direction.WEST, vector.anticlockwise());
        assertEquals(Direction.SOUTH, vector.flip());

        // Testing the fromVector method
        assertEquals(Direction.EAST, Direction.fromVector(1, 0));
        assertEquals(Direction.NORTH, Direction.fromVector(0, -1));
        assertEquals(Direction.WEST, Direction.fromVector(-1, 0));
        assertEquals(Direction.SOUTH, Direction.fromVector(0, 1));
    }

    // Performs tests on player movement
    @Test
    public void moveTest() {
        Tile[][] tileMap = level.getMap();

        // Tests that a player outside the map cannot move unless forced
        // Also tests that a player cannot move with a null vector
        Player illegalPlayer1 = new Player(-App.SPRITESIZE, 0, testSprites, 4, 3, player.getWeapons());
        assertFalse(illegalPlayer1.validateMove(null, tileMap));
        assertFalse(illegalPlayer1.validateMove(Direction.NORTH, tileMap));
        assertFalse(illegalPlayer1.startMove(Direction.NORTH, tileMap, false));
        assertTrue(illegalPlayer1.startMove(Direction.NORTH, tileMap, true));

        // Tests that a player outside the other illegal boundaries of the map cannot move
        Player illegalPlayer2 = new Player(0, -App.SPRITESIZE, testSprites, 4, 3, player.getWeapons());
        Player illegalPlayer3 = new Player(37 * App.SPRITESIZE, 0, testSprites, 4, 3, player.getWeapons());
        Player illegalPlayer4 = new Player(0, 37 * App.SPRITESIZE, testSprites, 4, 3, player.getWeapons());
        assertFalse(illegalPlayer2.validateMove(Direction.NORTH, tileMap));
        assertFalse(illegalPlayer3.validateMove(Direction.NORTH, tileMap));
        assertFalse(illegalPlayer4.validateMove(Direction.NORTH, tileMap));
        
        // Tests that a player can move in a map without obstructions
        // Also tests that a player cannot start a new move while undergoing an existing move
        Player legalPlayer = new Player(App.SPRITESIZE, App.SPRITESIZE, testSprites, 4, 3, player.getWeapons());
        assertTrue(legalPlayer.validateMove(Direction.EAST, tileMap));
        assertTrue(legalPlayer.startMove(Direction.EAST, tileMap, false));
        assertFalse(legalPlayer.startMove(Direction.EAST, tileMap, false));
    }

    // Performs tests of enemy movement
    @Test
    public void enemyTest() {
        Random rand = app.getRandom();
        Tile[][] tileMap = level.getMap();

        Enemy fury1 = new FuryGremlin(4 * App.SPRITESIZE, 2 * App.SPRITESIZE, testSprites, null, rand, tileMap);
        Enemy fury2 = new FuryGremlin(2 * App.SPRITESIZE, 12 * App.SPRITESIZE, testSprites, null, rand, tileMap);
        Enemy fury3 = new FuryGremlin(App.SPRITESIZE, App.SPRITESIZE, testSprites, null, rand, tileMap);
        Enemy fury4 = new FuryGremlin(0, 2 * App.SPRITESIZE, testSprites, null, rand, tileMap);
        Enemy furyRotateOtherWay = new FuryGremlin(7 * App.SPRITESIZE, 6 * App.SPRITESIZE, testSprites, null, rand, tileMap);
        Enemy furyTurnAround = new FuryGremlin(App.SPRITESIZE, 23 * App.SPRITESIZE, testSprites, null, rand, tileMap);

        // Tests the movement of each fury based on their current position relative to the player
        // Also tests furys that are forced to rotate away from the player or turn away from the player
        fury1.handleMovement(level);
        fury2.handleMovement(level);
        fury3.handleMovement(level);
        fury4.handleMovement(level);
        furyRotateOtherWay.handleMovement(level);
        furyTurnAround.handleMovement(level);

        // Tests the movement of a regular enemy that is forced to turn around
        Enemy greenTurnAround = new GreenGremlin(App.SPRITESIZE, 23 * App.SPRITESIZE, testSprites, null, rand, tileMap);
        assertTrue(greenTurnAround.handleMovement(level));
        assertFalse(greenTurnAround.handleMovement(level));
        for (int tick = 0; tick < App.FPS; tick++) {
            greenTurnAround.incrementMove();
        }
        assertTrue(greenTurnAround.handleMovement(level));

        // Tests the movement of a regular enemy that is force to turn
        Enemy greenTurnOneWay = new GreenGremlin(App.SPRITESIZE, 26 * App.SPRITESIZE, testSprites, null, rand, tileMap);
        assertTrue(greenTurnOneWay.handleMovement(level));
        for (int tick = 0; tick < App.FPS; tick++) {
            greenTurnOneWay.incrementMove();
        }
        assertTrue(greenTurnOneWay.handleMovement(level));

        // Tests the movement of a regular enemy that can turn either left or right
        Enemy greenTurnEitherWay = new GreenGremlin(30 * App.SPRITESIZE, 5 * App.SPRITESIZE, testSprites, null, rand, tileMap);
        assertTrue(greenTurnEitherWay.handleMovement(level));
        for (int tick = 0; tick < App.FPS; tick++) {
            greenTurnEitherWay.incrementMove();
        }
        assertTrue(greenTurnEitherWay.handleMovement(level));
    }

    // Performs tests on a powerup inside the map
    @Test
    public void mapPowerupTest() {
        Tile[][] tileMap = level.getMap();

        // Moves the player to a powerup and checks that they see the powerup
        player.setCoords(9 * App.SPRITESIZE, 7 * App.SPRITESIZE);
        Powerup powerup = (Powerup)tileMap[7][9];
        assertNotNull(tileMap[7][9]);
        assertEquals(TileType.POWERUP, tileMap[7][9].getTileType());
        player.resetMove();
        assertTrue(level.checkTileEvent());
        assertNotNull(powerup.getPowerupType());
        assertFalse(powerup.contact(level));

        // Resets the powerup availabiility and tries to activate the powerup again
        powerup.setAvailabilityTimer(2);
        assertTrue(powerup.reduceTimers());
        assertTrue(powerup.reduceTimers());
        assertTrue(powerup.getAvailability());
        assertTrue(powerup.contact(level));

        // Fires the player's weapons and tests the mana and powerup displays
        for (Weapon weapon : player.getWeapons().values()) {
            weapon.fire(player.getX(), player.getY(), Direction.EAST, level.getProjectiles());
        }
        infoBar.displayMana(player.getWeapons());
        infoBar.displayPowerups(player.getPowerups());

        // Tests expiring the powerup
        powerup.setPowerupTimer(2);
        assertEquals((double)2 / App.FPS, powerup.getCooldownTimer());
        assertTrue(powerup.reduceTimers());
        assertFalse(powerup.reduceTimers());

        assertTrue(powerup.reduceTimers());
    }

    // Performs tests on both powerups specifically
    @Test
    public void specificPowerupTest() {
        // Tests the thunderball powerup
        Weapon thunderballLauncher = app.getWeaponsList().get("Thunderball Launcher");
        Powerup thunderball1 = new ThunderballPowerup(1, 1, testSprites, 'a', thunderballLauncher.copy(), app.getRandom());
        Powerup thunderball2 = new ThunderballPowerup(2, 2, testSprites, 'a', thunderballLauncher.copy(), app.getRandom());

        thunderball1.activatePowerup(level);
        thunderball2.activatePowerup(level);

        // Tests the hydroball powerup
        Weapon hydroballLauncher = app.getWeaponsList().get("Hydroball Launcher");
        Powerup hydroball1 = new HydroballPowerup(1, 1, testSprites, 'a', hydroballLauncher.copy(), app.getRandom());
        Powerup hydroball2 = new HydroballPowerup(2, 2, testSprites, 'a', hydroballLauncher.copy(), app.getRandom());

        hydroball1.activatePowerup(level);
        hydroball2.activatePowerup(level);
    }

    // Tests how the moving objects react to collisions
    @Test
    public void collisionTest() {
        // Tests player collision with enemy (player should lose a life)
        Random rand = app.getRandom();
        Tile[][] tileMap = level.getMap();
        Enemy enemy = new GreenGremlin(player.getX(), player.getY(), testSprites, null, rand, tileMap);
        level.getEnemies().add(enemy);
        level.manageCollisions();
        assertEquals(2, player.getLives());

        // Tests player projectile collision (nothing should happen)
        Projectile p1 = new Projectile(34 * App.SPRITESIZE, 1 * App.SPRITESIZE, testSprites, ProjectileType.FIREBALL, Direction.EAST);
        Projectile p2 = new Projectile(34 * App.SPRITESIZE, 1 * App.SPRITESIZE, testSprites, ProjectileType.FIREBALL, Direction.EAST);
        level.getProjectiles().add(p1);
        level.getProjectiles().add(p2);
        level.manageCollisions();
        assertTrue(level.getProjectiles().contains(p1));
        assertTrue(level.getProjectiles().contains(p2));

        // Tests player projectile with enemy projectile collision (all colliding projectiles should be destroyed)
        Projectile p3 = new Projectile(33 * App.SPRITESIZE, 1 * App.SPRITESIZE, testSprites, ProjectileType.SLIMEBALL, Direction.EAST);
        Projectile p4 = new Projectile(34 * App.SPRITESIZE, 1 * App.SPRITESIZE, testSprites, ProjectileType.SLIMEBALL, Direction.EAST);
        level.getProjectiles().add(p3);
        level.getProjectiles().add(p4);
        level.manageCollisions();
        assertFalse(level.getProjectiles().contains(p1));
        assertFalse(level.getProjectiles().contains(p2));
        assertTrue(level.getProjectiles().contains(p3));
        assertFalse(level.getProjectiles().contains(p4));

        // Tests player projectile with enemy collision (enemy should teleport)
        Enemy dyingEnemy = new GreenGremlin(1 * App.SPRITESIZE, 1 * App.SPRITESIZE, testSprites, null, rand, tileMap);
        level.getEnemies().add(dyingEnemy);
        Projectile pEnemyKill = new Projectile(1 * App.SPRITESIZE, 1 * App.SPRITESIZE, testSprites, ProjectileType.FIREBALL, Direction.EAST);
        level.getProjectiles().add(pEnemyKill);
        level.manageCollisions();
        assertFalse(level.getProjectiles().contains(pEnemyKill));

        // Tests enemy projectile with player collision (player should lose a life)
        Projectile pPlayerKill = new Projectile(player.getX(), player.getY(), testSprites, ProjectileType.SLIMEBALL, Direction.EAST);
        level.getProjectiles().add(pPlayerKill);
        level.manageCollisions();
        assertFalse(level.getProjectiles().contains(pPlayerKill));
        assertEquals(1, player.getLives());
    }

    // Performs tests for responses to user key presses
    @Test
    public void keyPressTests() {
        // Left
        app.keyCode = 37;
        app.keyPressed();
        player.handleMovement(app.isRightPressed(), app.isLeftPressed(), app.isUpPressed(), app.isDownPressed(), level);
        app.keyReleased();

        // Up
        app.keyCode = 38;
        app.keyPressed();
        player.handleMovement(app.isRightPressed(), app.isLeftPressed(), app.isUpPressed(), app.isDownPressed(), level);
        app.keyReleased();

        // Right
        app.keyCode = 39;
        app.keyPressed();
        player.handleMovement(app.isRightPressed(), app.isLeftPressed(), app.isUpPressed(), app.isDownPressed(), level);
        app.keyReleased();
        
        // Down
        app.keyCode = 40;
        app.keyPressed();
        player.handleMovement(app.isRightPressed(), app.isLeftPressed(), app.isUpPressed(), app.isDownPressed(), level);
        app.keyReleased();

        // Real Weapon
        app.key = ' ';
        app.keyPressed();
        app.keyReleased();

        // Fake Weapon (E)
        app.keyCode = 69;
        app.keyPressed();
        app.keyReleased();
    }

    // Performs tests when the player wins the game
    @Test
    public void gameWinTest() {
        // Cycles through each level available
        while (app.getLevelNumber() < app.getLevelCount()) {  
            assertTrue(app.progressLevel());
        }
        assertFalse(app.progressLevel());

        // Tries to restart when the finish cooldown buffer is still running
        app.keyCode = 69;
        app.keyPressed();
        app.keyReleased();

        // Tries to restart when the finish cooldown buffer has expired
        app.setFinishCooldown(0);
        app.keyPressed();
        app.keyReleased();
    }

    // Performs tests when the player losses the game
    @Test
    public void gameOverTest() {
        // Kills the player until they have no lives left
        while (player.getLives() > 1) {
            level.reset();
        }
        assertFalse(level.reset());
    }
}
