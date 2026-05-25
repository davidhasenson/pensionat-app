package org.example.pensionatapp.pensionat.room;

public enum BedType {
    SINGLE_BED("Enkelrum"),
    DOUBLE_BED("Dubbelrum"),
    TWIN_ROOM("Tvåbäddsrum");

    private final String displayName;

    BedType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
