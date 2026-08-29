package com.example.model;

import java.util.Objects;

public class BattleUnit {
    private final String id;
    private final int playerId;
    private final int slotIndex;
    private final Hero hero;
    private final int currentHp;
    private final int maxHp;
    private final boolean isAlive;
    private final boolean hasActed;
    private final float attackBuffPct;
    private final float defenseBuffPct;
    private final String rowLabel;
    private final int shotsPerformed;
    private final int guardRoundsRemaining;
    private final int buffRoundsRemaining;

    public BattleUnit(
            String id,
            int playerId,
            int slotIndex,
            Hero hero,
            int currentHp,
            int maxHp,
            boolean isAlive,
            boolean hasActed,
            float attackBuffPct,
            float defenseBuffPct,
            String rowLabel,
            int shotsPerformed,
            int guardRoundsRemaining,
            int buffRoundsRemaining
    ) {
        this.id = id;
        this.playerId = playerId;
        this.slotIndex = slotIndex;
        this.hero = hero;
        this.currentHp = Math.max(0, currentHp);
        this.maxHp = maxHp;
        this.isAlive = isAlive && this.currentHp > 0;
        this.hasActed = hasActed;
        this.attackBuffPct = attackBuffPct;
        this.defenseBuffPct = defenseBuffPct;
        this.rowLabel = rowLabel;
        this.shotsPerformed = Math.max(0, shotsPerformed);
        this.guardRoundsRemaining = Math.max(0, guardRoundsRemaining);
        this.buffRoundsRemaining = Math.max(0, buffRoundsRemaining);
    }

    public BattleUnit(
            String id,
            int playerId,
            int slotIndex,
            Hero hero,
            int currentHp,
            int maxHp,
            boolean isAlive,
            boolean hasActed,
            float attackBuffPct,
            float defenseBuffPct,
            String rowLabel,
            int shotsPerformed,
            int guardRoundsRemaining
    ) {
        this(
                id,
                playerId,
                slotIndex,
                hero,
                currentHp,
                maxHp,
                isAlive,
                hasActed,
                attackBuffPct,
                defenseBuffPct,
                rowLabel,
                shotsPerformed,
                guardRoundsRemaining,
                (attackBuffPct != 0f || defenseBuffPct != 0f) ? 1 : 0
        );
    }

    public BattleUnit(
            String id,
            int playerId,
            int slotIndex,
            Hero hero,
            int currentHp,
            int maxHp,
            boolean isAlive,
            boolean hasActed,
            float attackBuffPct,
            float defenseBuffPct,
            String rowLabel
    ) {
        this(
                id,
                playerId,
                slotIndex,
                hero,
                currentHp,
                maxHp,
                isAlive,
                hasActed,
                attackBuffPct,
                defenseBuffPct,
                rowLabel,
                0,
                hero.getRole() == HeroRole.GUERRERO ? 3 : 0
        );
    }

    public BattleUnit(
            String id,
            int playerId,
            int slotIndex,
            Hero hero,
            String rowLabel
    ) {
        this(
                id,
                playerId,
                slotIndex,
                hero,
                hero.getHp(),
                hero.getHp(),
                true,
                false,
                0f,
                0f,
                rowLabel,
                0,
                hero.getRole() == HeroRole.GUERRERO ? 3 : 0
        );
    }

    public String getId() {
        return id;
    }

    public int getPlayerId() {
        return playerId;
    }

    public int getSlotIndex() {
        return slotIndex;
    }

    public Hero getHero() {
        return hero;
    }

    public int getCurrentHp() {
        return currentHp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public boolean isHasActed() {
        return hasActed;
    }

    public boolean getHasActed() {
        return hasActed;
    }

    public float getAttackBuffPct() {
        return attackBuffPct;
    }

    public float getDefenseBuffPct() {
        return defenseBuffPct;
    }

    public String getRowLabel() {
        return rowLabel;
    }

    public int getEffectiveAttack() {
        float multiplier = 1.0f + attackBuffPct;
        return Math.max(1, Math.round(hero.getAttack() * multiplier));
    }

    public int getEffectiveDefense() {
        float multiplier = 1.0f + defenseBuffPct;
        return Math.max(0, Math.round(hero.getDefense() * multiplier));
    }

    public float getHpPercent() {
        if (maxHp <= 0) return 0f;
        return Math.max(0f, Math.min(1f, (float) currentHp / (float) maxHp));
    }

    public float getHpPercentage() {
        return getHpPercent();
    }

    public int getShotsPerformed() {
        return shotsPerformed;
    }

    public boolean canUseUltimate() {
        return shotsPerformed >= 1;
    }

    public int getGuardRoundsRemaining() {
        return guardRoundsRemaining;
    }

    public boolean isGuardActive() {
        return guardRoundsRemaining > 0;
    }

    public int getBuffRoundsRemaining() {
        return buffRoundsRemaining;
    }

    public boolean hasActiveBuffsOrDebuffs() {
        return buffRoundsRemaining > 0 && (attackBuffPct != 0f || defenseBuffPct != 0f);
    }

    public BattleUnit copy(
            String id,
            Integer playerId,
            Integer slotIndex,
            Hero hero,
            Integer currentHp,
            Integer maxHp,
            Boolean isAlive,
            Boolean hasActed,
            Float attackBuffPct,
            Float defenseBuffPct,
            String rowLabel,
            Integer shotsPerformed,
            Integer guardRoundsRemaining,
            Integer buffRoundsRemaining
    ) {
        return new BattleUnit(
                id != null ? id : this.id,
                playerId != null ? playerId : this.playerId,
                slotIndex != null ? slotIndex : this.slotIndex,
                hero != null ? hero : this.hero,
                currentHp != null ? currentHp : this.currentHp,
                maxHp != null ? maxHp : this.maxHp,
                isAlive != null ? isAlive : (currentHp != null ? currentHp > 0 : this.isAlive),
                hasActed != null ? hasActed : this.hasActed,
                attackBuffPct != null ? attackBuffPct : this.attackBuffPct,
                defenseBuffPct != null ? defenseBuffPct : this.defenseBuffPct,
                rowLabel != null ? rowLabel : this.rowLabel,
                shotsPerformed != null ? shotsPerformed : this.shotsPerformed,
                guardRoundsRemaining != null ? guardRoundsRemaining : this.guardRoundsRemaining,
                buffRoundsRemaining != null ? buffRoundsRemaining : this.buffRoundsRemaining
        );
    }

    public BattleUnit copy(
            String id,
            Integer playerId,
            Integer slotIndex,
            Hero hero,
            Integer currentHp,
            Integer maxHp,
            Boolean isAlive,
            Boolean hasActed,
            Float attackBuffPct,
            Float defenseBuffPct,
            String rowLabel,
            Integer shotsPerformed,
            Integer guardRoundsRemaining
    ) {
        return copy(
                id,
                playerId,
                slotIndex,
                hero,
                currentHp,
                maxHp,
                isAlive,
                hasActed,
                attackBuffPct,
                defenseBuffPct,
                rowLabel,
                shotsPerformed,
                guardRoundsRemaining,
                this.buffRoundsRemaining
        );
    }

    public BattleUnit copy(
            String id,
            Integer playerId,
            Integer slotIndex,
            Hero hero,
            Integer currentHp,
            Integer maxHp,
            Boolean isAlive,
            Boolean hasActed,
            Float attackBuffPct,
            Float defenseBuffPct,
            String rowLabel
    ) {
        return copy(
                id,
                playerId,
                slotIndex,
                hero,
                currentHp,
                maxHp,
                isAlive,
                hasActed,
                attackBuffPct,
                defenseBuffPct,
                rowLabel,
                this.shotsPerformed,
                this.guardRoundsRemaining,
                this.buffRoundsRemaining
        );
    }

    public BattleUnit copyWithHp(int newHp) {
        return copy(null, null, null, null, newHp, null, newHp > 0, null, null, null, null, null, null, null);
    }

    public BattleUnit copyWithActed(boolean acted) {
        return copy(null, null, null, null, null, null, null, acted, null, null, null, null, null, null);
    }

    public BattleUnit copyWithBuffs(float atkBuff, float defBuff, int buffRounds) {
        return copy(null, null, null, null, null, null, null, null, atkBuff, defBuff, null, null, null, buffRounds);
    }

    public BattleUnit copyWithBuffs(float atkBuff, float defBuff) {
        return copyWithBuffs(atkBuff, defBuff, 1);
    }

    public BattleUnit copyWithShots(int shots) {
        return copy(null, null, null, null, null, null, null, null, null, null, null, shots, null, null);
    }

    public BattleUnit copyWithIncrementedShots() {
        return copy(null, null, null, null, null, null, null, null, null, null, null, this.shotsPerformed + 1, null, null);
    }

    public BattleUnit copyWithGuardRounds(int guardRounds) {
        return copy(null, null, null, null, null, null, null, null, null, null, null, null, guardRounds, null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BattleUnit that = (BattleUnit) o;
        return playerId == that.playerId &&
                slotIndex == that.slotIndex &&
                currentHp == that.currentHp &&
                maxHp == that.maxHp &&
                isAlive == that.isAlive &&
                hasActed == that.hasActed &&
                shotsPerformed == that.shotsPerformed &&
                guardRoundsRemaining == that.guardRoundsRemaining &&
                buffRoundsRemaining == that.buffRoundsRemaining &&
                Float.compare(that.attackBuffPct, attackBuffPct) == 0 &&
                Float.compare(that.defenseBuffPct, defenseBuffPct) == 0 &&
                Objects.equals(id, that.id) &&
                Objects.equals(hero, that.hero) &&
                Objects.equals(rowLabel, that.rowLabel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, playerId, slotIndex, hero, currentHp, maxHp, isAlive, hasActed, attackBuffPct, defenseBuffPct, rowLabel, shotsPerformed, guardRoundsRemaining, buffRoundsRemaining);
    }
}
