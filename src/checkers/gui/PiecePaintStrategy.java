package checkers.gui;

import checkers.model.Piece;

import java.awt.*;

public interface PiecePaintStrategy {
    void draw(int cx, int cy, Piece piece, Graphics g, int squareSize, int pieceSize);

    Color getColorForPoint(Point point);

}
