package com.example

import com.example.model.Hero
import com.example.model.HeroCatalog
import com.example.model.Team
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TeamCompositionTest {

    @Test
    fun testValidTeamComposition() {
        val validTeam = Team(
            "test_team_1",
            "Escuadrón Élite",
            false,
            listOf(HeroCatalog.GUERREROS[0], HeroCatalog.GUERREROS[1], HeroCatalog.GUERREROS[2], HeroCatalog.GUERREROS[3]),
            listOf(HeroCatalog.MISTICOS[0]),
            listOf(HeroCatalog.MAGOS[0])
        )

        assertEquals(6, validTeam.totalCount)
        assertTrue(validTeam.isValidComposition)
    }

    @Test
    fun testInvalidTeamExceedingMaxHeroes() {
        val invalidTeam = Team(
            "test_team_invalid",
            "Escuadrón Ilegal",
            false,
            listOf(HeroCatalog.GUERREROS[0], HeroCatalog.GUERREROS[1], HeroCatalog.GUERREROS[2], HeroCatalog.GUERREROS[3]),
            listOf(HeroCatalog.MISTICOS[0], HeroCatalog.MISTICOS[1]),
            listOf(HeroCatalog.MAGOS[0])
        )

        assertEquals(7, invalidTeam.totalCount)
        assertFalse(invalidTeam.isValidComposition)
    }

    @Test
    fun testInvalidTeamExceedingClassLimits() {
        val tooManyMages = Team(
            "test_team_mages",
            "Muchos Magos",
            false,
            listOf(HeroCatalog.GUERREROS[0]),
            listOf(HeroCatalog.MISTICOS[0]),
            listOf(HeroCatalog.MAGOS[0], HeroCatalog.MAGOS[1], HeroCatalog.MAGOS[2])
        )

        assertEquals(5, tooManyMages.totalCount)
        assertFalse(tooManyMages.isValidComposition)
    }

    @Test
    fun testCustomTeamInBattle() {
        val customTeam = Team(
            "custom_p1",
            "Fuerza Bruta",
            false,
            listOf(HeroCatalog.GUERREROS[0], HeroCatalog.GUERREROS[1], HeroCatalog.GUERREROS[2], HeroCatalog.GUERREROS[3]),
            listOf(HeroCatalog.MISTICOS[0], HeroCatalog.MISTICOS[1]),
            emptyList<Hero>()
        )

        assertEquals(4, customTeam.guerreros.size)
        assertEquals(2, customTeam.misticos.size)
        assertEquals(0, customTeam.magos.size)
        assertTrue(customTeam.isValidComposition)
    }
}
