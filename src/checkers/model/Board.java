package checkers.model;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Board {
    // dimensions of the board
    private final int sizeX;
    private final int sizeY;

    private Map<Point, Square> point2Square = new HashMap<>(); // holds state of all squares accessed via point
    private final Map<Point, Piece> point2Piece = new HashMap<>(); // holds piece info of a square accessed via point

    private final GameStrategy gameStrategy;

    public Board(GameStrategy strategy, int x, int y) { // constructor
        gameStrategy = strategy;
        this.sizeX = x;
        this.sizeY = y;
    }

    public java.util.List<Point> generatePointsTopDownLeftRight() { // returns container full of all points for all squares on the board
        List<Point> ret = new ArrayList<>(); // container for all points

        for (int y = 1; y <= getSizeY(); y++) { // numbering starting in upper-left: (1,1) (2,1) ... (x,y) (1,2) ... ... (x,y)
            for (int x = 1; x <= getSizeX(); x++) {
                Point point = new Point(x, y);
                ret.add(point);
            }
        }
        return ret;
    }
    public java.util.List<Point> generatePointsInPlay() { // returns container full of all points for dark squares on the board
        List<Point> ret = new ArrayList<>(); // container for all points

        for (int y = 1; y <= getSizeY(); y++) { // numbering starting in upper-left: (1,1) (2,1) ... (x,y) (1,2) ... ... (x,y)
            for (int x = 1; x <= getSizeX(); x++) {
                Point point = new Point(x, y);
                ret.add(point);
                if (y % 2 == 0){
                    x++;
                }
            }
        }
        return ret;
    }

    public enum Square { // Square is an enum for the state of the square
        NOT_VALID_COORDINATES,  // 1) NOT_VALID_COORDINATES --outside the bounding rectangle
        LIGHT,            // 2) NOT_IN_PLAY -- inside the bounding rectangle, but is a light color square
        DARK;                // 3) IN_PLAY -- inside and valid


        public boolean equalsType(Square other) {
            return equals(other);
        }
    }

    public Square getSquare(Point point) { // gets state of square via it's point
        return point2Square.getOrDefault(point, Square.NOT_VALID_COORDINATES);
    }

    public Piece getPiece(Point point) { // gets info of the piece at a given point
        checkPoint(point);
        return getPoint2Piece(point);
    }

    public void checkPoint(Point point) { // checks if point is valid i.e. within the dimensions of the board
        Square square = getSquare(point);
        if (square.equalsType(Square.NOT_VALID_COORDINATES)) {
            throw new RuntimeException("Invalid coordinates " + point);
        }
    }

    public void place(Piece piece, Point point) { // places a piece at a target point
        checkPoint(point);
        if (point2Piece.containsKey(point)) {
            throw new RuntimeException("Point already contains a piece");
        } else {
            putPoint2Piece(point, piece);
        }

        // DESIGN: allow a placement to potentially "remove" a piece, because
        //         we are not going to check.
        // If we were going to check, it would be:
    }


    Piece getPoint2Piece(Point point) { // returns the piece value from the point key inside the point2Piece map
        return point2Piece.get(point);
    }

    void putPoint2Piece(Point point, Piece piece) { // adds/updates a piece and it's location (point)
        point2Piece.put(point,  piece);
    }
    void removePoint2Piece(Point point) { // removes a point and it's associated piece
        point2Piece.remove(point);
    }
    void putPoint2Square(Point point, Square square) { // adds/updates a piece and it's state
        point2Square.put(point, square);
    }

    public String dump() { // not directly used, but good for testing/debugging.
        StringBuilder sb = new StringBuilder();

        for (Point point : generatePointsTopDownLeftRight()) { // loops through every point/square on the board
            final String cell = gameStrategy.convertPointToDumpString(point);

            sb.append(cell);
            if (point.getX() == getSizeX()) {
                sb.append("\n");
            } else {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    public int getSizeX() {
        return sizeX;
    }

    public int getSizeY() {
        return sizeY;
    }

    public void movePiece(Point from, Point to) {
        gameStrategy.movePiece(from, to);
    }

    public boolean isValidToMove(Point from, Point to) {
        return gameStrategy.isValidToMove(from, to);

    }

    public boolean canMovePieceAtPoint(Point point) {
        return gameStrategy.canMovePieceAtPoint(point);

    }

    public void unmodifiablePoint2Square() {
        // TODO: prevent multiple calls
        point2Square = Collections.unmodifiableMap(point2Square);
    }

    public final void loadPiecesFromString(String s) { // example s: "bbbbbbbbbbbb--------wwwwwwwwwwww"

        List<String> piecesString = fromString(s);
        List<Piece> pieces = fromList(piecesString);
        loadPieces(pieces);
    }

    private List<String> fromString(String s) {
        return gameStrategy.splitBoardStateString(s);
    }

    private List<Piece> fromList(List<String> list) {
        List<Piece> ret = new ArrayList<>();
        if (list != null) {
            for (String s : list) {
                Piece piece = createFromSingleString(s);
                ret.add(piece);
            }
        }
        return ret;
    }

    private Piece createFromSingleString(String s) {
        return gameStrategy.createPieceFromSingleString(s);
    }
    public final void loadPieces(List<Piece> pieces) {
        List<Piece> copy = new ArrayList<>(pieces);
        for (Point point : generatePointsTopDownLeftRight()) {
            Square val = getSquare(point);
            if (Square.DARK.equalsType(val)) {
                Piece piece = copy.remove(0); // removing from the top seems better suited to another container type but maybe later oh well lol

                // even if piece is null, "place" it (to clear that Point)
                place(piece, point);
            }
        }
        if (copy.size() != 0) {
            throw new RuntimeException("Programmer error- extra pieces, size=" + copy.size());
        }
    }

}
