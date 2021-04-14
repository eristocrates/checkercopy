package checkers.main;

import checkers.gui.BoardPainter;
import checkers.gui.PiecePaintStrategy;
import checkers.gui.PiecePaintStrategyCheckers;
import checkers.model.Board;
import checkers.model.BoardFactoryCheckers;

import javax.swing.*;

public class Checkers extends JFrame {
    public Checkers(String title) {
        super(title);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        Board board = BoardFactoryCheckers.createCheckerBoardStandardStarting();
        PiecePaintStrategy piecePainter = new PiecePaintStrategyCheckers(board);
        BoardPainter boardPaint = new BoardPainter(board, piecePainter);
        add(boardPaint);

        pack();
        setVisible(true);


    }

    public static void main(String[] args) {
        new Checkers("Checkers");
    }
}