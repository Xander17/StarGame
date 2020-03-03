package com.star.app.game.ships.parts;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.star.app.game.GameController;
import com.star.app.game.ships.Ship;

public class Gun {
    private Vector2 position;
    private float velocity;
    private int groupIndex;
    private float angleOffset;
    private float groupDamage;
    private float countInGroup;

    public Gun(float offsetX, float offsetY, float velocity, float angleOffset) {
        this.position = new Vector2(offsetX, offsetY);
        this.groupIndex = 0;
        this.angleOffset = angleOffset;
        this.velocity = velocity;
        this.groupDamage = 0;
        this.countInGroup = 1;
    }

    public int getGroupIndex() {
        return groupIndex;
    }

    public void setGroupIndex(int groupIndex) {
        this.groupIndex = groupIndex;
    }

    public void setGroupDamage(float groupDamage, int countInGroup) {
        this.groupDamage = groupDamage;
        this.countInGroup = countInGroup;
    }

    public void updateGroupDamage(float amount) {
        this.groupDamage += amount;
    }

    public void fire(GameController gameController, Ship ship, boolean playerIsOwner) {
        float shipAngle = ship.getAngle();
        gameController.getBulletController().createNew(ship.getPosition().x + position.x * MathUtils.cosDeg(shipAngle) - position.y * MathUtils.sinDeg(shipAngle),
                ship.getPosition().y + position.y * MathUtils.cosDeg(shipAngle) + position.x * MathUtils.sinDeg(shipAngle),
                ship.getAngle() + angleOffset,
                MathUtils.cosDeg(ship.getAngle() + angleOffset) * velocity + ship.getVelocity().x,
                MathUtils.sinDeg(ship.getAngle() + angleOffset) * velocity + ship.getVelocity().y,
                groupDamage / countInGroup, playerIsOwner);
    }
}
