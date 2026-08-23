package com.example.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class HeroCatalog {

    public static final List<Hero> GUERREROS;
    public static final List<Hero> MISTICOS;
    public static final List<Hero> MAGOS;
    public static final List<Hero> ALL_HEROES;

    static {
        List<Hero> warriors = new ArrayList<>();
        warriors.add(new Hero(
                "g_paladin",
                "Lord Valerius",
                HeroRole.GUERRERO,
                480,
                110,
                85,
                "Paladín acorazado con escudo pesado de la Orden del Roble.",
                "Muro Inquebrantable",
                "Mitiga daño y protege la vanguardia con armadura pesada.",
                "🛡️"
        ));
        warriors.add(new Hero(
                "g_barbarian",
                "Brutus el Feroz",
                HeroRole.GUERRERO,
                520,
                135,
                65,
                "Bárbaro de las montañas del norte con gran hacha de guerra.",
                "Golpe Hendiente",
                "Impactos devastadores que desgarran armaduras enemigas.",
                "🪓"
        ));
        warriors.add(new Hero(
                "g_knight",
                "Sir Galahad",
                HeroRole.GUERRERO,
                460,
                120,
                80,
                "Caballero consagrado veloz y letal con espada larga.",
                "Estocada Sagrada",
                "Cortes de precisión con alta probabilidad de golpe crítico.",
                "⚔️"
        ));
        warriors.add(new Hero(
                "g_guardian",
                "Darius Hojaferro",
                HeroRole.GUERRERO,
                500,
                125,
                75,
                "Veterano mercenario curtido en incontables asedios.",
                "Rompefilas",
                "Carga con lanza capaz de desestabilizar posiciones defensivas.",
                "🗡️"
        ));
        GUERREROS = Collections.unmodifiableList(warriors);

        List<Hero> mystics = new ArrayList<>();
        mystics.add(new Hero(
                "m_solar",
                "Elysia Tejevelos",
                HeroRole.MISTICO,
                360,
                130,
                45,
                "Sacerdotisa solar que debilita la voluntad y defensas del rival.",
                "Juicio Solar",
                "Ataque de luz concentrada que reduce la defensa enemiga (-35% DEF).",
                "☀️"
        ));
        mystics.add(new Hero(
                "m_shadow",
                "Malakor el Sombrío",
                HeroRole.MISTICO,
                380,
                140,
                40,
                "Brujo del abismo experto en maldiciones y drenaje de fuerza.",
                "Maldición Umbría",
                "Entropía oscura que desgasta y aminora el ataque y defensa rival.",
                "🌑"
        ));
        mystics.add(new Hero(
                "m_bard",
                "Lyra Cantoarcano",
                HeroRole.MISTICO,
                350,
                135,
                50,
                "Barda oracular que manipula los flujos de energía táctica.",
                "Réquiem Arcano",
                "Onda sónica que confunde y distorsiona el poder rival.",
                "🔮"
        ));
        mystics.add(new Hero(
                "m_druid",
                "Kaelen Susurro Verde",
                HeroRole.MISTICO,
                370,
                128,
                48,
                "Druida que invoca espíritus ancestrales y raíces espinosas.",
                "Zarza Ancestral",
                "Espinas místicas que desgastan el ataque del oponente (-30% ATK).",
                "🌿"
        ));
        mystics.add(new Hero(
                "m_cleric",
                "Hermana Serena",
                HeroRole.MISTICO,
                375,
                122,
                52,
                "Clériga de la luz que bendice a sus aliados.",
                "Bendición Heroica",
                "Otorga +25% de ataque y defensa a todos los aliados vivos.",
                "✨"
        ));
        MISTICOS = Collections.unmodifiableList(mystics);

        List<Hero> mages = new ArrayList<>();
        mages.add(new Hero(
                "w_pyro",
                "Archimago Ignis",
                HeroRole.MAGO,
                320,
                175,
                35,
                "Maestro de las llamas eternas con inmenso poder destructivo.",
                "Maelstrom Ígneo",
                "Lanza bolas de fuego demoledoras y olas restauradoras.",
                "🔥"
        ));
        mages.add(new Hero(
                "w_storm",
                "Kaelith Tempestad",
                HeroRole.MAGO,
                310,
                185,
                30,
                "Invocador de relámpagos y vendavales celestiales.",
                "Tormenta de Rayos",
                "Golpea a múltiples objetivos con descargas de alto voltaje.",
                "⚡"
        ));
        mages.add(new Hero(
                "w_frost",
                "Morrigan Escarcha",
                HeroRole.MAGO,
                330,
                165,
                40,
                "Hechicera glacial que congela y canaliza ventiscas curativas.",
                "Ventisca Glacial",
                "Vientos gélidos y regeneración pura de escarcha.",
                "❄️"
        ));
        mages.add(new Hero(
                "w_astral",
                "Zephyr del Vacío",
                HeroRole.MAGO,
                315,
                180,
                32,
                "Canalizador de anomalías astrales y magia primordial cósmica.",
                "Cataclismo Astral",
                "Explosión cósmica que altera el destino del combate.",
                "🌌"
        ));
        MAGOS = Collections.unmodifiableList(mages);

        List<Hero> all = new ArrayList<>();
        all.addAll(GUERREROS);
        all.addAll(MISTICOS);
        all.addAll(MAGOS);
        ALL_HEROES = Collections.unmodifiableList(all);
    }

    private HeroCatalog() {}

    public static List<Hero> getAllHeroes() {
        return ALL_HEROES;
    }

    public static Hero findById(String id) {
        for (Hero hero : ALL_HEROES) {
            if (hero.getId().equals(id)) {
                return hero;
            }
        }
        return ALL_HEROES.get(0);
    }

    public static List<Hero> getByRole(HeroRole role) {
        if (role == null) return ALL_HEROES;
        switch (role) {
            case GUERRERO:
                return GUERREROS;
            case MISTICO:
                return MISTICOS;
            case MAGO:
                return MAGOS;
            default:
                return ALL_HEROES;
        }
    }
}
