package net.christopherknox.rc.model;

public enum Priority {
    LOW(1),
    MEDIUM(3),
    HIGH(6);

    private final Integer weight;

    Priority(final Integer weight) {
        this.weight = weight;
    }

    public Integer getWeight() {
        return weight;
    }
}
