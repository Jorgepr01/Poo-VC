package com.example.engine;

import com.example.model.BattleUnit;
import com.example.model.HeroRole;

import java.util.ArrayList;
import java.util.List;

public final class BattleRules {

    public static final int MAX_ATTACKS_PER_TURN = 3;

    private BattleRules() {}

    /**
     * Checks if a target unit can be attacked given the battlefield state.
     * Rule:
     * - Must be alive.
     * - If Guerreros: always targetable (frontline).
     * - If Místicos or Magos: targetable ONLY IF their team has no living Guerreros.
     */
    public static boolean isUnitTargetable(BattleUnit targetUnit, List<BattleUnit> allUnits) {
        if (targetUnit == null || !targetUnit.isAlive()) {
            return false;
        }

        if (targetUnit.getHero().getRole() == HeroRole.GUERRERO) {
            return true;
        }

        boolean hasLivingWarriors = false;
        for (BattleUnit unit : allUnits) {
            if (unit.getPlayerId() == targetUnit.getPlayerId()
                    && unit.getHero().getRole() == HeroRole.GUERRERO
                    && unit.isAlive()) {
                hasLivingWarriors = true;
                break;
            }
        }
        return !hasLivingWarriors;
    }

    /**
     * Checks if a unit is currently protected by living frontline warriors.
     */
    public static boolean isUnitProtected(BattleUnit unit, List<BattleUnit> allUnits) {
        if (unit == null || !unit.isAlive()) {
            return false;
        }
        if (unit.getHero().getRole() == HeroRole.GUERRERO) {
            return false;
        }
        for (BattleUnit u : allUnits) {
            if (u.getPlayerId() == unit.getPlayerId()
                    && u.getHero().getRole() == HeroRole.GUERRERO
                    && u.isAlive()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Finds all units belonging to a specific player.
     */
    public static List<BattleUnit> getPlayerUnits(List<BattleUnit> allUnits, int playerId) {
        List<BattleUnit> result = new ArrayList<>();
        if (allUnits == null) return result;
        for (BattleUnit u : allUnits) {
            if (u.getPlayerId() == playerId) {
                result.add(u);
            }
        }
        return result;
    }

    /**
     * Finds all alive units for a player.
     */
    public static List<BattleUnit> getLivingPlayerUnits(List<BattleUnit> allUnits, int playerId) {
        List<BattleUnit> result = new ArrayList<>();
        if (allUnits == null) return result;
        for (BattleUnit u : allUnits) {
            if (u.getPlayerId() == playerId && u.isAlive()) {
                result.add(u);
            }
        }
        return result;
    }

    /**
     * Returns true if a player has any living units.
     */
    public static boolean hasLivingUnits(List<BattleUnit> allUnits, int playerId) {
        if (allUnits == null) return false;
        for (BattleUnit u : allUnits) {
            if (u.getPlayerId() == playerId && u.isAlive()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Refreshes all living units for a specific player so they can act on their turn.
     */
    public static List<BattleUnit> refreshPlayerUnits(List<BattleUnit> allUnits, int playerId) {
        List<BattleUnit> result = new ArrayList<>();
        if (allUnits == null) return result;
        for (BattleUnit u : allUnits) {
            if (u.getPlayerId() == playerId) {
                result.add(u.copyWithActed(false));
            } else {
                result.add(u);
            }
        }
        return result;
    }

    /**
     * Refreshes all living units on the battlefield for all players at the start of a new round.
     */
    public static List<BattleUnit> refreshAllUnitsForNewRound(List<BattleUnit> allUnits) {
        List<BattleUnit> result = new ArrayList<>();
        if (allUnits == null) return result;
        for (BattleUnit u : allUnits) {
            result.add(u.copyWithActed(false));
        }
        return result;
    }
}
