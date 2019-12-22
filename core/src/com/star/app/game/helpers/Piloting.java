package com.star.app.game.helpers;

public interface Piloting {
    boolean control(float dt);

    void setDeadStatus(boolean status);
}
