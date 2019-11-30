package com.star.app.game.ships;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.star.app.game.GameController;
import com.star.app.game.drops.Drop;
import com.star.app.game.helpers.Collisional;
import com.star.app.game.helpers.Piloting;
import com.star.app.game.pilots.PlayerStatistic;

import static com.star.app.screen.ScreenManager.SCREEN_HEIGHT;
import static com.star.app.screen.ScreenManager.SCREEN_WIDTH;

public class Ship {
    private final float BOUND_BREAK_FACTOR = 0.5f;
    private final float COLLISION_BREAK_FACTOR = 0.5f;

    private final float FORWARD_SPEED_MAX;
    private final float BACKWARD_SPEED_MAX;
    private final float FORWARD_POWER;
    private final float BACKWARD_POWER;
    private final float FRICTION_BREAK;
    private final float ROTATE_SPEED;

    TextureRegion texture;
    int textureW;
    int textureH;
    Vector2 massCenter;
    Vector2[] exhaustPoints;
    Weapon weapon;

    private GameController gameController;
    private Piloting pilot;
    private Vector2 position;
    private Vector2 velocity;
    private Circle hitBox;
    private float maxDurability;
    private float durability;
    private boolean shipDestoyed;
    private float angle;

    public Vector2 getVelocity() {
        return velocity;
    }

    public boolean isShipDestoyed() {
        return shipDestoyed;
    }

    Ship(GameController gameController, Piloting pilot, float durability, float FORWARD_SPEED_MAX, float BACKWARD_SPEED_MAX,
         float FORWARD_POWER, float BACKWARD_POWER, float FRICTION_BREAK, float ROTATE_SPEED) {
        this.gameController = gameController;
        this.pilot = pilot;
        this.position = new Vector2(SCREEN_WIDTH / 2f, SCREEN_HEIGHT / 2f);
        this.velocity = new Vector2(0, 0);
        this.angle = 0.0f;
        this.massCenter = new Vector2(0, 0);
        this.hitBox = new Circle();
        this.maxDurability = durability;
        this.durability = durability;
        this.shipDestoyed = false;
        this.FORWARD_SPEED_MAX = FORWARD_SPEED_MAX;
        this.BACKWARD_SPEED_MAX = BACKWARD_SPEED_MAX;
        this.FORWARD_POWER = FORWARD_POWER;
        this.BACKWARD_POWER = BACKWARD_POWER;
        this.FRICTION_BREAK = FRICTION_BREAK;
        this.ROTATE_SPEED = ROTATE_SPEED;
    }

    public void setTextureSettings(TextureRegion texture, float massCenterX, float massCenterY, Vector2[] exhaustPoints) {
        this.texture = texture;
        this.textureW = texture.getRegionWidth();
        this.textureH = texture.getRegionHeight();
        this.massCenter.set(massCenterX, massCenterY);
        this.exhaustPoints = exhaustPoints;
    }

    public void render(SpriteBatch batch) {
        if (shipDestoyed) return;
        batch.draw(texture, position.x - massCenter.x, position.y - massCenter.y,
                massCenter.x, massCenter.y, textureW, textureH, 1, 1, angle);

    }

    public void update(float dt) {
        if (!pilot.control(dt)) frictionBreak(dt);
        position.mulAdd(velocity, dt);
        weapon.update(dt);
        checkBounds();
    }

    public void fire() {
        weapon.fire();
    }

    public void turnLeft(float dt) {
        angle += ROTATE_SPEED * dt;
        if (angle >= 360) angle %= 360;
    }

    public void turnRight(float dt) {
        angle -= ROTATE_SPEED * dt;
        if (angle < 0) angle = angle % 360 + 360;
    }

    public void moveForward(float dt) {
        float directionX = MathUtils.cosDeg(angle);
        float directionY = MathUtils.sinDeg(angle);
        boolean isForwardMoving = velocity.dot(directionX, directionY) >= 0;
        velocity.add(directionX * FORWARD_POWER * dt, directionY * FORWARD_POWER * dt);
        if (velocity.len() > FORWARD_SPEED_MAX && isForwardMoving)
            velocity.nor().scl(FORWARD_SPEED_MAX);
        makeAccelerationParticles();
    }

    public void moveBack(float dt) {
        float directionX = MathUtils.cosDeg(angle);
        float directionY = MathUtils.sinDeg(angle);
        boolean isForwardMoving = velocity.dot(directionX, directionY) >= 0;
        velocity.sub(directionX * BACKWARD_POWER * dt, directionY * BACKWARD_POWER * dt);
        if (velocity.len() > BACKWARD_SPEED_MAX && !isForwardMoving)
            velocity.nor().scl(BACKWARD_SPEED_MAX);
    }

    private void frictionBreak(float dt) {
        if (velocity.len() < FRICTION_BREAK * dt) velocity.set(0, 0);
        else {
            float skl = FRICTION_BREAK * dt / velocity.len();
            velocity.mulAdd(velocity, -skl);
        }
    }

    private void makeAccelerationParticles() {
        for (int i = 0; i < exhaustPoints.length; i++) {
            gameController.getParticleController().getEffectBuilder().exhaust(
                    position.x + getOffsetX(exhaustPoints[i].x, exhaustPoints[i].y),
                    position.y + getOffsetY(exhaustPoints[i].x, exhaustPoints[i].y),
                    velocity, angle, 8, 1f, 0.9f, 0.5f
            );
        }
    }

    private float getOffsetX(float... coords) {
        return MathUtils.cosDeg(angle) * coords[0] - MathUtils.sinDeg(angle) * coords[1];
    }

    private float getOffsetY(float... coords) {
        return MathUtils.sinDeg(angle) * coords[0] + MathUtils.cosDeg(angle) * coords[1];
    }

    private float[] getTextureCenter() {
        return new float[]{textureW / 2f - massCenter.x, textureH / 2f - massCenter.y};
    }

    private void checkBounds() {
        float offsetX = getOffsetX(getTextureCenter());
        float offsetY = getOffsetY(getTextureCenter());
        if (position.x + offsetX < textureW / 2f) {
            position.x = textureW / 2f - offsetX;
            velocity.x *= -BOUND_BREAK_FACTOR;
        } else if (position.x + offsetX > SCREEN_WIDTH - textureW / 2f) {
            position.x = SCREEN_WIDTH - textureW / 2f - offsetX;
            velocity.x *= -BOUND_BREAK_FACTOR;
        }
        if (position.y + offsetY < textureH / 2f) {
            position.y = textureH / 2f - offsetY;
            velocity.y *= -BOUND_BREAK_FACTOR;
        } else if (position.y + offsetY > SCREEN_HEIGHT - textureH / 2f) {
            position.y = SCREEN_HEIGHT - textureH / 2f - offsetY;
            velocity.y *= -BOUND_BREAK_FACTOR;
        }
    }

    public Circle getHitBox() {
        float[] coords = getTextureCenter();
        hitBox.set(position.x + coords[0], position.y + coords[1], textureH / 2f);
        return hitBox;
    }

    public float getDurability() {
        return durability;
    }

    public void addDurability(float amount) {
        durability += amount;
        if (durability > maxDurability) durability = maxDurability;
    }

    public Vector2 getPosition() {
        return position;
    }

    public float getAngle() {
        return angle;
    }

    public Weapon getWeapon() {
        return weapon;
    }

    public boolean checkCollision(Collisional obj, float dt) {
        getHitBox();
        Circle objHitBox = obj.getHitBox();
        if (!objHitBox.overlaps(hitBox)) return false;
        Vector2 objVelocity = obj.getVelocity();
        Vector2 objPosition = obj.getPosition();
        float collisionDistance = position.dst(objPosition);
        float collisionAngle = (float) Math.toDegrees(Math.atan2(objPosition.y - position.y, objPosition.x - position.x));
        if (collisionAngle < 0) collisionAngle += 360;
        if (collisionDistance < hitBox.radius + objHitBox.radius) {
            float offset = (hitBox.radius + objHitBox.radius - collisionDistance) / 2;
            float offsetX = offset * MathUtils.cosDeg(collisionAngle) + 1;
            float offsetY = offset * MathUtils.sinDeg(collisionAngle) + 1;
            position.sub(offsetX, offsetY);
            objPosition.add(offsetX, offsetY);
        }

        boolean velocityAndAngle = velocity.x * (objPosition.x - position.x) + velocity.y * (objPosition.y - position.y) > 0;
        if (velocity.dot(objVelocity) < 0 && velocityAndAngle) {
            headOnCollision(objVelocity, obj.getMassFactor());
        } else if (velocityAndAngle) {
            oneWayCollision(velocity, objVelocity, 1, obj.getMassFactor());
        } else {
            oneWayCollision(objVelocity, velocity, obj.getMassFactor(), 1);
        }
        takeDamage(obj.getMassFactor());
        obj.takeDamage(velocity.len() * dt);
        return true;
    }

    private void takeDamage(float amount) {
        gameController.getPlayer().getPlayerStatistic().add(PlayerStatistic.Stats.DAMAGE_TAKEN, amount);
        durability -= amount;
        if (durability <= 0) {
            durability = 0;
            shipDestoyed = true;
            pilot.setDeadStatus(true);
        }
    }

    private void headOnCollision(Vector2 objVelocity, float massFactor) {
        float playerV = velocity.len();
        float objV = objVelocity.len();
        if (playerV > objV) {
            objVelocity.mulAdd(velocity, 1 / massFactor);
            velocity.scl(-COLLISION_BREAK_FACTOR);
        } else {
            objVelocity.scl(-COLLISION_BREAK_FACTOR / massFactor);
            velocity.add(objVelocity);
        }
    }

    private void oneWayCollision(Vector2 behindVelocity, Vector2 aheadVelocity, float behindMassFactor, float aheadMassFactor) {
        float behindV = behindVelocity.len();
        float aheadV = aheadVelocity.len();
        float max = Math.max(behindV, aheadV);
        aheadVelocity.mulAdd(behindVelocity, 1 / aheadMassFactor);
        if (aheadV > max) aheadVelocity.scl(max / aheadV);
        behindVelocity.scl(-COLLISION_BREAK_FACTOR / behindMassFactor);
    }

    public void checkDropItem(Drop drop) {
        if (hitBox.overlaps(drop.getHitBox())) drop.consume();
    }
}
