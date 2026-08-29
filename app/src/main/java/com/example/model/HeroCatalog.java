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
                "El Bastión Inquebrantable",
                "«Mi escudo es la muralla donde el caos se estrella.»",
                HeroRole.GUERRERO,
                480,
                110,
                85,
                "Paladín acorazado con escudo pesado de la Orden del Roble Real.",
                "Muro Inquebrantable",
                "Mitiga daño y blinda la vanguardia con armadura consagrada.",
                "Ideal para absorber castigo en vanguardia gracias a su altísima defensa de 85 y protección frontal activa a sus aliados.",
                "🛡️",
                2
        ));
        warriors.add(new Hero(
                "g_barbarian",
                "Brutus el Feroz",
                "Furia de las Cumbres del Norte",
                "«¡La piedad no forja imperios, la fuerza sí!»",
                HeroRole.GUERRERO,
                520,
                135,
                65,
                "Bárbaro colosal forjado en el hielo con una demoledora hacha doble.",
                "Golpe Hendiente",
                "Impactos feroces que desgarran armaduras y quiebran formaciones.",
                "Excelente para jugadores agresivos: tiene la mayor cantidad de vida (520 HP), alto daño físico (135 ATK) y estrategia rota 1d6 (+35% Daño).",
                "🪓",
                1
        ));
        warriors.add(new Hero(
                "g_knight",
                "Sir Galahad",
                "Hoja del Juramento Solar",
                "«Por el honor de la corona, cada golpe encuentra su destino.»",
                HeroRole.GUERRERO,
                460,
                120,
                80,
                "Caballero consagrado veloz y equilibrado, maestro de la espada bastarda.",
                "Estocada Sagrada",
                "Cortes milimétricos de precisión con alta probabilidad crítica.",
                "El guerrero más equilibrado: sólida defensa (80 DEF) con versatilidad para responder en cualquier etapa del combate.",
                "⚔️",
                2
        ));
        warriors.add(new Hero(
                "g_guardian",
                "Darius Hojaferro",
                "Veterano de Mil Asedios",
                "«He roto mil líneas enemigas; esta será solo una más.»",
                HeroRole.GUERRERO,
                500,
                125,
                75,
                "Veterano mercenario con lanza larga capaz de doblegar escuadrones.",
                "Rompefilas",
                "Carga con lanza capaz de desestabilizar posiciones defensivas.",
                "El mejor rompe-tanques: 500 HP y lanza pesada con estrategia rota 1d6 (+35% Daño) para quebrar blindajes.",
                "🗡️",
                1
        ));
        GUERREROS = Collections.unmodifiableList(warriors);

        List<Hero> mystics = new ArrayList<>();
        mystics.add(new Hero(
                "m_solar",
                "Elysia Tejevelos",
                "Suma Sacerdotisa Solar",
                "«La luz del alba desvanece las defensas de la sombra.»",
                HeroRole.MISTICO,
                360,
                130,
                45,
                "Sacerdotisa mística que manipula los rayos solares para debilitar rivales.",
                "Juicio Solar",
                "Haz de luz concentrada que calcina la armadura enemiga (-35% DEF durante 1 ronda de vulnerabilidad).",
                "La elección perfecta para debilitar muros defensivos enemigos y preparar remates letales en el turno.",
                "☀️",
                2
        ));
        mystics.add(new Hero(
                "m_shadow",
                "Malakor el Sombrío",
                "Nigromante del Abismo",
                "«Vuestra fuerza me pertenece... gota a gota.»",
                HeroRole.MISTICO,
                380,
                140,
                40,
                "Brujo del abismo experto en maldiciones oscuras y drenaje de vigor.",
                "Maldición Umbría",
                "Entropía oscura devastadora que aminora el ataque y defensa rival (-25% ATK/DEF durante 2 rondas).",
                "El mejor místico ofensivo con 140 ATK y estrategia rota 1d6 (+35% Daño) para aniquilar amenazas.",
                "🌑",
                1
        ));
        mystics.add(new Hero(
                "m_bard",
                "Lyra Cantoarcano",
                "Voz de los Ecos Perdidos",
                "«Una sola nota justa puede cambiar el compás de una guerra.»",
                HeroRole.MISTICO,
                350,
                135,
                50,
                "Barda oracular con arpa rúnica que altera el destino y el flujo del combate.",
                "Réquiem Arcano",
                "Onda sónica armónica que distorsiona las tácticas del adversario (-25% DEF durante 1 ronda).",
                "Mayor resistencia en línea media (50 DEF) combinada con control de combate para abrir brechas tácticas.",
                "🔮",
                2
        ));
        mystics.add(new Hero(
                "m_druid",
                "Kaelen Susurro Verde",
                "Guardián del Bosque Primigenio",
                "«La naturaleza no juzga; simplemente reclama lo que es suyo.»",
                HeroRole.MISTICO,
                370,
                128,
                48,
                "Druida que convoca la furia de las raíces y la fuerza de la tierra ancestral.",
                "Zarza Ancestral",
                "Espinas místicas que desgastan el ataque del oponente (-30% ATK durante 2 rondas).",
                "Vital contra equipos con bárbaros o magos explosivos: neutraliza la potencia del mayor atacante enemigo.",
                "🌿",
                2
        ));
        mystics.add(new Hero(
                "m_cleric",
                "Hermana Serena",
                "Mano de la Gracia Celestial",
                "«Donde hay fe y unión, la victoria es ineludible.»",
                HeroRole.MISTICO,
                375,
                122,
                52,
                "Clériga devota que potencia la moral y fortaleza de sus camaradas.",
                "Bendición Heroica",
                "Otorga +25% de ataque y +20% defensa a todos los aliados vivos durante 3 rondas (Gran Bendición).",
                "La mejor opción de soporte global: potencia simultáneamente la resistencia y letalidad de toda tu escuadra.",
                "✨",
                2
        ));
        MISTICOS = Collections.unmodifiableList(mystics);

        List<Hero> mages = new ArrayList<>();
        mages.add(new Hero(
                "w_pyro",
                "Archimago Ignis",
                "Señor de la Llama Eterna",
                "«Todo arderá bajo el crisol del fuego primordial.»",
                HeroRole.MAGO,
                320,
                175,
                35,
                "Maestro del fuego primordial con devastación explosiva y cauterización regenerativa.",
                "Maelstrom Ígneo",
                "Lanza tormentas incandescentes o canaliza calor restaurador.",
                "Mago demoledor híbrido: 175 ATK con estrategia rota 1d6 (+35% Daño) o potentes curaciones tácticas.",
                "🔥",
                1
        ));
        mages.add(new Hero(
                "w_storm",
                "Kaelith Tempestad",
                "Heraldo del Rayo Celestial",
                "«El trueno no avisa; cae y purifica el campo.»",
                HeroRole.MAGO,
                310,
                185,
                30,
                "Invocador de relámpagos celestes y centellas de alto poder destructivo.",
                "Tormenta de Rayos",
                "Golpea a múltiples objetivos con descargas eléctricas de choque.",
                "El máximo portador de daño del torneo con 185 ATK y estrategia rota 1d6 (+35% Daño).",
                "⚡",
                1
        ));
        mages.add(new Hero(
                "w_frost",
                "Morrigan Escarcha",
                "Dama de los Glaciares Eternos",
                "«El frío preserva a los míos y congela las esperanzas rivales.»",
                HeroRole.MAGO,
                330,
                165,
                40,
                "Hechicera glacial que canaliza vientos árticos y sellos de escarcha defensiva.",
                "Ventisca Glacial",
                "Vientos polares dañinos y regeneración profunda de frío puro.",
                "La maga más resistente (330 HP / 40 DEF) y con la curación más consistente para partidas largas de desgaste.",
                "❄️",
                2
        ));
        mages.add(new Hero(
                "w_astral",
                "Zephyr del Vacío",
                "Canalizador de Estrellas Caídas",
                "«En el tejido del cosmos, vuestra derrota ya está escrita.»",
                HeroRole.MAGO,
                315,
                180,
                32,
                "Astrólogo arcano que invoca la gravedad cósmica y supernovas arcanas.",
                "Cataclismo Astral",
                "Explosión cósmica que fractura el destino del combate.",
                "Inmenso poder de ráfaga (180 ATK) con estrategia rota 1d6 (+35% Daño) para definir momentos críticos.",
                "🌌",
                1
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
