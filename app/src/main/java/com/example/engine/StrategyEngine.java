package com.example.engine;

import com.example.model.StrategyRollResult;

import java.util.Random;

public final class StrategyEngine {

    private static final Random RANDOM = new Random();

    private StrategyEngine() {}

    /**
     * Evaluates a strategy roll given player's selected number and rolled number.
     * Generous win conditions (matching wireframe and tactical flow):
     * Win if rolled number equals chosen or same parity (even/odd), with higher probability for engagement.
     */
    public static StrategyRollResult evaluateRoll(int chosenNumber, int rolledNumber) {
        boolean isWin = (rolledNumber == chosenNumber) || (rolledNumber % 2 == chosenNumber % 2);
        float bonusPct = isWin ? 0.15f : -0.05f;
        String message = isWin
                ? "¡Perfecto! Ganaste un bono (+15% Ataque)"
                : "¡Intento fallido! Penalización menor (-5% Defensa)";

        return new StrategyRollResult(chosenNumber, rolledNumber, isWin, bonusPct, message);
    }

    /**
     * Rolls random number with engaging probability weighting.
     */
    public static int generateRoll(int chosenNumber) {
        if (RANDOM.nextFloat() < 0.65f) {
            return chosenNumber;
        }
        return RANDOM.nextInt(6) + 1;
    }
}
