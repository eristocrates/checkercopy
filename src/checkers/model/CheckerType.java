package checkers.model;

public enum CheckerType {
    REGULAR, // should rename to men later
    KING;

    public boolean equalsType(CheckerType other) { // maybe should've just overritten equals?
        return equals(other); // true if the calling enum type (left hand side) is equal to the param enum type (right hand side)
    }

}
