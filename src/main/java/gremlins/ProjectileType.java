package gremlins;

/**
 * Represents the projectiles available and their attributes
 */
public enum ProjectileType {
    
    FIREBALL (4, 1, 0, 0, false, CollisionType.PLAYERPROJECTILE),
    SLIMEBALL (4, 0, 0, 0, false, CollisionType.ENEMYPROJECTILE),
    GHOSTBALL (2, 0, 1, 0, false, CollisionType.ENEMYPROJECTILE),
    THUNDERBALL (10, 0, 1, 0, false, CollisionType.PLAYERPROJECTILE),
    HYDROBALL (4, 1, 0, 2, true, CollisionType.PLAYERPROJECTILE),
    FURYBALL (10, 1, 0, 4, false, CollisionType.ENEMYPROJECTILE);

    public final int speed;
    public final int penetration;
    public final int etherealness;
    public final int collisions;
    public final boolean bounce;
    public final CollisionType collisionType;

    private ProjectileType(int speed, int penetration, int etherealness, int collisions, boolean bounce, CollisionType collisionType) {
        this.speed = speed;
        this.penetration = penetration;
        this.etherealness = etherealness;
        this.collisions = collisions;
        this.bounce = bounce;
        this.collisionType = collisionType;
    }
}
