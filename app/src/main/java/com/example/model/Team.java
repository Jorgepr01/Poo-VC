package com.example.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Team {
    public static final int MAX_TOTAL_HEROES = 6;
    public static final int MIN_TOTAL_HEROES = 1;
    public static final int MAX_GUERREROS = 4;
    public static final int MAX_MISTICOS = 3;
    public static final int MAX_MAGOS = 2;

    private final String id;
    private final String name;
    private final boolean isDefault;
    private final List<Hero> guerreros;
    private final List<Hero> misticos;
    private final List<Hero> magos;

    public Team(
            String id,
            String name,
            boolean isDefault,
            List<Hero> guerreros,
            List<Hero> misticos,
            List<Hero> magos
    ) {
        this.id = id;
        this.name = name;
        this.isDefault = isDefault;
        this.guerreros = Collections.unmodifiableList(new ArrayList<>(guerreros));
        this.misticos = Collections.unmodifiableList(new ArrayList<>(misticos));
        this.magos = Collections.unmodifiableList(new ArrayList<>(magos));
    }

    public Team(String id, String name, List<Hero> heroes) {
        this.id = id;
        this.name = name;
        this.isDefault = false;
        List<Hero> g = new ArrayList<>();
        List<Hero> m = new ArrayList<>();
        List<Hero> w = new ArrayList<>();
        for (Hero hero : heroes) {
            if (hero.getRole() == HeroRole.GUERRERO) g.add(hero);
            else if (hero.getRole() == HeroRole.MISTICO) m.add(hero);
            else if (hero.getRole() == HeroRole.MAGO) w.add(hero);
        }
        this.guerreros = Collections.unmodifiableList(g);
        this.misticos = Collections.unmodifiableList(m);
        this.magos = Collections.unmodifiableList(w);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public List<Hero> getGuerreros() {
        return guerreros;
    }

    public List<Hero> getMisticos() {
        return misticos;
    }

    public List<Hero> getMagos() {
        return magos;
    }

    public List<Hero> getAllHeroes() {
        List<Hero> all = new ArrayList<>();
        all.addAll(guerreros);
        all.addAll(misticos);
        all.addAll(magos);
        return Collections.unmodifiableList(all);
    }

    public int getTotalCount() {
        return guerreros.size() + misticos.size() + magos.size();
    }

    public String getSoldierCountDesc() {
        return guerreros.size() + " Guerreros · " + misticos.size() + " Místicos · " + magos.size() + " Magos";
    }

    public int getTotalHp() {
        int total = 0;
        for (Hero h : getAllHeroes()) total += h.getHp();
        return total;
    }

    public int getTotalAtk() {
        int total = 0;
        for (Hero h : getAllHeroes()) total += h.getAttack();
        return total;
    }

    public int getTotalAttack() {
        return getTotalAtk();
    }

    public int getTotalDef() {
        int total = 0;
        for (Hero h : getAllHeroes()) total += h.getDefense();
        return total;
    }

    public int getTotalDefense() {
        return getTotalDef();
    }

    public boolean isValidComposition() {
        int total = getTotalCount();
        if (total < MIN_TOTAL_HEROES || total > MAX_TOTAL_HEROES) {
            return false;
        }
        return guerreros.size() <= MAX_GUERREROS &&
                misticos.size() <= MAX_MISTICOS &&
                magos.size() <= MAX_MAGOS;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Team team = (Team) o;
        return isDefault == team.isDefault &&
                Objects.equals(id, team.id) &&
                Objects.equals(name, team.name) &&
                Objects.equals(guerreros, team.guerreros) &&
                Objects.equals(misticos, team.misticos) &&
                Objects.equals(magos, team.magos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, isDefault, guerreros, misticos, magos);
    }
}
