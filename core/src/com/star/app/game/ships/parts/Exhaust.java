package com.star.app.game.ships.parts;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.star.app.game.GameController;
import com.star.app.game.particles.ParticleLayouts;

public class Exhaust {

    private GameController gameController;
    private Vector2 position;
    private Flags flag;
    private int currentPower;
    public Exhaust(GameController gameController, float offsetX, float offsetY, Flags flag) {
        this.gameController = gameController;
        this.position = new Vector2(offsetX, offsetY);
        this.flag = flag;
        currentPower = 0;
    }

    public void exhaust(Vector2 shipPos, Vector2 velocity, float angle) {
        if (currentPower == 0) return;
        float offsetX = MathUtils.cosDeg(angle) * position.x - MathUtils.sinDeg(angle) * position.y;
        float offsetY = MathUtils.sinDeg(angle) * position.x + MathUtils.cosDeg(angle) * position.y;
        gameController.getParticleController().getEffectBuilder().exhaust(ParticleLayouts.SHIP,
                shipPos.x + offsetX, shipPos.y + offsetY,
                velocity, angle, 4 * (1 + currentPower), 1f, 0.9f, 0.5f
        );
        currentPower = 0;
    }

    public void increasePower(Flags flag) {
        if (isTrueExhaust(flag)) currentPower++;
    }

    private boolean isTrueExhaust(Flags flag) {
        switch (flag) {
            case THRUST:
                if (this.flag == Flags.THRUST || this.flag == Flags.RIGHT_THRUST || this.flag == Flags.LEFT_THRUST)
                    return true;
                break;
            case LEFT_TURN:
                if (this.flag == Flags.LEFT_THRUST || this.flag == Flags.LEFT_TURN) return true;
                break;
            case RIGHT_TURN:
                if (this.flag == Flags.RIGHT_THRUST || this.flag == Flags.RIGHT_TURN) return true;
                break;
        }
        return false;
    }

    public enum Flags {LEFT_TURN, LEFT_THRUST, THRUST, RIGHT_THRUST, RIGHT_TURN}
}
