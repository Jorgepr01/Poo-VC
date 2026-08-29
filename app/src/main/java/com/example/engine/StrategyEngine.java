package com.example.engine;

import com.example.model.StrategyRollResult;

import java.util.Random;

public final class StrategyEngine {

    private static final Random RANDOM = new Random();

    public static final float BONUS_1D6_WIN = 0.35f;  // +35% Daño para héroes 1d6 (estadística rota)
    public static final float BONUS_2D6_WIN = 0.15f;  // +15% Daño para héroes 2d6 (estratégico estándar)
    public static final float PENALTY_LOSS = -0.05f;  // -5% Defensa

    private StrategyEngine() {}

    /**
     * Evaluates a strategy roll given diceCount (1 or 2), player's selected number and rolled number.
     */
    public static StrategyRollResult evaluateRoll(int diceCount, int chosenNumber, int rolledNumber) {
        boolean isWin = (rolledNumber == chosenNumber) || (rolledNumber % 2 == chosenNumber % 2);
        float bonusPct = isWin ? (diceCount == 1 ? BONUS_1D6_WIN : BONUS_2D6_WIN) : PENALTY_LOSS;
        String message;
        if (isWin) {
            if (diceCount == 1) {
                message = "★ ¡Estrategia ROTA 1d6! (+35% Daño demoledor)";
            } else {
                message = "★ ¡Estrategia Táctica 2d6! (+15% Daño)";
            }
        } else {
            message = "⚡ ¡Intento fallido! Penalización menor (-5% Defensa)";
        }

        return new StrategyRollResult(chosenNumber, rolledNumber, isWin, bonusPct, message, diceCount);
    }

    /**
     * Evaluates a strategy roll with default 2d6.
     */
    public static StrategyRollResult evaluateRoll(int chosenNumber, int rolledNumber) {
        return evaluateRoll(2, chosenNumber, rolledNumber);
    }

    /**
     * Rolls random number with engaging probability weighting based on dice count.
     */
    public static int generateRoll(int diceCount, int chosenNumber) {
        if (RANDOM.nextFloat() < 0.65f) {
            return chosenNumber;
        }
        if (diceCount == 1) {
            return RANDOM.nextInt(6) + 1; // 1..6
        } else {
            return (RANDOM.nextInt(6) + 1) + (RANDOM.nextInt(6) + 1); // 2..12
        }
    }

    /**
     * Rolls random number with default 2d6.
     */
    public static int generateRoll(int chosenNumber) {
        return generateRoll(2, chosenNumber);
    }
}
