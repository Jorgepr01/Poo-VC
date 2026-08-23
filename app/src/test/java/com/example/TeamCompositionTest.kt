package com.example

import com.example.model.HeroCatalog
import com.example.model.HeroRole
import com.example.model.Team
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TeamCompositionTest {

    @Test
    fun testValidTeamComposition() {
        val validTeam = Team(
            id = "test_team_1",
            name = "Escuadrón Élite",
            guerreros = listOf(HeroCatalog.GUERREROS[0], HeroCatalog.GUERREROS[1], HeroCatalog.GUERREROS[2], HeroCatalog.GUERREROS[3]), // 4 Guerreros (Max)
            misticos = listOf(HeroCatalog.MISTICOS[0]), // 1 Místico
            magos = listOf(HeroCatalog.MAGOS[0]) // 1 Mago
        )

        assertEquals(6, validTeam.totalCount)
        assertTrue(validTeam.isValidComposition)
    }

    @Test
    fun testInvalidTeamExceedingMaxHeroes() {
        val invalidTeam = Team(
            id = "test_team_invalid",
            name = "Escuadrón Ilegal",
            guerreros = listOf(HeroCatalog.GUERREROS[0], HeroCatalog.GUERREROS[1], HeroCatalog.GUERREROS[2], HeroCatalog.GUERREROS[3]), // 4 Guerreros
            misticos = listOf(HeroCatalog.MISTICOS[0], HeroCatalog.MISTICOS[1]), // 2 Místicos
            magos = listOf(HeroCatalog.MAGOS[0]) // 1 Mago -> Total 7 > 6
        )

        assertEquals(7, invalidTeam.totalCount)
        assertFalse(invalidTeam.isValidComposition)
    }

    @Test
    fun testInvalidTeamExceedingClassLimits() {
        val tooManyMages = Team(
            id = "test_team_mages",
            name = "Muchos Magos",
            guerreros = listOf(HeroCatalog.GUERREROS[0]),
            misticos = listOf(HeroCatalog.MISTICOS[0]),
            magos = listOf(HeroCatalog.MAGOS[0], HeroCatalog.MAGOS[1], HeroCatalog.MAGOS[2]) // 3 Magos > Max 2
        )

        assertEquals(5, tooManyMages.totalCount)
        assertFalse(tooManyMages.isValidComposition)
    }

    @Test
    fun testCustomTeamInBattle() {
        val customTeam = Team(
            id = "custom_p1",
            name = "Fuerza Bruta",
            guerreros = listOf(HeroCatalog.GUERREROS[0], HeroCatalog.GUERREROS[1], HeroCatalog.GUERREROS[2], HeroCatalog.GUERREROS[3]),
            misticos = listOf(HeroCatalog.MISTICOS[0], HeroCatalog.MISTICOS[1]),
            magos = emptyList()
        )

        assertEquals(4, customTeam.guerreros.size)
        assertEquals(2, customTeam.misticos.size)
        assertEquals(0, customTeam.magos.size)
        assertTrue(customTeam.isValidComposition)
    }
}
