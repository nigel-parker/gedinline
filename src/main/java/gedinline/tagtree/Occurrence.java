package gedinline.tagtree;

public enum Occurrence {
    MANDATORY, OPTIONAL, MULTIPLE, UP_TO_3_TIMES, AT_LEAST_1;

    public boolean isMandatory() {
        return this == MANDATORY;
    }

    public boolean isOptional() {
        return this == OPTIONAL;
    }

    public boolean isMultiple() {
        return this == MULTIPLE;
    }

    public boolean is3Times() {
        return this == UP_TO_3_TIMES;
    }

    public boolean isAtLeastOnce() {
        return this == AT_LEAST_1;
    }

    public String toString() {
        return this == OPTIONAL ? "" : super.toString();
    }
}
