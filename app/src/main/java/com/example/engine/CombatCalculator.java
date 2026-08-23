package com.example.engine;

import com.example.model.AttackType;
import com.example.model.BattleUnit;
import com.example.model.HeroRole;

public final class CombatCalculator {

    private CombatCalculator() {}

    /**
     * Calculates net damage dealt by an attacker to a target based on attack type, strategy bonus, and target count.
     * Formula:
     *   spreadFactor = 1.0 (1 target), 0.85 (2 targets), 0.70 (3 targets)
     *   baseDamage = effectiveAttack * (isUltimate ? 1.35 : 1.0) * spreadFactor * (1.0 + strategyBonus)
     *   mitigation = effectiveDefense * 0.45
     *   netDamage = max(10, round(baseDamage - mitigation))
     */
    public static int calculateDamage(
            BattleUnit attacker,
            BattleUnit target,
            AttackType attackType,
            float strategyBonus,
            int targetCount
    ) {
        if (attacker == null || target == null || !attacker.isAlive() || !target.isAlive()) {
            return 0;
        }

        boolean isUltimate = (attackType == AttackType.ULTIMATE);
        // Strategy bonus ONLY applies to basic attacks, strictly 0 for ultimate
        float appliedStrategy = isUltimate ? 0f : Math.max(-0.5f, strategyBonus);

        float spreadFactor;
        if (targetCount <= 1) {
            spreadFactor = 1.0f;
        } else if (targetCount == 2) {
            spreadFactor = 0.85f;
        } else {
            spreadFactor = 0.70f;
        }

        float typeMultiplier = isUltimate ? 1.35f : 1.0f;
        float baseDamage = attacker.getEffectiveAttack() * typeMultiplier * spreadFactor * (1.0f + appliedStrategy);
        float effectiveDef = target.getEffectiveDefense() * 0.45f;
        int netDamage = Math.max(10, Math.round(baseDamage - effectiveDef));

        return netDamage;
    }

    /**
     * Calculates heal performed by a Mage ally.
     * Formula:
     *   healFactor = 1.0 (1 target), 0.80 (2 targets), 0.65 (3 targets)
     *   baseHeal = (effectiveAttack * 0.60 + 16) * healFactor * (1.0 + strategyBonus)
     */
    public static int calculateHeal(
            BattleUnit caster,
            int targetCount,
            float strategyBonus,
            boolean isUltimate
    ) {
        if (caster == null || !caster.isAlive() || caster.getHero().getRole() != HeroRole.MAGO) {
            return 0;
        }

        float healFactor;
        if (targetCount <= 1) {
            healFactor = 1.0f;
        } else if (targetCount == 2) {
            healFactor = 0.80f;
        } else {
            healFactor = 0.65f;
        }

        float appliedStrategy = isUltimate ? 0f : Math.max(0f, strategyBonus);
        float baseHeal = ((caster.getEffectiveAttack() * 0.60f + 16f) * healFactor * (1.0f + appliedStrategy));
        return Math.max(12, Math.round(baseHeal));
    }
}
