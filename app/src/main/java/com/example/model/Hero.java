package com.example.model;

import java.util.Objects;

public class Hero {
    private final String id;
    private final String name;
    private final HeroRole role;
    private final int hp;
    private final int attack;
    private final int defense;
    private final String description;
    private final String abilityName;
    private final String abilityDescription;
    private final String avatarEmoji;
    private final long colorAccentHex;

    public Hero(
            String id,
            String name,
            HeroRole role,
            int hp,
            int attack,
            int defense,
            String description,
            String abilityName,
            String abilityDescription,
            String avatarEmoji,
            long colorAccentHex
    ) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.hp = hp;
        this.attack = attack;
        this.defense = defense;
        this.description = description;
        this.abilityName = abilityName;
        this.abilityDescription = abilityDescription;
        this.avatarEmoji = avatarEmoji;
        this.colorAccentHex = colorAccentHex;
    }

    public Hero(
            String id,
            String name,
            HeroRole role,
            int hp,
            int attack,
            int defense,
            String description,
            String abilityName,
            String abilityDescription,
            String avatarEmoji
    ) {
        this(id, name, role, hp, attack, defense, description, abilityName, abilityDescription, avatarEmoji, role.getColorHex());
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public HeroRole getRole() {
        return role;
    }

    public int getHp() {
        return hp;
    }

    public int getAttack() {
        return attack;
    }

    public int getDefense() {
        return defense;
    }

    public String getDescription() {
        return description;
    }

    public String getBasicDesc() {
        return description;
    }

    public String getAbilityName() {
        return abilityName;
    }

    public String getAbilityDescription() {
        return abilityDescription;
    }

    public String getUltimateDesc() {
        return abilityDescription;
    }

    public String getStrategyDesc() {
        return "Minijuego táctico de dados 2d6 (+15% Ataque si acierta).";
    }

    public String getAvatarEmoji() {
        return avatarEmoji;
    }

    public String getAvatarSymbol() {
        return avatarEmoji;
    }

    public String getAvatarId() {
        return avatarEmoji;
    }

    public long getColorAccentHex() {
        return colorAccentHex;
    }

    public String getRoleName() {
        return role != null ? role.getDisplayName() : "";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Hero hero = (Hero) o;
        return hp == hero.hp &&
                attack == hero.attack &&
                defense == hero.defense &&
                colorAccentHex == hero.colorAccentHex &&
                Objects.equals(id, hero.id) &&
                Objects.equals(name, hero.name) &&
                role == hero.role &&
                Objects.equals(description, hero.description) &&
                Objects.equals(abilityName, hero.abilityName) &&
                Objects.equals(abilityDescription, hero.abilityDescription) &&
                Objects.equals(avatarEmoji, hero.avatarEmoji);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, role, hp, attack, defense, description, abilityName, abilityDescription, avatarEmoji, colorAccentHex);
    }

    @Override
    public String toString() {
        return "Hero{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", role=" + role +
                ", hp=" + hp +
                ", attack=" + attack +
                ", defense=" + defense +
                '}';
    }
}
