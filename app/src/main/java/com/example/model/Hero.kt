package com.example.model

enum class HeroRole(val displayName: String, val shortBadge: String) {
    GUERRERO("Guerrero", "G"),
    MISTICO("Místico", "M"),
    MAGO("Mago", "A")
}

data class Hero(
    val id: String,
    val name: String,
    val role: HeroRole,
    val hp: Int,
    val attack: Int,
    val defense: Int,
    val strategyDesc: String,
    val ultimateDesc: String,
    val avatarId: String
)

object HeroCatalog {
    val GUERREROS = listOf(
        Hero(
            id = "g_ironclad",
            name = "Guerrero de Hierro",
            role = HeroRole.GUERRERO,
            hp = 63,
            attack = 63,
            defense = 40,
            strategyDesc = "Multiplicar el ataque un 15% o disminuye 10%",
            ultimateDesc = "Aumenta el daño de sus compañeros un 20% en 2 rondas",
            avatarId = "img1"
        ),
        Hero(
            id = "g_paladin",
            name = "Caballero Paladín",
            role = HeroRole.GUERRERO,
            hp = 70,
            attack = 55,
            defense = 50,
            strategyDesc = "Otorga escudo de 15 puntos o suma +8 de ataque",
            ultimateDesc = "Golpe de justicia divina a 2 enemigos con +30% de daño",
            avatarId = "img2"
        ),
        Hero(
            id = "g_berserker",
            name = "Bárbaro Furioso",
            role = HeroRole.GUERRERO,
            hp = 58,
            attack = 72,
            defense = 30,
            strategyDesc = "Sacrifica 5 de vida por +25% de daño crítico",
            ultimateDesc = "Frenesí de batalla: ataca a toda la primera fila enemiga",
            avatarId = "img3"
        ),
        Hero(
            id = "g_sentinel",
            name = "Centinela Real",
            role = HeroRole.GUERRERO,
            hp = 65,
            attack = 60,
            defense = 45,
            strategyDesc = "Bloquea el 20% del siguiente daño recibido",
            ultimateDesc = "Muro de escudos: reduce daño recibido a todo el equipo un 25%",
            avatarId = "img4"
        )
    )

    val MISTICOS = listOf(
        Hero(
            id = "m_solar",
            name = "Místico Solar",
            role = HeroRole.MISTICO,
            hp = 50,
            attack = 58,
            defense = 35,
            strategyDesc = "Restaura 12 HP al aliado más débil o +10% ataque",
            ultimateDesc = "Luz curativa: cura 18 HP a todos los aliados y purifica",
            avatarId = "img1"
        ),
        Hero(
            id = "m_druid",
            name = "Druida Arbóreo",
            role = HeroRole.MISTICO,
            hp = 54,
            attack = 52,
            defense = 38,
            strategyDesc = "Enreda al enemigo reduciendo su defensa un 15%",
            ultimateDesc = "Espinas protectoras: devuelve 20% de daño recibido",
            avatarId = "img2"
        ),
        Hero(
            id = "m_shadow",
            name = "Místico Umbrío",
            role = HeroRole.MISTICO,
            hp = 48,
            attack = 65,
            defense = 32,
            strategyDesc = "Drena 10 HP al objetivo para transferirlos a sí mismo",
            ultimateDesc = "Velo de sombras: 30% evasión para el equipo durante 1 ronda",
            avatarId = "img3"
        ),
        Hero(
            id = "m_cleric",
            name = "Sacerdote Santo",
            role = HeroRole.MISTICO,
            hp = 52,
            attack = 50,
            defense = 40,
            strategyDesc = "Aumenta la defensa del equipo un 10%",
            ultimateDesc = "Bendición arcana: otorga revivir con 20 HP al primer aliado caído",
            avatarId = "img4"
        )
    )

    val MAGOS = listOf(
        Hero(
            id = "a_pyro",
            name = "Archimago del Fuego",
            role = HeroRole.MAGO,
            hp = 45,
            attack = 78,
            defense = 25,
            strategyDesc = "Quema al objetivo causando 8 de daño continuo",
            ultimateDesc = "Lluvia de meteoros: impacta a 3 enemigos con 75 de daño",
            avatarId = "img1"
        ),
        Hero(
            id = "a_frost",
            name = "Mago de Escarcha",
            role = HeroRole.MAGO,
            hp = 47,
            attack = 70,
            defense = 30,
            strategyDesc = "Congela al enemigo haciéndolo perder 10% de ataque",
            ultimateDesc = "Ventisca gélida: ralentiza y congela la fila frontal",
            avatarId = "img2"
        ),
        Hero(
            id = "a_storm",
            name = "Invocador de Tormentas",
            role = HeroRole.MAGO,
            hp = 44,
            attack = 82,
            defense = 22,
            strategyDesc = "Descarga de relámpago con 20% probabilidad de crítico x2",
            ultimateDesc = "Cataclismo eléctrico: daña a todos los enemigos de la retaguardia",
            avatarId = "img3"
        ),
        Hero(
            id = "a_necromancer",
            name = "Nigromante Arcano",
            role = HeroRole.MAGO,
            hp = 46,
            attack = 74,
            defense = 28,
            strategyDesc = "Maldición oscura que debilita el ataque enemigo en 12%",
            ultimateDesc = "Almas en pena: absorbe 15 HP de cada enemigo",
            avatarId = "img4"
        )
    )

    val ALL_HEROES = GUERREROS + MISTICOS + MAGOS

    fun getHeroById(id: String): Hero {
        return ALL_HEROES.find { it.id == id } ?: GUERREROS[0]
    }
}
