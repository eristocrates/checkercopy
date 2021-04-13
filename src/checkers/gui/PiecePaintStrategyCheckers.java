package checkers.gui;

import checkers.model.Board;
import checkers.model.Checker;
import checkers.model.CheckerSide;
import checkers.model.Piece;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class PiecePaintStrategyCheckers implements PiecePaintStrategy {
    private final Board board;

    private final boolean piecesOnDark = true; // idk why there's an option for playing pieces on white, might remove later

    public PiecePaintStrategyCheckers(Board b) {
        board = b;
    }

    @Override
    public Color getColorForPoint(Point point) {
        Board.Square square = board.getSquare(point);

        // The official rules say pieces go on dark.
        if (square.equalsType(Board.Square.NOT_IN_PLAY)) { // might add flag for black/white or black/red or green/white later if time permits
            if (piecesOnDark) {
                return Color.WHITE;
            } else {
                return Color.BLACK;
            }
        } else if (square.equalsType(Board.Square.IN_PLAY)) {
            if (piecesOnDark) {
                return Color.BLACK;
            } else {
                return Color.WHITE;
            }
        } else {
            throw new RuntimeException("cannot get color for point=" + point + " square=" + square);
        }
    }

    public Checker getChecker(Piece piece) {
        return (Checker) piece;
    }

    public Color getColorForPiece(Piece piece) {
        return getColorForChecker(getChecker(piece));
    }

    public Color getColorForChecker(Checker piece) { // might add flag for black v red or red v white later, depending on time
        if (piece.isSide(CheckerSide.BLACK)) {
            return Color.BLACK;
        } else if (piece.isSide(CheckerSide.RED)) {
            return Color.RED;
        } else {
            throw new RuntimeException("cannot get color for piece=" + piece + " side=" + piece.getSide());
        }
    }

    public Color getColorForPieceBorder() {
        return Color.WHITE;
    }

    public Color getColorForKingMarker() {
        return Color.WHITE;
    }

    @Override
    public void draw(int cx, int cy, Piece piece, Graphics g, int squareSize, int pieceSize) {

        final int x = cx - pieceSize / 2;
        final int y = cy - pieceSize / 2;

        g.setColor(getColorForPiece(piece));
        g.fillOval(x,  y, pieceSize, pieceSize);
        g.setColor(getColorForPieceBorder());
        g.drawOval(x,  y, pieceSize, pieceSize);

        if (getChecker(piece).isKing()) {
            Color c = getColorForKingMarker();
            g.setColor(c);
            final String marker = "K";

            // adjust cx, cy for size of the "K"
            java.awt.FontMetrics fm = g.getFontMetrics(g.getFont());
            Rectangle2D bounds = fm.getStringBounds(marker, g);
            int width = (int) bounds.getWidth();
            int height = (int) bounds.getHeight();
            int atx = cx - (width / 2);
            int aty = cy + (height / 3); // TODO: /2 seems "too low"
            g.drawString(marker, atx, aty);
        }
    }












}
