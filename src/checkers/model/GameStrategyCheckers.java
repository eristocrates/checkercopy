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
            List<String> ret = new ArrayList<>();
            for (int i = 0, n = s.length(); i < n; i++) {
                ret.add(s.substring(i, i+1));
            }
            return ret;
        }
        return Arrays.asList(split);
    }

    @Override
    public Checker createPieceFromSingleString(String s) {
        return Checker.createFromSingleString(s);
    }

    @Override
    public String convertPointToDumpString(Point point) { // used for snafus
        String cell;

        final Board.Square square = getSquare(point); // square refers to the enum type of a point's state
        if (square.equalsType(Board.Square.NOT_VALID_COORDINATES)) {
            cell = "<ERROR at point=" + point;
        } else if (square.equalsType(Board.Square.LIGHT)) {
            cell = " ";
        } else if (square.equalsType(Board.Square.DARK)) {
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

    private boolean darkerTurn = true;

    private CheckerSide currentTurn;
    private Point forwardLeftDiagonal;
    private Point forwardRightDiagonal;
    private Point forwardLeftLanding;
    private Point forwardRightLanding;
    private Point backwardLeftDiagonal;
    private Point backwardRightDiagonal;
    private Point backwardLeftLanding;
    private Point backwardRightLanding;
    private final Point capturedPiece = new Point();
    private boolean jumpMandate;

    // check every piece to see if one has a jump and only allow that one to be moved
    //public boolean mandatoryCheck()
    // somehow also needs to deal with the possibility of mutliple possible jumps
    // maybe it returns a list of pieces with mandatory jumps, flags that there is a mandatory move and canmovepieceatpoint checks if point is in the list
    @Override
    public boolean canMovePieceAtPoint(Point point) {
        int forward; // helps in calculating y axis diretions
        jumpMandate = false;
        if (darkerTurn) {
            currentTurn = CheckerSide.BLACK;
            forward = 1;
        } else {
            currentTurn = CheckerSide.RED;
            forward = -1;
        }
        System.out.print(" current turn=" + currentTurn + " ");



        if (getPiece(point) != null){ // makes sure the clicked point has a piece
            if (getPiece(point).isSide(currentTurn)){ // and its the turn order's piece
                // calculate points for possible moves
                forwardLeftDiagonal = new Point((int)point.getX() - 1, (int)point.getY() + forward);
                forwardRightDiagonal = new Point((int)point.getX() + 1, (int)point.getY() + forward);
                forwardLeftLanding = new Point((int)point.getX() - 2, (int)point.getY() + (2 * forward));
                forwardRightLanding = new Point((int)point.getX() + 2, (int)point.getY() + (2 * forward));
                backwardLeftDiagonal = new Point((int)point.getX() - 1, (int)point.getY() + (-1 * forward));
                backwardRightDiagonal = new Point((int)point.getX() + 1, (int)point.getY() + (-1 * forward));
                backwardLeftLanding = new Point((int)point.getX() - 2, (int)point.getY() + (-2 * forward));
                backwardRightLanding = new Point((int)point.getX() + 2, (int)point.getY() + (-2 * forward));
                if ((!getSquare(forwardLeftDiagonal).equalsType(Board.Square.NOT_VALID_COORDINATES) && getPiece(forwardLeftDiagonal) == null) // there is at least an open diagonal in front
                        || (!getSquare(forwardRightDiagonal).equalsType(Board.Square.NOT_VALID_COORDINATES) && getPiece(forwardRightDiagonal) == null)){
                    return true;
                }else if((!getSquare(forwardLeftDiagonal).equalsType(Board.Square.NOT_VALID_COORDINATES) // there's a square ahead
                        && getPiece(forwardLeftDiagonal) != null // theres a piece in front
                        && !getPiece(forwardLeftDiagonal).getSide().equalsType(getPiece(point).getSide()) // that's the other color
                        && (!getSquare(forwardLeftLanding).equalsType(Board.Square.NOT_VALID_COORDINATES) // there's a landing square
                        && getPiece(forwardLeftLanding) == null)) // that's empty
                        || (!getSquare(forwardRightDiagonal).equalsType(Board.Square.NOT_VALID_COORDINATES) // there's a square ahead
                        && getPiece(forwardRightDiagonal) != null// theres a piece in front
                        && !getPiece(forwardRightDiagonal).getSide().equalsType(getPiece(point).getSide()) // that's the other color
                        && (!getSquare(forwardRightLanding).equalsType(Board.Square.NOT_VALID_COORDINATES) // there's a landing square
                        && getPiece(forwardRightLanding) == null))){ // that's empty
                    return true;
                }else if (getPiece(point).isKing() // if king, check backwards as well
                        && ((!getSquare(backwardLeftDiagonal).equalsType(Board.Square.NOT_VALID_COORDINATES) && getPiece(backwardLeftDiagonal) == null) // there is at least an open diagonal in back
                                || (!getSquare(backwardRightDiagonal).equalsType(Board.Square.NOT_VALID_COORDINATES) && getPiece(backwardRightDiagonal) == null))){
                    return true;
                }else if((getPiece(point).isKing() // if king, check backwards as well
                        && !getSquare(backwardLeftDiagonal).equalsType(Board.Square.NOT_VALID_COORDINATES) // there's a square behind
                        && getPiece(backwardLeftDiagonal) != null // theres a piece in back
                        && !getPiece(backwardLeftDiagonal).getSide().equalsType(getPiece(point).getSide()) // that's the other color
                        && (!getSquare(backwardLeftLanding).equalsType(Board.Square.NOT_VALID_COORDINATES) // there's a landing square
                        && getPiece(backwardLeftLanding) == null)) // that's empty
                        || (!getSquare(backwardRightDiagonal).equalsType(Board.Square.NOT_VALID_COORDINATES) // there's a square behind
                        && getPiece(backwardRightDiagonal) != null// theres a piece in back
                        && !getPiece(backwardRightDiagonal).getSide().equalsType(getPiece(point).getSide()) // that's the other color
                        && (!getSquare(backwardRightLanding).equalsType(Board.Square.NOT_VALID_COORDINATES) // there's a landing square
                        && getPiece(backwardRightLanding) == null))){ // that's empty
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isAvailableTargetForMove(Point from, Point to) { // checks if target point landing is valid
        boolean ret = false;
        if((!getSquare(forwardLeftDiagonal).equalsType(Board.Square.NOT_VALID_COORDINATES) // there's a square ahead
                && getPiece(forwardLeftDiagonal) != null // theres a piece in front
                && !getPiece(forwardLeftDiagonal).getSide().equalsType(getPiece(from).getSide()) // that's the other color
                && (!getSquare(forwardLeftLanding).equalsType(Board.Square.NOT_VALID_COORDINATES) // there's a landing square
                && getPiece(forwardLeftLanding) == null)) // that's empty
                || (!getSquare(forwardRightDiagonal).equalsType(Board.Square.NOT_VALID_COORDINATES) // there's a square ahead
                && getPiece(forwardRightDiagonal) != null// theres a piece in front
                && !getPiece(forwardRightDiagonal).getSide().equalsType(getPiece(from).getSide()) // that's the other color
                && (!getSquare(forwardRightLanding).equalsType(Board.Square.NOT_VALID_COORDINATES) // there's a landing square
                && getPiece(forwardRightLanding) == null))) { // that's empty
            jumpMandate = true;
        }else if((getPiece(from).isKing() // if king, check backwards as well
                && !getSquare(backwardLeftDiagonal).equalsType(Board.Square.NOT_VALID_COORDINATES) // there's a square behind
                && getPiece(backwardLeftDiagonal) != null // theres a piece in back
                && !getPiece(backwardLeftDiagonal).getSide().equalsType(getPiece(from).getSide()) // that's the other color
                && (!getSquare(backwardLeftLanding).equalsType(Board.Square.NOT_VALID_COORDINATES) // there's a landing square
                && getPiece(backwardLeftLanding) == null)) // that's empty
                || (!getSquare(backwardRightDiagonal).equalsType(Board.Square.NOT_VALID_COORDINATES) // there's a square behind
                && getPiece(backwardRightDiagonal) != null// theres a piece in back
                && !getPiece(backwardRightDiagonal).getSide().equalsType(getPiece(from).getSide()) // that's the other color
                && (!getSquare(backwardRightLanding).equalsType(Board.Square.NOT_VALID_COORDINATES) // there's a landing square
                && getPiece(backwardRightLanding) == null))) { // that's empty
            jumpMandate = true;
        }

        if (Board.Square.DARK.equalsType(getSquare(to))) { // target is a dark square
            if (getPiece(to) == null){ // that is empty

                //capture handling
                if (to.equals(forwardLeftLanding) && getPiece(forwardLeftDiagonal) != null) { // forward left capture
                    capturedPiece.setLocation(forwardLeftDiagonal);
                    ret = true;
                }else if (to.equals(forwardRightLanding) && getPiece(forwardRightDiagonal) != null) { // forward right capture
                    capturedPiece.setLocation(forwardRightDiagonal);
                    ret = true;
                }else if (getPiece(from).isKing() && to.equals(backwardLeftLanding) && getPiece(backwardLeftDiagonal) != null){ // backward left capture
                    capturedPiece.setLocation(backwardLeftDiagonal);
                    ret = true;
                }else if (getPiece(from).isKing() && to.equals(backwardRightLanding) && getPiece(backwardRightDiagonal) != null){ // backward right capture
                    capturedPiece.setLocation(backwardRightDiagonal);
                    ret = true;
                }

                if (!jumpMandate) { // disables simple move when jump is found
                    if (to.equals(forwardLeftDiagonal) || to.equals(forwardRightDiagonal)) { // and one diagonal move forward
                        ret = true;
                    } else if (getPiece(from).isKing()
                            && (to.equals(backwardLeftDiagonal)
                            || to.equals(backwardRightDiagonal))) { // or if king diagonal backward
                        ret = true;
                    }
                }


                // handle promotion
                if (ret // successful move
                        && getPiece(from).getSide().equalsType(CheckerSide.BLACK) // of a black piece
                        && getPiece(from).getType().equalsType(CheckerType.MAN) // that's a man
                        && to.getY() == 8){ // to the bottom row
                    getPiece(from).setKing();
                }else if (ret // successful move
                        && getPiece(from).getSide().equalsType(CheckerSide.RED) // of a red piece
                        && getPiece(from).getType().equalsType(CheckerType.MAN) // that's a man
                        && to.getY() == 1){ // to the top row
                    getPiece(from).setKing();
                }
            }
        }
        System.out.println("isAvailable(" + to + ") ret=" + ret);
        return ret;
    }

    @Override
    public void movePiece(Point from, Point to) { // handles movement of one point to another
        if (jumpMandate){
            movePiece(from, to, capturedPiece);
        }else{
            movePiece(from, to, new Point(0, 0));
        }
    }

    @Override
    public void movePiece(Point from, Point to, Point over) { // handles movement of one point to another

        final Piece piece = getPiece(from);
        if (piece != null) { // makes sure there's a piece
            if (isAvailableTargetForMove(from, to)) { // is trying to be moved to a point on the board
                if (getPiece(from).isSide(currentTurn)){ //ensure turn order
                    board.removePoint2Piece(from);
                    if (!over.equals(new Point(0, 0))){
                        board.removePoint2Piece(over);
                    }
                    board.putPoint2Piece(to, piece);

                    // remember to only swap turns after all mandatory jumps later
                    darkerTurn = !darkerTurn;
                }
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
            if (isAvailableTargetForMove(from, to)) {
                //  TODO: rule check too
                return true;
            }
        }
        return false;
    }

}
