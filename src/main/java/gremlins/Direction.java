package gremlins;

/**
 * Represents cardinal directions in the map
 */
public enum Direction {

    EAST (1, 0),
    NORTH (0, -1),
    WEST (-1, 0),
    SOUTH (0, 1);

    public final int[] vector;

    private Direction(int xValue, int yValue) {
        this.vector = new int[] {xValue, yValue};
    }

    /**
     * Gets the vector's x-value
     * @return The x-value of the vector
     */
    public int getX() {
        return vector[0];
    }

    /**
     * Gets the vector's y-value
     * @return The y-value of the vector
     */
    public int getY() {
        return vector[1];
    }

    /**
     * Gets the anticlockwise rotation of the vector
     * @return The vector rotated anticlockwise
     */
    public Direction anticlockwise() {
        return Direction.values()[(this.ordinal() + 1) % 4];
    }

    /**
     * Gets the flipped direction of the vector
     * @return The vector flipped
     */
    public Direction flip() {
        return Direction.values()[(this.ordinal() + 2) % 4];
    }

    /**
     * Gets the clockwise rotation of the vector
     * @return The vector rotated clockwise
     */
    public Direction clockwise() {
        return Direction.values()[(this.ordinal() + 3) % 4];
    }

    /**
     * Gets the best matching direction from an x and y direction
     * @param x The x-value of the vector
     * @param y The y-value of the vector
     * @return The best matching direction
     */
    public static Direction fromVector(int x, int y) {
        if (Math.abs(y) > Math.abs(x)) {
            if (y > 0) {
                return SOUTH;
            }
            else {
                return NORTH;
            }
        }
        else {
            if (x > 0) {
                return EAST;
            }
            else if (x < 0){
                return WEST;
            }
        }

        return null;
    }
}

