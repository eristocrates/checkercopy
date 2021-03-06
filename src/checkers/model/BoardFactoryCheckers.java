package checkers.model;

import java.awt.*;

public class BoardFactoryCheckers { // factory for making the checker board
    public static Board createCheckerBoardStandardStarting() {
        final int size = 8;
        final int numberCheckersPerSide = 12;
        final int numberBlankInMiddle = 8;
        StringBuilder pieces = new StringBuilder();

        for (int i = 0; i < numberCheckersPerSide; i++) {
            String piece = "b";
            pieces.append(piece);
        }
        for (int i = 0; i < numberBlankInMiddle; i++) {
            String piece = "-";
            pieces.append(piece);
        }
        for (int i = 0; i < numberCheckersPerSide; i++) {
            String piece = "r";
            pieces.append(piece);
        }

        return createCheckerBoard(size, pieces.toString());
    }
    public static Board createCheckerBoard(int size, String piecesAsString) {
        GameStrategy gameStrategy = new GameStrategyCheckers();
        Board ret = new Board(gameStrategy, size, size);
        gameStrategy.setBoard(ret);

        Board.Square val = Board.Square.LIGHT;
        for (Point point : ret.generatePointsTopDownLeftRight()) {
            ret.putPoint2Square(point, val);

            // "alternate val" covers most cases,
            // but when we come to the end, we stay the same
            if (point.getX() != size) {
                val = val.equalsType(Board.Square.LIGHT) ? Board.Square.DARK : Board.Square.LIGHT;
            }
        }
        // no more modifications to the squares:
        ret.unmodifiablePoint2Square();


        ret.loadPiecesFromString(piecesAsString);

        return ret;
    }

}
