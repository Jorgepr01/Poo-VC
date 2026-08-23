package com.example.engine;

import com.example.model.AttackType;
import com.example.model.BattleUnit;
import com.example.model.HeroRole;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class CombatAiLogic {

    public static class AiActionDecision {
        private final BattleUnit attacker;
        private final boolean isHeal;
        private final AttackType attackType;
        private final List<String> targetIds;

        public AiActionDecision(
                BattleUnit attacker,
                boolean isHeal,
                AttackType attackType,
                List<String> targetIds
        ) {
            this.attacker = attacker;
            this.isHeal = isHeal;
            this.attackType = attackType;
            this.targetIds = targetIds;
        }

        public BattleUnit getAttacker() {
            return attacker;
        }

        public boolean isHeal() {
            return isHeal;
        }

        public AttackType getAttackType() {
            return attackType;
        }

        public List<String> getTargetIds() {
            return targetIds;
        }
    }

    private CombatAiLogic() {}

    /**
     * Determines AI's tactical move: attacker, target selection, attack/heal mode.
     */
    public static AiActionDecision decideAction(
            int aiPlayerId,
            List<BattleUnit> allUnits,
            int currentRound
    ) {
        List<BattleUnit> aiUnits = BattleRules.getLivingPlayerUnits(allUnits, aiPlayerId);
        List<BattleUnit> availableAttackers = new ArrayList<>();
        for (BattleUnit u : aiUnits) {
            if (!u.isHasActed()) {
                availableAttackers.add(u);
            }
        }

        if (availableAttackers.isEmpty()) {
            return null;
        }

        // Check if any ally is critically injured (< 40% HP)
        BattleUnit criticallyInjuredAlly = null;
        for (BattleUnit ally : aiUnits) {
            if (ally.getHpPercent() < 0.40f) {
                if (criticallyInjuredAlly == null || ally.getHpPercent() < criticallyInjuredAlly.getHpPercent()) {
                    criticallyInjuredAlly = ally;
                }
            }
        }

        // If an ally is critical and AI has an available Mage, prioritize healing
        if (criticallyInjuredAlly != null) {
            for (BattleUnit unit : availableAttackers) {
                if (unit.getHero().getRole() == HeroRole.MAGO) {
                    List<String> healTarget = new ArrayList<>();
                    healTarget.add(criticallyInjuredAlly.getId());
                    return new AiActionDecision(unit, true, AttackType.BASIC, healTarget);
                }
            }
        }

        // Select the unit with highest effective attack
        availableAttackers.sort(new Comparator<BattleUnit>() {
            @Override
            public int compare(BattleUnit a, BattleUnit b) {
                return Integer.compare(b.getEffectiveAttack(), a.getEffectiveAttack());
            }
        });
        BattleUnit attacker = availableAttackers.get(0);

        // Find all enemy targetable units
        List<BattleUnit> targetableEnemies = new ArrayList<>();
        for (BattleUnit unit : allUnits) {
            if (unit.getPlayerId() != aiPlayerId && unit.isAlive() && BattleRules.isUnitTargetable(unit, allUnits)) {
                targetableEnemies.add(unit);
            }
        }

        if (targetableEnemies.isEmpty()) {
            return null;
        }

        // Target lowest HP enemy to secure eliminations
        targetableEnemies.sort(new Comparator<BattleUnit>() {
            @Override
            public int compare(BattleUnit a, BattleUnit b) {
                return Integer.compare(a.getCurrentHp(), b.getCurrentHp());
            }
        });

        AttackType attackType = AttackType.BASIC;
        if (currentRound >= 3 && Math.random() < 0.4) {
            attackType = AttackType.ULTIMATE;
        }

        List<String> targets = new ArrayList<>();
        int countNeeded = Math.min(attackType.getTargetCount(), targetableEnemies.size());
        for (int i = 0; i < countNeeded; i++) {
            targets.add(targetableEnemies.get(i).getId());
        }

        return new AiActionDecision(attacker, false, attackType, targets);
    }
}
