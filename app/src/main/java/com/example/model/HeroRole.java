package com.example.model;

public enum HeroRole {
    GUERRERO("Guerrero", "Vanguardia", "G", 4, 0xFF8A9A5B, 0xFF5D6B3C),
    MISTICO("Místico", "Línea Media", "M", 3, 0xFFB8860B, 0xFF8B6508),
    MAGO("Mago", "Retaguardia", "W", 2, 0xFF4A766E, 0xFF2F4F4F);

    private final String displayName;
    private final String roleLabel;
    private final String shortTag;
    private final int maxCount;
    private final long colorHex;
    private final long badgeBorderHex;

    HeroRole(String displayName, String roleLabel, String shortTag, int maxCount, long colorHex, long badgeBorderHex) {
        this.displayName = displayName;
        this.roleLabel = roleLabel;
        this.shortTag = shortTag;
        this.maxCount = maxCount;
        this.colorHex = colorHex;
        this.badgeBorderHex = badgeBorderHex;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getRoleLabel() {
        return roleLabel;
    }

    public String getShortTag() {
        return shortTag;
    }

    public String getShortBadge() {
        return shortTag;
    }

    public int getMaxCount() {
        return maxCount;
    }

    public long getColorHex() {
        return colorHex;
    }

    public long getBadgeBorderHex() {
        return badgeBorderHex;
    }
}
