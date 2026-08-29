package com.example.model;

import java.util.Objects;

public class Hero {
    private final String id;
    private final String name;
    private final String title;
    private final String motto;
    private final HeroRole role;
    private final int hp;
    private final int attack;
    private final int defense;
    private final String description;
    private final String abilityName;
    private final String abilityDescription;
    private final String tacticalReason;
    private final String avatarEmoji;
    private final long colorAccentHex;
    private final int strategyDiceCount;

    public Hero(
            String id,
            String name,
            String title,
            String motto,
            HeroRole role,
            int hp,
            int attack,
            int defense,
            String description,
            String abilityName,
            String abilityDescription,
            String tacticalReason,
            String avatarEmoji,
            long colorAccentHex,
            int strategyDiceCount
    ) {
        this.id = id;
        this.name = name;
        this.title = title != null ? title : "";
        this.motto = motto != null ? motto : "";
        this.role = role;
        this.hp = hp;
        this.attack = attack;
        this.defense = defense;
        this.description = description;
        this.abilityName = abilityName;
        this.abilityDescription = abilityDescription;
        this.tacticalReason = tacticalReason != null ? tacticalReason : "";
        this.avatarEmoji = avatarEmoji;
        this.colorAccentHex = colorAccentHex;
        this.strategyDiceCount = strategyDiceCount <= 1 ? 1 : 2;
    }

    public Hero(
            String id,
            String name,
            String title,
            String motto,
            HeroRole role,
            int hp,
            int attack,
            int defense,
            String description,
            String abilityName,
            String abilityDescription,
            String tacticalReason,
            String avatarEmoji,
            long colorAccentHex
    ) {
        this(id, name, title, motto, role, hp, attack, defense, description, abilityName, abilityDescription, tacticalReason, avatarEmoji, colorAccentHex, 2);
    }

    public Hero(
            String id,
            String name,
            String title,
            String motto,
            HeroRole role,
            int hp,
            int attack,
            int defense,
            String description,
            String abilityName,
            String abilityDescription,
            String tacticalReason,
            String avatarEmoji
    ) {
        this(id, name, title, motto, role, hp, attack, defense, description, abilityName, abilityDescription, tacticalReason, avatarEmoji, role.getColorHex(), 2);
    }

    public Hero(
            String id,
            String name,
            String title,
            String motto,
            HeroRole role,
            int hp,
            int attack,
            int defense,
            String description,
            String abilityName,
            String abilityDescription,
            String tacticalReason,
            String avatarEmoji,
            int strategyDiceCount
    ) {
        this(id, name, title, motto, role, hp, attack, defense, description, abilityName, abilityDescription, tacticalReason, avatarEmoji, role.getColorHex(), strategyDiceCount);
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
        this(id, name, "", "", role, hp, attack, defense, description, abilityName, abilityDescription, abilityDescription, avatarEmoji, role.getColorHex(), 2);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public String getMotto() {
        return motto;
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

    public String getTacticalReason() {
        return tacticalReason.isEmpty() ? abilityDescription : tacticalReason;
    }

    public String getUltimateDesc() {
        return abilityDescription;
    }

    public String getStrategyDesc() {
        if (strategyDiceCount == 1) {
            return "Minijuego táctico 1d6 ROTO (1-6): ¡Bonificación demoledora (+35% Daño) si aciertas!";
        } else {
            return "Minijuego táctico 2d6 (2-12): Bonificación estratégica (+15% Daño) si aciertas.";
        }
    }

    public int getStrategyDiceCount() {
        return strategyDiceCount;
    }

    public boolean isBrokenStrategy() {
        return strategyDiceCount == 1;
    }

    public float getStrategyBonusMultiplier() {
        return strategyDiceCount == 1 ? 0.35f : 0.15f;
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
                strategyDiceCount == hero.strategyDiceCount &&
                Objects.equals(id, hero.id) &&
                Objects.equals(name, hero.name) &&
                Objects.equals(title, hero.title) &&
                Objects.equals(motto, hero.motto) &&
                role == hero.role &&
                Objects.equals(description, hero.description) &&
                Objects.equals(abilityName, hero.abilityName) &&
                Objects.equals(abilityDescription, hero.abilityDescription) &&
                Objects.equals(tacticalReason, hero.tacticalReason) &&
                Objects.equals(avatarEmoji, hero.avatarEmoji);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, title, motto, role, hp, attack, defense, description, abilityName, abilityDescription, tacticalReason, avatarEmoji, colorAccentHex, strategyDiceCount);
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
