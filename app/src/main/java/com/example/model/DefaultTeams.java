package com.example.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class DefaultTeams {

    private DefaultTeams() {}

    public static List<Team> createDefaults() {
        List<Team> list = new ArrayList<>();

        // Equipo 1: Escuadrón Imperial (2 Guerreros, 2 Místicos, 2 Magos)
        List<Hero> g1 = new ArrayList<>();
        g1.add(HeroCatalog.GUERREROS.get(0)); // Lord Valerius
        g1.add(HeroCatalog.GUERREROS.get(2)); // Sir Galahad

        List<Hero> m1 = new ArrayList<>();
        m1.add(HeroCatalog.MISTICOS.get(0)); // Elysia
        m1.add(HeroCatalog.MISTICOS.get(2)); // Lyra

        List<Hero> w1 = new ArrayList<>();
        w1.add(HeroCatalog.MAGOS.get(0)); // Archimago Ignis
        w1.add(HeroCatalog.MAGOS.get(1)); // Kaelith Tempestad

        list.add(new Team("default_1", "Escuadrón Imperial", true, g1, m1, w1));

        // Equipo 2: Horda del Caos (3 Guerreros, 2 Místicos, 1 Mago)
        List<Hero> g2 = new ArrayList<>();
        g2.add(HeroCatalog.GUERREROS.get(1)); // Brutus
        g2.add(HeroCatalog.GUERREROS.get(3)); // Darius
        g2.add(HeroCatalog.GUERREROS.get(2)); // Sir Galahad

        List<Hero> m2 = new ArrayList<>();
        m2.add(HeroCatalog.MISTICOS.get(1)); // Malakor
        m2.add(HeroCatalog.MISTICOS.get(3)); // Kaelen

        List<Hero> w2 = new ArrayList<>();
        w2.add(HeroCatalog.MAGOS.get(2)); // Morrigan Escarcha

        list.add(new Team("default_2", "Horda del Caos", true, g2, m2, w2));

        // Equipo 3: Vanguardia Arcana (2 Guerreros, 3 Místicos, 1 Mago)
        List<Hero> g3 = new ArrayList<>();
        g3.add(HeroCatalog.GUERREROS.get(0)); // Lord Valerius
        g3.add(HeroCatalog.GUERREROS.get(1)); // Brutus

        List<Hero> m3 = new ArrayList<>();
        m3.add(HeroCatalog.MISTICOS.get(0)); // Elysia
        m3.add(HeroCatalog.MISTICOS.get(1)); // Malakor
        m3.add(HeroCatalog.MISTICOS.get(4)); // Hermana Serena

        List<Hero> w3 = new ArrayList<>();
        w3.add(HeroCatalog.MAGOS.get(3)); // Zephyr

        list.add(new Team("default_3", "Vanguardia Arcana", true, g3, m3, w3));

        return Collections.unmodifiableList(list);
    }
}
