package com.example.model;

import java.util.Objects;

public class StrategyRollResult {
    private final int selectedNumber;
    private final int rolledNumber;
    private final boolean isWin;
    private final float bonusPct;
    private final String message;
    private final int diceCount;

    public StrategyRollResult(
            int selectedNumber,
            int rolledNumber,
            boolean isWin,
            float bonusPct,
            String message,
            int diceCount
    ) {
        this.selectedNumber = selectedNumber;
        this.rolledNumber = rolledNumber;
        this.isWin = isWin;
        this.bonusPct = bonusPct;
        this.message = message;
        this.diceCount = diceCount <= 1 ? 1 : 2;
    }

    public StrategyRollResult(
            int selectedNumber,
            int rolledNumber,
            boolean isWin,
            float bonusPct,
            String message
    ) {
        this(selectedNumber, rolledNumber, isWin, bonusPct, message, 2);
    }

    public int getSelectedNumber() {
        return selectedNumber;
    }

    public int getRolledNumber() {
        return rolledNumber;
    }

    public boolean isWin() {
        return isWin;
    }

    public float getBonusPct() {
        return bonusPct;
    }

    public String getMessage() {
        return message;
    }

    public int getDiceCount() {
        return diceCount;
    }

    public boolean isBroken() {
        return diceCount == 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StrategyRollResult that = (StrategyRollResult) o;
        return selectedNumber == that.selectedNumber &&
                rolledNumber == that.rolledNumber &&
                isWin == that.isWin &&
                Float.compare(that.bonusPct, bonusPct) == 0 &&
                diceCount == that.diceCount &&
                Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(selectedNumber, rolledNumber, isWin, bonusPct, message, diceCount);
    }
}
