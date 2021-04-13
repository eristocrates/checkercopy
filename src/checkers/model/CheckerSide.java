package checkers.model;

public enum CheckerSide {
    BLACK,
    RED;

    public boolean equalsType(CheckerSide other) {
        return equals(other); // true if the calling enum type (left hand side) is equal to the param enum type (right hand side)
    }

}
