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
import com.star.app.game.helpers.RenderPosition;
import com.star.app.game.particles.ParticleLayouts;
import com.star.app.game.pilots.PlayerStatistic;

import static com.star.app.screen.ScreenManager.*;

public class Ship implements Collisional {
    private final float COLLISION_BREAK_FACTOR = 0.5f;
    private final float INVULNERABILITY_TIME = 3f;
    private final float SCAN_DISTANCE = 2000f;

    private final float FORWARD_POWER;
    private final float REVERSE_POWER;
    private final float FRICTION_BREAK;

    private TextureRegion texture;
    private int textureW;
    private int textureH;
    private Vector2 massCenter;
    private Vector2[] exhaustPoints;
    private Weapon weapon;

    private GameController gameController;
    private Piloting pilot;
    private Vector2 position;
    private RenderPosition renderPosition;
    private Vector2 velocity;
    private Circle hitBox;
    private float maxDurability;
    private float durability;
    private float forwardMaxSpeed;
    private float rotationSpeed;
    private boolean shipDestroyed;
    private float angle;
    private float invulnerabilityTime;

    public Vector2 getVelocity() {
        return velocity;
    }

    public boolean isShipDestroyed() {
        return shipDestroyed;
    }

    Ship(GameController gameController, Piloting pilot, float durability, float forwardMaxSpeed,
         float FORWARD_POWER, float REVERSE_POWER, float FRICTION_BREAK, float rotationSpeed) {
        this.gameController = gameController;
        this.pilot = pilot;
        this.position = new Vector2(gameController.SPACE_WIDTH / 2f, gameController.SPACE_HEIGHT / 2f);
        this.renderPosition = new RenderPosition(position);
        this.velocity = new Vector2(0, 0);
        this.angle = 0.0f;
        this.massCenter = new Vector2(0, 0);
        this.hitBox = new Circle();
        this.maxDurability = durability;
        this.durability = maxDurability;
        this.shipDestroyed = false;
        this.forwardMaxSpeed = forwardMaxSpeed;
        this.FORWARD_POWER = FORWARD_POWER;
        this.REVERSE_POWER = REVERSE_POWER;
        this.FRICTION_BREAK = FRICTION_BREAK;
        this.rotationSpeed = rotationSpeed;
    }

    public void setTextureSettings(TextureRegion texture, float massCenterX, float massCenterY, Vector2[] exhaustPoints) {
        this.texture = texture;
        this.textureW = texture.getRegionWidth();
        this.textureH = texture.getRegionHeight();
        this.massCenter.set(massCenterX, massCenterY);
        this.exhaustPoints = exhaustPoints;
    }

    public void setRandomState() {
        this.position.set(gameController.getRandomStartPoint(textureW / 2f, textureH / 2f));
        this.angle = MathUtils.random(0, 359);
    }

    public void updatePlayer(float dt) {
        update(dt);
        if (invulnerabilityTime > 0) invulnerabilityTime -= dt;
    }

    public void updateEnemy(float dt) {
        update(dt);
        float[] centerCoords = getTextureCenterShipCS();
        renderPosition.recalculate(gameController, textureW / 2f, textureH / 2f, centerCoords[0], centerCoords[1]);
    }

    private void update(float dt) {
        if (!pilot.control(dt)) frictionBreak(dt);
        position.mulAdd(velocity, dt);
        weapon.update(dt);
        gameController.seamlessTranslate(position);
        updateHitBox();
    }

    public void renderPlayer(SpriteBatch batch) {
        if (shipDestroyed) return;
        if (invulnerabilityTime > 0) batch.setColor(1, 1, 1, 0.6f);
        render(batch, position);
        if (invulnerabilityTime > 0) batch.setColor(1, 1, 1, 1);
    }

    public void renderEnemy(SpriteBatch batch) {
        if (!renderPosition.isRenderable()) return;
        render(batch, renderPosition);
    }

    private void render(SpriteBatch batch, Vector2 position) {
        batch.draw(texture, position.x - massCenter.x, position.y - massCenter.y,
                massCenter.x, massCenter.y, textureW, textureH, 1, 1, angle);
    }

    public void fire(boolean playerIsOwner) {
        weapon.fire(playerIsOwner);
    }

    public void turnLeft(float dt) {
        turn(dt, 1, -1);
    }

    public void turnLeft(float dt, float maxAngle) {
        turn(dt, 1, maxAngle);
    }

    public void turnRight(float dt) {
        turn(dt, -1, -1);
    }

    public void turnRight(float dt, float maxAngle) {
        turn(dt, -1, maxAngle);
    }

    private void turn(float dt, int direction, float maxAngle) {
        if (maxAngle == 0) return;
        else if (maxAngle > 0 && rotationSpeed * dt > maxAngle) angle += direction * maxAngle;
        else angle += direction * rotationSpeed * dt;
        if (direction > 0 && angle >= 360) angle %= 360;
        else if (direction < 0 && angle < 0) angle += angle % 360 + 360;
    }

    public void moveForward(float dt) {
        float directionX = MathUtils.cosDeg(angle);
        float directionY = MathUtils.sinDeg(angle);
        boolean isForwardMoving = velocity.dot(directionX, directionY) >= 0;
        velocity.add(directionX * FORWARD_POWER * dt, directionY * FORWARD_POWER * dt);
        if (velocity.len() > forwardMaxSpeed && isForwardMoving)
            velocity.nor().scl(forwardMaxSpeed);
        makeAccelerationParticles();
    }

    public void reverse(float dt) {
        velocity.sub(REVERSE_POWER * MathUtils.cosDeg(angle) * dt, REVERSE_POWER * MathUtils.sinDeg(angle) * dt);
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
            gameController.getParticleController().getEffectBuilder().exhaust(ParticleLayouts.SHIP,
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

    private float[] getTextureCenterShipCS() {
        return new float[]{textureW / 2f - massCenter.x, textureH / 2f - massCenter.y};
    }

    public float[] getTextureCenterRealCS() {
        float[] center = getTextureCenterShipCS();
        float offsetX = getOffsetX(center);
        float offsetY = getOffsetY(center);
        return new float[]{position.x + offsetX, position.y + offsetY};
    }

    private void updateHitBox() {
        float[] coords = getTextureCenterShipCS();
        hitBox.set(position.x + coords[0], position.y + coords[1], textureH / 2f);
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

    public void setWeapon(Weapon weapon) {
        this.weapon = weapon;
    }

    public Weapon getWeapon() {
        return weapon;
    }

    public boolean checkCollision(Collisional obj, float dt) {
        updateHitBox();
        Circle objHitBox = obj.getHitBox();
        if (!objHitBox.overlaps(hitBox)) return false;
        Vector2 objVelocity = obj.getVelocity();
        Vector2 objPosition = obj.getPosition();
        float[] objVisibleCoords = renderPosition.recalculate(gameController, obj.getPosition(), textureW / 2f, textureH / 2f);
        float collisionDistance = renderPosition.dst(objVisibleCoords[0], objVisibleCoords[1]);
        float collisionAngle = (float) Math.toDegrees(Math.atan2(objVisibleCoords[1] - renderPosition.y, objVisibleCoords[0] - renderPosition.x));
        if (collisionAngle < 0) collisionAngle += 360;
        if (collisionDistance < hitBox.radius + objHitBox.radius) {
            float offset = (hitBox.radius + objHitBox.radius - collisionDistance) / 2;
            float offsetX = offset * MathUtils.cosDeg(collisionAngle) + 1;
            float offsetY = offset * MathUtils.sinDeg(collisionAngle) + 1;
            position.sub(offsetX, offsetY);
            objPosition.add(offsetX, offsetY);
            float[] centerCoords = getTextureCenterShipCS();
            renderPosition.recalculate(gameController, textureW / 2f, textureH / 2f, centerCoords[0], centerCoords[1]);
        }

//        float v1 = velocity.len();
//        float v2 = objVelocity.len();
//        float angle1 = velocity.angle();
//        float angle2 = objPosition.angle();
//        Gdx.app.log("colA",collisionAngle+"");
//        Gdx.app.log("v",v1+" "+angle1);
//        Gdx.app.log("vo",v2+" "+angle2);
//        float[] newV = getNewVelocity(v1, v2, angle1, angle2, collisionAngle, 1, obj.getMassFactor() );
//        velocity.set(newV[0], newV[1]);
//        newV = getNewVelocity(v2, v1, angle2, angle1, collisionAngle + 180, obj.getMassFactor() , 1);
//        objVelocity.set(newV[0], newV[1]);
//        Gdx.app.log("v",velocity.len()+" "+velocity.angle());
//        Gdx.app.log("vo",objVelocity.len()+" "+objVelocity.angle());

        boolean velocityAndAngle = velocity.x * (objVisibleCoords[0] - position.x) + velocity.y * (objVisibleCoords[1] - position.y) > 0;
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

    private float[] getNewVelocity(float v1, float v2, float angle1, float angle2, float collisionAngle, float m1, float m2) {
        if (collisionAngle > 360) collisionAngle -= 360;
        float[] result = new float[2];
        float a = (v1 * MathUtils.cosDeg(angle1 - collisionAngle) * (m1 - m2) + 2 * m2 * v2 * MathUtils.cosDeg(angle2 - collisionAngle)) / (m1 + m2);
        float b = v1 * MathUtils.sinDeg(angle1 - collisionAngle);
        result[0] = a * MathUtils.cosDeg(collisionAngle) + b * MathUtils.cosDeg(collisionAngle + 90);
        result[1] = a * MathUtils.sinDeg(collisionAngle) + b * MathUtils.sinDeg(collisionAngle + 90);
        return result;
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

    @Override
    public float getMassFactor() {
        return 1;
    }

    @Override
    public void destroy() {
        durability = 0;
        shipDestroyed = true;
        pilot.setDeadStatus(true);
    }

    @Override
    public Circle getHitBox() {
        return hitBox;
    }

    @Override
    public boolean takeDamage(float amount) {
        if (invulnerabilityTime > 0) return false;
        gameController.getPlayer().getPlayerStatistic().add(PlayerStatistic.Stats.DAMAGE_TAKEN, amount);
        durability -= amount;
        if (durability <= 0) {
            destroy();
            return true;
        }
        return false;
    }

    public void checkDropItem(Drop drop) {
        if (hitBox.overlaps(drop.getHitBox())) drop.consume();
    }

    public void resetInvulnerability() {
        invulnerabilityTime = INVULNERABILITY_TIME;
    }

    public void setVelocity(float x, float y) {
        this.velocity.set(x, y);
    }

    public float getMaxDurability() {
        return maxDurability;
    }

    public void updateMaxDurability(int amount) {
        this.maxDurability += amount;
        this.durability += amount;
    }

    public void updateRotationSpeed(int amount) {
        this.rotationSpeed += amount;
    }

    public void updateForwardMaxSpeed(int amount) {
        this.forwardMaxSpeed += amount;
    }

    public float getSCAN_DISTANCE() {
        return SCAN_DISTANCE;
    }

    public RenderPosition getRenderPosition() {
        return renderPosition;
    }
}
