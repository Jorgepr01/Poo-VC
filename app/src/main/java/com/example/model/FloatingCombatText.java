package com.example.model;

import java.util.Objects;

public class FloatingCombatText {
    private final long id;
    private final String targetUnitId;
    private final String text;
    private final boolean isCritical;
    private final boolean isHeal;

    public FloatingCombatText(long id, String targetUnitId, String text, boolean isCritical, boolean isHeal) {
        this.id = id;
        this.targetUnitId = targetUnitId;
        this.text = text;
        this.isCritical = isCritical;
        this.isHeal = isHeal;
    }

    public FloatingCombatText(long id, String targetUnitId, String text, boolean isCritical) {
        this(id, targetUnitId, text, isCritical, false);
    }

    public long getId() {
        return id;
    }

    public String getTargetUnitId() {
        return targetUnitId;
    }

    public String getText() {
        return text;
    }

    public boolean isCritical() {
        return isCritical;
    }

    public boolean isHeal() {
        return isHeal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FloatingCombatText that = (FloatingCombatText) o;
        return id == that.id &&
                isCritical == that.isCritical &&
                isHeal == that.isHeal &&
                Objects.equals(targetUnitId, that.targetUnitId) &&
                Objects.equals(text, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, targetUnitId, text, isCritical, isHeal);
    }
}
