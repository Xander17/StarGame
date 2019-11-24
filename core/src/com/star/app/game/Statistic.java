package com.star.app.game;

public class Statistic {
    private float MAX_SEC_TO_GAIN_SCORE = 3f;
    private int SCORE_GAIN_PER_SEC = 1000;
    private int SCORE_DEAD_PENALTY = 10000;

    private int score;
    private int scoreView;

    public Statistic() {
        score = 0;
        scoreView = 0;
    }

    public void update(float dt) {
        if (score - scoreView > SCORE_GAIN_PER_SEC * MAX_SEC_TO_GAIN_SCORE)
            scoreView += (score - scoreView) / MAX_SEC_TO_GAIN_SCORE * dt;
        else scoreView += dt * SCORE_GAIN_PER_SEC;
        if (scoreView > score) scoreView = score;
    }

    public int getScoreView() {
        return scoreView;
    }

    public int getScore() {
        return score;
    }

    public void addScore(int amount) {
        score += amount;
    }

    public void subScore(int amount) {
        score -= amount;
        if (score < 0) score = 0;
    }

    public void scoreDeadPenalty(){
        subScore(SCORE_DEAD_PENALTY);
    }
}
