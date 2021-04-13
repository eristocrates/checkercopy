package checkers.gui;

import checkers.model.Board;
import checkers.model.Piece;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;

public class BoardPainter extends JComponent {
    private static final long serialVersionUID = 1L;
    private Board board;

    private PiecePaintStrategy piecePainter; // helps with square color
    private int currentPieceSize;
    private DragHelper dragHelper; // holds relevant drag-drop data


    public BoardPainter(Board board, PiecePaintStrategy piecePainter) { // constructor
        super();
        this.board = board;
        this.currentPieceSize = 50;
        this.piecePainter = piecePainter;
        dragHelper = new DragHelper(); // track dragging, set up listeners, etc.:
    }

    public int getCurrentPieceSize() {
        return currentPieceSize;
    }

    public int getSquareSize() {
        return (int) (getCurrentPieceSize() * 1.25); // 25% bigger than piece
    }

    @Override
    public Dimension getPreferredSize() { // pretty sure we can fit resizing window here later
        int x = board.getSizeX() * getSquareSize();
        int y = board.getSizeY() * getSquareSize();
        return new Dimension(x, y);
    }

    @Override
    protected void paintComponent(Graphics g) {

        paintCheckerBoard(g);

        paintPieces(g);
    }

    private void paintCheckerBoard(Graphics g) {
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        // AWESOME: if you setRenderingHints, then there is a chance that
        //          the 1st square will stay black until a refresh!!!
        // This "useless" fillRect() stops that bug from showing.
        g.fillRect(0, 0, 1, 1);
        // Bug from 2006: https://bugs.openjdk.java.net/browse/JDK-6468831
        // Bug from 2009: https://bugs.openjdk.java.net/browse/JDK-6808062
        // If you don't set the Hints, then the "K" does not show up on king pieces


        final int squareSize = getSquareSize();

        for (Point point : board.generatePointsTopDownLeftRight()) {
            int gx = (point.x - 1) * squareSize;
            int gy = (point.y - 1) * squareSize;
            Color color = piecePainter.getColorForPoint(point);
            g.setColor(color);
            g.fillRect(gx, gy, squareSize, squareSize);
        }
    }

    private void paintPieces(Graphics g) {
        paintPiecesNotBeingDragged(g);
        paintPieceBeingDragged(g);
    }

    private void paintPiecesNotBeingDragged(Graphics g) {
        for (Point point : board.generatePointsTopDownLeftRight()) {
            Piece piece = board.getPiece(point);
            if (piece != null) {
                if (!dragHelper.isPieceBeingDragged(piece)) {
                    drawPiece(point, piece, g);
                }
            }
        }
    }

    private void paintPieceBeingDragged(Graphics g) {
        dragHelper.paintPieceBeingDragged(g);
    }


    public Point getPointFromGuiXY(int guiX, int guiY) {
        int over = guiX / getSquareSize();
        int down = guiY / getSquareSize();
        Point ret = new Point(over + 1, down + 1);
        return ret;
    }

    public void drawPiece(Point point, Piece piece, Graphics g) {
        final int squareSize = getSquareSize();

        int cx = (point.x - 1) * squareSize + squareSize / 2;
        int cy = (point.y - 1) * squareSize + squareSize / 2;

        drawPiece(cx, cy, piece, g);
    }

    // "lower level" draw, used by "Dragging" code.
    // allows piece to be drawn not centered on a square.
    public void drawPiece(int cx, int cy, Piece piece, Graphics g) {
        final int squareSize = getSquareSize();
        final int pieceSize = getCurrentPieceSize();

        piecePainter.draw(cx, cy, piece, g, squareSize, pieceSize);
    }


    private class DragHelper { // "instance" inner class, so it has access to outer
        private boolean inDrag = false;
        private Point dragging = null;
        private int draggingCx = 0;
        private int draggingCy = 0;

        public DragHelper() {
            addMouseListener(getMouseAdapter());
            addMouseMotionListener(getMouseMotionAdapter());
        }

        public void triggerRepaint() {
            repaint();
        }

        public void paintPieceBeingDragged(Graphics g) {
            if (inDrag) {
                if (dragging != null) {
                    Piece piece = board.getPiece(dragging);
                    drawPiece(draggingCx, draggingCy, piece, g);
                } else {
                    throw new RuntimeException("indrag is true, but dragging is null");
                }
            } else {
                // ok, not in drag mode
            }

        }

        public Piece getPieceBeingDragged() {
            if (dragging != null) {
                return board.getPiece(dragging);
            } else {
                return null;
            }
        }

        public boolean isPieceBeingDragged(Piece piece) {
            if (piece.equals(getPieceBeingDragged())) {
                return true;
            } else {
                return false;
            }
        }
        public Point findPointForXY(int guiX, int guiY) {
            return getPointFromGuiXY(guiX, guiY);
        }
        public MouseListener getMouseAdapter() {
            return new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent me) {
                    final int x = me.getX();
                    final int y = me.getY();
                    // Locate the board Point under the mouse press
                    Point point = findPointForXY(x, y);
                    if (point != null) {
                        // Check for piece at that Point
                        if (board.canMovePieceAtPoint(point)) {
                            // start dragging:
                            dragging = point;
                            inDrag = true;
                        }
                    }
                    System.out.println("me x=" + x + " y=" + y + " inDrag=" + inDrag + " dragging=" + dragging);
                }

                @Override
                public void mouseReleased(MouseEvent me) {
                    if (!inDrag) {
                        return;
                    }


                    // When mouse is released, clear inDrag no matter what
                    // But for now, don't clear the 'dragging' variable

                    inDrag = false;
                    // dragging = null;

                    final int x = me.getX();
                    final int y = me.getY();
                    Point point = findPointForXY(x, y);

                    if (board.isValidToMove(dragging, point)) {
                        board.movePiece(dragging, point);
                    } else {
                        // nothing  TODO: better option?
                    }
                    // later could handle getting point for
                    dragging = null;

                    triggerRepaint();
                }
            };
        }
        public MouseMotionAdapter getMouseMotionAdapter() {
            return new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent me) {
                    if (inDrag) {
                        draggingCx = me.getX(); // - deltaX;
                        draggingCy = me.getY(); //  - deltaY;
                        triggerRepaint();
                    }
                }
            };
        }
    }























}
