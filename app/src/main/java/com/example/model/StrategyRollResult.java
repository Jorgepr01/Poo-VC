package com.example.model;

import java.util.Objects;

public class StrategyRollResult {
    private final int selectedNumber;
    private final int rolledNumber;
    private final boolean isWin;
    private final float bonusPct;
    private final String message;

    public StrategyRollResult(
            int selectedNumber,
            int rolledNumber,
            boolean isWin,
            float bonusPct,
            String message
    ) {
        this.selectedNumber = selectedNumber;
        this.rolledNumber = rolledNumber;
        this.isWin = isWin;
        this.bonusPct = bonusPct;
        this.message = message;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StrategyRollResult that = (StrategyRollResult) o;
        return selectedNumber == that.selectedNumber &&
                rolledNumber == that.rolledNumber &&
                isWin == that.isWin &&
                Float.compare(that.bonusPct, bonusPct) == 0 &&
                Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(selectedNumber, rolledNumber, isWin, bonusPct, message);
    }
}
