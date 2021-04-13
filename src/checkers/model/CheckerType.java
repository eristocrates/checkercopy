package checkers.model;

public enum CheckerType {
    MAN,
    KING;

    public boolean equalsType(CheckerType other) {
        return equals(other); // true if the calling enum type (left hand side) is equal to the param enum type (right hand side)
    }

}
