package com.example.model;

public enum AttackType {
    BASIC("Ataque Básico", "Golpe estándar individual", 1.0, 1),
    MULTI_2("Ataque Doble", "Golpea a 2 objetivos (85% daño c/u)", 0.85, 2),
    MULTI_3("Ataque Triple", "Golpea a 3 objetivos (70% daño c/u)", 0.70, 3),
    ULTIMATE("Habilidad Definitiva", "Poder devastador (+35% daño extra)", 1.35, 1);

    private final String displayName;
    private final String description;
    private final double damageMultiplier;
    private final int targetCount;

    AttackType(String displayName, String description, double damageMultiplier, int targetCount) {
        this.displayName = displayName;
        this.description = description;
        this.damageMultiplier = damageMultiplier;
        this.targetCount = targetCount;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public double getDamageMultiplier() {
        return damageMultiplier;
    }

    public int getTargetCount() {
        return targetCount;
    }
}
