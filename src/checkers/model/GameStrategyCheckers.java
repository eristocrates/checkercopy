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
    private boolean jumpMandate = false;
    private boolean forwardLeftMove;
    private boolean forwardRightMove;
    private boolean forwardLeftJump;
    private boolean forwardRightJump;
    private boolean backwardLeftMove;
    private boolean backwardRightMove;
    private boolean backwardLeftJump;
    private boolean backwardRightJump;
    List<Point> pointsWithJumps = new ArrayList<>();

    // check every piece to see if one has a jump and only allow that one to be moved
    public List<Point> generatePointsInColor(CheckerSide targetColor) {
        List<Point> ret = new ArrayList<>();
        for (Point point : board.generatePointsInPlay()) { // loops through every dark point/square on the board
            if (getPiece(point) != null && getPiece(point).getSide().equalsType(targetColor)){ // find points of pieces of the target color
                ret.add(point);
            }
            if (ret.size() == 12){ // there's only 12 pieces of a color to find.
                break; // sure i could've not used a range-based for loop, so sue me lol
            }
        }
        return ret;
    }

    public void flagPossibleMoves(Point point){
        int forward; // helps in calculating y axis directions
        if (darkerTurn) {
            currentTurn = CheckerSide.BLACK;
            forward = 1;
        } else {
            currentTurn = CheckerSide.RED;
            forward = -1;
        }

        //flags for possible moves/jumps
        forwardLeftMove = false;
        forwardRightMove = false;
        forwardLeftJump = false;
        forwardRightJump = false;
        backwardLeftMove = false;
        backwardRightMove = false;
        backwardLeftJump = false;
        backwardRightJump = false;


        // check if point is on the mandatory list, else do below later
        if (getPiece(point) != null){ // makes sure the clicked point has a piece
            if (getPiece(point).isSide(currentTurn)) { // and its the turn order's piece
                // calculate points for possible moves
                forwardLeftDiagonal = new Point((int) point.getX() - 1, (int) point.getY() + forward);
                forwardRightDiagonal = new Point((int) point.getX() + 1, (int) point.getY() + forward);
                forwardLeftLanding = new Point((int) point.getX() - 2, (int) point.getY() + (2 * forward));
                forwardRightLanding = new Point((int) point.getX() + 2, (int) point.getY() + (2 * forward));
                backwardLeftDiagonal = new Point((int) point.getX() - 1, (int) point.getY() + (-1 * forward));
                backwardRightDiagonal = new Point((int) point.getX() + 1, (int) point.getY() + (-1 * forward));
                backwardLeftLanding = new Point((int) point.getX() - 2, (int) point.getY() + (-2 * forward));
                backwardRightLanding = new Point((int) point.getX() + 2, (int) point.getY() + (-2 * forward));

                // calculate if any moves are available
                if (!getSquare(forwardLeftDiagonal).equalsType(Board.Square.NOT_VALID_COORDINATES) // there's a square ahead
                        && getPiece(forwardLeftDiagonal) == null) { // that's empty
                    forwardLeftMove = true;
                }
                if (!getSquare(forwardRightDiagonal).equalsType(Board.Square.NOT_VALID_COORDINATES) // there's a square ahead
                        && getPiece(forwardRightDiagonal) == null) { // that's empty
                    forwardRightMove = true;
                }
                if (!getSquare(forwardLeftDiagonal).equalsType(Board.Square.NOT_VALID_COORDINATES) // there's a square ahead
                        && getPiece(forwardLeftDiagonal) != null // that has a piece
                        && !getPiece(forwardLeftDiagonal).getSide().equalsType(getPiece(point).getSide()) // that's the other color
                        && (!getSquare(forwardLeftLanding).equalsType(Board.Square.NOT_VALID_COORDINATES) // and a square to land on
                        && getPiece(forwardLeftLanding) == null)) { // that's empty
                    forwardLeftJump = true;
                }
                if (!getSquare(forwardRightDiagonal).equalsType(Board.Square.NOT_VALID_COORDINATES) // there's a square ahead
                        && getPiece(forwardRightDiagonal) != null// that has a piece
                        && !getPiece(forwardRightDiagonal).getSide().equalsType(getPiece(point).getSide()) // that's the other color
                        && (!getSquare(forwardRightLanding).equalsType(Board.Square.NOT_VALID_COORDINATES) // and a square to land on
                        && getPiece(forwardRightLanding) == null)) { // that's empty
                    forwardRightJump = true;
                }
                if (getPiece(point).isKing()){
                    if (!getSquare(backwardLeftDiagonal).equalsType(Board.Square.NOT_VALID_COORDINATES) // there's a square behind
                            && getPiece(backwardLeftDiagonal) == null) { // that's empty
                        backwardLeftMove = true;
                    }
                    if (!getSquare(backwardRightDiagonal).equalsType(Board.Square.NOT_VALID_COORDINATES) // there's a square behind
                            && getPiece(backwardRightDiagonal) == null) { // that's empty
                        backwardRightMove = true;
                    }
                    if (!getSquare(backwardLeftDiagonal).equalsType(Board.Square.NOT_VALID_COORDINATES) // there's a square behind
                            && getPiece(backwardLeftDiagonal) != null // that has a piece
                            && !getPiece(backwardLeftDiagonal).getSide().equalsType(getPiece(point).getSide()) // that's the other color
                            && (!getSquare(backwardLeftLanding).equalsType(Board.Square.NOT_VALID_COORDINATES) // and a square to land on
                            && getPiece(backwardLeftLanding) == null)){ // that's empty
                        backwardLeftJump = true;
                    }
                    if (!getSquare(backwardRightDiagonal).equalsType(Board.Square.NOT_VALID_COORDINATES) // there's a square behind
                            && getPiece(backwardRightDiagonal) != null // that has a piece
                            && !getPiece(backwardRightDiagonal).getSide().equalsType(getPiece(point).getSide()) // that's the other color
                            && (!getSquare(backwardRightLanding).equalsType(Board.Square.NOT_VALID_COORDINATES) // and a square to land on
                            && getPiece(backwardRightLanding) == null)){ // that's empty
                        backwardRightJump = true;
                    }
                }
            }
        }
    }
    // somehow also needs to deal with the possibility of multiple possible jumps
    // maybe it returns a list of pieces with mandatory jumps, flags that there is a mandatory move and canmovepieceatpoint checks if point is in the list
    @Override
    public boolean canMovePieceAtPoint(Point point) {
        pointsWithJumps.clear();
        for (Point pointInColor : generatePointsInColor(getPiece(point).getSide())) { // loops through every point of the same color as parameter
            flagPossibleMoves(pointInColor);
            if ((forwardLeftJump || forwardRightJump) || (getPiece(pointInColor).isKing() && backwardLeftJump || backwardRightJump)){ // if a piece is found to have a jump
                pointsWithJumps.add(pointInColor);
            }
        }
        System.out.print(" current turn=" + currentTurn + " ");
        System.out.print(" points with jumps=" + pointsWithJumps + " ");
        if (pointsWithJumps.size() != 0) { // if there are points than can jump
            if (pointsWithJumps.contains(point)) { // only validate moving a point that has a mandatory jump
                flagPossibleMoves(point); // resets flags with originally chosen point
                return true;
            } else{
                return false;
            }
        }else{ // if there are no points that can jump
                flagPossibleMoves(point); // resets flags with originally chosen point
                return forwardLeftMove || forwardRightMove || forwardLeftJump || forwardRightJump || backwardLeftMove || backwardRightMove || backwardLeftJump || backwardRightJump;
            }
        }

    public boolean isAvailableTargetForMove(Point from, Point to) { // checks if target point landing is valid
        boolean ret = false;
        if (getPiece(from).isKing()) {
            jumpMandate = forwardLeftJump || forwardRightJump || backwardLeftJump || backwardRightJump;
        } else {
            jumpMandate = forwardLeftJump || forwardRightJump;
        }

        if (Board.Square.DARK.equalsType(getSquare(to))) { // target is a dark square
            if (getPiece(to) == null){ // that is empty

                if (!jumpMandate) { // disables simple moves when jump is found
                    if (forwardLeftMove && to.equals(forwardLeftDiagonal)) {
                        ret = true;
                    }
                    if (forwardRightMove && to.equals(forwardRightDiagonal)) {
                        ret = true;
                    }
                    if (getPiece(from).isKing()) {
                        if (to.equals(backwardLeftDiagonal)) {
                            ret = true;
                        }
                        if (to.equals(backwardRightDiagonal)) {
                            ret = true;
                        }
                    }
                }

                //capture handling
                // check that get getPiece is from the other color
                // better yet, see if previously used flags can be used here
                if (forwardLeftJump && to.equals(forwardLeftLanding)) { // forward left capture
                    capturedPiece.setLocation(forwardLeftDiagonal);
                    ret = true;
                    System.out.print("forwardLeftJump =" + forwardLeftJump + " ");
                }
                if (forwardRightJump && to.equals(forwardRightLanding)) { // forward right capture
                    capturedPiece.setLocation(forwardRightDiagonal);
                    ret = true;
                }
                if (getPiece(from).isKing()) {
                    if (backwardLeftJump && to.equals(backwardLeftLanding)) { // backward left capture
                        capturedPiece.setLocation(backwardLeftDiagonal);
                        ret = true;
                    }
                    if (backwardRightJump && to.equals(backwardRightLanding)) { // backward right capture
                        capturedPiece.setLocation(backwardRightDiagonal);
                        ret = true;
                    }
                }



                // handle promotion
                if (ret) {
                    if (getPiece(from).getSide().equalsType(CheckerSide.BLACK) // a black piece
                            && getPiece(from).getType().equalsType(CheckerType.MAN) // that's a man
                            && to.getY() == 8) { // getting to the bottom row
                        getPiece(from).setKing();
                    }
                    if (getPiece(from).getSide().equalsType(CheckerSide.RED) // a red piece
                            && getPiece(from).getType().equalsType(CheckerType.MAN) // that's a man
                            && to.getY() == 1) { //getting to the top row
                        getPiece(from).setKing();
                    }
                }
            }
        }
        System.out.println("isAvailable(" + to + ") ret=" + ret); // printed twice due to both movePiece & isValidToMove calling isAvailableTargetForMove
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
                    board.putPoint2Piece(to, piece);


                    if (!over.equals(new Point(0, 0))){
                        board.removePoint2Piece(over);
                        pointsWithJumps.clear();
                        for (Point pointInColor : generatePointsInColor(getPiece(to).getSide())) { // loops through every point of the same color as parameter
                            flagPossibleMoves(pointInColor);
                            if (forwardLeftJump || forwardRightJump || backwardLeftJump || backwardRightJump) { // if a piece is found to have a jump
                                pointsWithJumps.add(pointInColor);
                            }
                        }
                    }

                    /*
                    if (jumpMandate) {
                        pointsWithJumps.clear();
                        for (Point pointInColor : generatePointsInColor(getPiece(to).getSide())) { // loops through every point of the same color as parameter
                            flagPossibleMoves(pointInColor);
                            if (forwardLeftJump || forwardRightJump || backwardLeftJump || backwardRightJump) { // if a piece is found to have a jump
                                pointsWithJumps.add(pointInColor);
                            }
                        }
                    }
                    */
                    if (pointsWithJumps.size() == 0) { // only swaps turns after all mandatory jumps
                        darkerTurn = !darkerTurn;
                    }
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
