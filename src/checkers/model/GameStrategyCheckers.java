package checkers.model;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameStrategyCheckers implements GameStrategy {

    private Board board;

    @Override
    public void setBoard(Board ret) {
        board = ret;
    }

    // there's a string that represents the entire pieces on boardstate and this seperates the string
    // into an array of each relevant substring, i.e. p1 string, blank middle string,  p2 string
    @Override
    public List<String> splitBoardStateString(String s) {
        String[] split = s.split("");
        if (split.length != s.length()) {
            System.out.println("WTF splitBoardstra s.len=" + s.length() + " split.len=" + split.length);
            // TODO: WTF? split just stopped working!
            List<String> ret = new ArrayList<>();
            for (int i = 0, n = s.length(); i < n; i++) {
                ret.add(s.substring(i, i+1));
            }
            return ret;
            //throw new RuntimeException("length mismatch: s.len=" + s.length() + " but array.len=" + split.length);
        }
        return Arrays.asList(split);
    }

    @Override
    public Checker createPieceFromSingleString(String s) {
        return Checker.createFromSingleString(s);
    }

    @Override
    public String convertPointToDumpString(Point point) { // used for debugging
        String cell;

        final Board.Square square = getSquare(point); // square refers to the enum type of a point's state
        if (square.equalsType(Board.Square.NOT_VALID_COORDINATES)) {
            cell = "<ERROR at point=" + point;
        } else if (square.equalsType(Board.Square.NOT_IN_PLAY)) {
            cell = " ";
        } else if (square.equalsType(Board.Square.IN_PLAY)) {
            Checker checker = getPiece(point);
            if (checker == null) {
                cell = "_";
            } else {
                final String tmpcell;
                if (checker.isSide(CheckerSide.BLACK)) {
                    tmpcell = "b";
                } else if (checker.isSide(CheckerSide.RED)) {
                    tmpcell = "r";
                } else {
                    tmpcell = "error";
                }

                if (checker.isKing()) {
                    cell = tmpcell.toUpperCase();
                } else {
                    cell = tmpcell;
                }
            }
        } else {
            cell = "Case error point=" + point + " square=" + square;
        }

        return cell;
    }

    private Checker getPiece(Point point) {
        return (Checker) board.getPiece(point);
    }

    private Board.Square getSquare(Point point) {
        return board.getSquare(point);
    }

    @Override
    public boolean canMovePieceAtPoint(Point point) { // very important must fix later
        // TODO: better implementation - pay attention to which side has the turn,
        //       pay attention to "must move another piece" rules
        return (getPiece(point) != null);
    }

    // maybe mirror this structure for an isAvailableTargetForCaptureToMake
    public boolean isAvailableTargetForMove(Point point) { // checks if point is in play and empty
        final boolean ret;
        // later should check if either
        // should check if target is one square away
        // should check if target involves a capture
        // should check for backwards motion with an allowance flag for kings
        if (Board.Square.IN_PLAY.equalsType(getSquare(point))) {
            if (null == getPiece(point)) {
                ret = true;
            } else {
                ret = false;
            }
        } else {
            ret = false;
        }
        System.out.println("isAvailable(" + point + ") ret=" + ret);
        return ret;
    }

    @Override
    public void movePiece(Point from, Point to) { // handles movement of one point to another

        final Piece piece = getPiece(from);
        if (piece != null) {
            if (isAvailableTargetForMove(to)) {
                board.removePoint2Piece(from);
                board.putPoint2Piece(to, piece);
            } else {
                throw new RuntimeException("Programmer error - point not available, point=" + to);
            }
            // likely could add an if for a capture here later
            // could also alternate flags for turn completion?
            // king promotion could def happen here
        } else {
            throw new RuntimeException("Programmer error - no piece at original, point=" + from);
        }
    }

    @Override
    public boolean isValidToMove(Point from, Point to) { // checks if a piece is moveable
        // later should check if piece is surrounded by other pieces & the border
        // maybe enforce turn order here? an alternating flag should work
        if (getPiece(from) != null) {
            if (isAvailableTargetForMove(to)) {
                //  TODO: rule check too
                return true;
            }
        }
        return false;
    }

}
