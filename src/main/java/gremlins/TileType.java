package gremlins;

/**
 * Represents the tile types available and their strengths
 */
public enum TileType {

    /*
     * Strength is the minimum penetration level or etherealness required to break or pass through the tile respectively
     * 0 - Indestructible
     * -1 - Always passed through
     */
    BRICKWALL (1),
    STONEWALL (0),
    EXIT (-1),
    POWERUP (-1);

    public final int strength;

    private TileType(int strength) {
        this.strength = strength;
    }
}