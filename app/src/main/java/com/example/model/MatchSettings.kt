package com.example.model

data class MatchSettings(
    val maxRounds: Int = 10,
    val isUnlimitedRounds: Boolean = false,
    val guardRounds: Int = 3,
    val maxAttacksPerTurn: Int = 3,
    val hpMultiplier: Float = 1.0f,
    val ultimateRequiredShots: Int = 1,
    val allowStrategyMinigame: Boolean = true
) {
    val maxRoundsDisplay: String
        get() = if (isUnlimitedRounds || maxRounds >= 99) "Sin Límite" else "$maxRounds"

    val hpMultiplierDisplay: String
        get() = when (hpMultiplier) {
            0.75f -> "75% (Rápida)"
            1.25f -> "125% (Épica)"
            1.50f -> "150% (Tanques)"
            else -> "100% (Estándar)"
        }

    val ultimateRequiredShotsDisplay: String
        get() = when (ultimateRequiredShots) {
            0 -> "Inmediata (0 tiros)"
            2 -> "2 Tiros Previos"
            else -> "1 Tiro Previo (Estándar)"
        }

    companion object {
        val DEFAULT = MatchSettings()
    }
}
