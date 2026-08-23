package com.example.model

data class BattleUnit(
    val id: String,
    val playerId: Int,
    val slotIndex: Int,
    val hero: Hero,
    val maxHp: Int = hero.hp,
    val currentHp: Int = hero.hp,
    val hasActed: Boolean = false,
    val attackBuffPct: Float = 0f,
    val defenseBuffPct: Float = 0f,
    val isUltimateReady: Boolean = false,
    val rowLabel: String = ""
) {
    val isAlive: Boolean
        get() = currentHp > 0

    val hpPercentage: Float
        get() = if (maxHp > 0) (currentHp.toFloat() / maxHp.toFloat()).coerceIn(0f, 1f) else 0f

    val effectiveAttack: Int
        get() = (hero.attack * (1f + attackBuffPct)).toInt().coerceAtLeast(1)

    val effectiveDefense: Int
        get() = (hero.defense * (1f + defenseBuffPct)).toInt().coerceAtLeast(0)
}

enum class AttackType(val displayName: String) {
    BASIC("Básico"),
    ULTIMATE("Ultimate")
}

data class StrategyRollResult(
    val selectedNumber: Int,
    val rolledNumber: Int,
    val isWin: Boolean,
    val bonusPct: Float,
    val message: String
)

data class FloatingCombatText(
    val id: Long = System.currentTimeMillis(),
    val targetUnitId: String,
    val text: String,
    val isCritical: Boolean = false,
    val isHeal: Boolean = false
)
