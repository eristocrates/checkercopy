package checkers.model;

public class Checker implements Piece {
    private static int globalCheckerId = 1; // each checker is assigned it's own id as they're created

    private final int checkerId = globalCheckerId++;
    private CheckerType checkerType;
    private final CheckerSide checkerSide;

    public Checker(CheckerType checkerType, CheckerSide checkerSide) { // constructor
        super();
        this.checkerType = checkerType;
        this.checkerSide = checkerSide;
    }

    public CheckerSide getSide() {
        return checkerSide;
    }

    public CheckerType getType() {
        return checkerType;
    }

    // really need a void make king, will add later
    public void setKing(){
        this.checkerType = CheckerType.KING;
    }
    public boolean isKing() {
        return CheckerType.KING.equalsType(checkerType);
    }

    public boolean isSide(CheckerSide querySide) { // is the parameter side (red or black) the same as the calling side
        return checkerSide.equalsType(querySide);
    }

    @Override
    public String toString() {
        return "Checker [checkerId=" + checkerId + ", checkerType="
                + checkerType + ", checkerSide=" + checkerSide + "]";
    }

    @Override
    public int hashCode() { // since we'll only ever need 32 checkers of a color 31 just werks
        final int prime = 31;
        int result = 1;
        result = prime * result + checkerId;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) // literally the same instance
            return true;
        if (obj == null) // empty parameter
            return false;
        if (getClass() != obj.getClass()) // mismatched classes
            return false;
        Checker other = (Checker) obj;

        return checkerId == other.checkerId; // different ids
    }

    public boolean equalsType(Checker checker) {
        return equals(checker);
    }

    // board population is handled by creating a string
    // this translates the letters in the string into Checker pieces
    public static Checker createFromSingleString(String s) {
        final Checker ret;
        final CheckerSide side;
        final CheckerType type;

        switch (s) {
            case "b" -> {
                side = CheckerSide.BLACK;
                type = CheckerType.MAN;
            }
            case "r" -> {
                side = CheckerSide.RED;
                type = CheckerType.MAN;
            }
            case "B" -> {
                side = CheckerSide.BLACK;
                type = CheckerType.KING;
            }
            case "R" -> {
                side = CheckerSide.RED;
                type = CheckerType.KING;
            }
            case "-" -> {
                side = null;
                type = null;
            }
            default -> throw new RuntimeException("Cannot convert '" + s + "' to a piece");
        }

        if (side != null) {
            ret = new Checker(type, side);
        } else {
            ret = null;
        }

        return ret;
    }

// funtion to promote to king could easily go here later


}
