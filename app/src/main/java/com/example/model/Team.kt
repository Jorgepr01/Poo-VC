package com.example.model

data class Team(
    val id: String,
    val name: String,
    val isDefault: Boolean = false,
    val guerreros: List<Hero>, // max 4
    val misticos: List<Hero>,  // max 3
    val magos: List<Hero>      // max 2
) {
    companion object {
        const val MAX_TOTAL_HEROES = 6
        const val MAX_GUERREROS = 4
        const val MAX_MISTICOS = 3
        const val MAX_MAGOS = 2
    }

    val totalHeroes: List<Hero>
        get() = guerreros + misticos + magos

    val totalCount: Int
        get() = totalHeroes.size

    val isValidComposition: Boolean
        get() = totalCount in 1..MAX_TOTAL_HEROES &&
                guerreros.size <= MAX_GUERREROS &&
                misticos.size <= MAX_MISTICOS &&
                magos.size <= MAX_MAGOS

    val soldierCountDesc: String
        get() = "${guerreros.size}G · ${misticos.size}M · ${magos.size}A"
}

object DefaultTeams {
    fun createDefaults(): List<Team> {
        return listOf(
            Team(
                id = "team_balanced",
                name = "Nivelado (default)",
                isDefault = true,
                guerreros = listOf(
                    HeroCatalog.GUERREROS[0],
                    HeroCatalog.GUERREROS[1],
                    HeroCatalog.GUERREROS[3]
                ),
                misticos = listOf(
                    HeroCatalog.MISTICOS[0],
                    HeroCatalog.MISTICOS[1]
                ),
                magos = listOf(
                    HeroCatalog.MAGOS[0]
                )
            ),
            Team(
                id = "team_offensive",
                name = "Ofensivo (default)",
                isDefault = true,
                guerreros = listOf(
                    HeroCatalog.GUERREROS[2],
                    HeroCatalog.GUERREROS[0],
                    HeroCatalog.GUERREROS[2]
                ),
                misticos = listOf(
                    HeroCatalog.MISTICOS[2],
                    HeroCatalog.MISTICOS[0]
                ),
                magos = listOf(
                    HeroCatalog.MAGOS[2]
                )
            ),
            Team(
                id = "team_defensive",
                name = "Defensivo (default)",
                isDefault = true,
                guerreros = listOf(
                    HeroCatalog.GUERREROS[1],
                    HeroCatalog.GUERREROS[3],
                    HeroCatalog.GUERREROS[1]
                ),
                misticos = listOf(
                    HeroCatalog.MISTICOS[1],
                    HeroCatalog.MISTICOS[3]
                ),
                magos = listOf(
                    HeroCatalog.MAGOS[1]
                )
            ),
            Team(
                id = "team_arcane",
                name = "Magos Oscuros",
                isDefault = false,
                guerreros = listOf(
                    HeroCatalog.GUERREROS[0],
                    HeroCatalog.GUERREROS[1],
                    HeroCatalog.GUERREROS[3]
                ),
                misticos = listOf(
                    HeroCatalog.MISTICOS[2],
                    HeroCatalog.MISTICOS[3]
                ),
                magos = listOf(
                    HeroCatalog.MAGOS[3]
                )
            ),
            Team(
                id = "team_vanguard",
                name = "Vanguardia de Acero",
                isDefault = false,
                guerreros = listOf(
                    HeroCatalog.GUERREROS[0],
                    HeroCatalog.GUERREROS[2],
                    HeroCatalog.GUERREROS[3]
                ),
                misticos = listOf(
                    HeroCatalog.MISTICOS[0],
                    HeroCatalog.MISTICOS[1]
                ),
                magos = listOf(
                    HeroCatalog.MAGOS[0]
                )
            )
        )
    }
}
