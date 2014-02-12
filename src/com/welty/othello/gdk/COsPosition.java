package com.welty.othello.gdk;

import com.orbanova.common.misc.Require;
import org.jetbrains.annotations.NotNull;

/**
 * Board position and clocks from a GGF game
 * <PRE>
 * User: Chris
 * Date: May 2, 2009
 * Time: 3:46:38 PM
 * </PRE>
 */
public class COsPosition {
    public final OsBoard board;
    private @NotNull OsClock blackClock;
    private @NotNull OsClock whiteClock;

    public COsPosition() {
        this(new OsBoard(), OsClock.DEFAULT, OsClock.DEFAULT);
    }

    public COsPosition(OsBoard board, @NotNull OsClock whiteClock, @NotNull OsClock blackClock) {
        this.board = new OsBoard(board);
        this.blackClock = blackClock;
        this.whiteClock = whiteClock;
    }

    /**
     * Copy constructor. Deep copy.
     */
    public COsPosition(COsPosition posStart) {
        board = new OsBoard(posStart.board);
        copyClocks(posStart);
    }

    private void copyClocks(COsPosition pos) {
        blackClock = pos.getBlackClock();
        whiteClock = pos.getWhiteClock();
    }

    /**
     * Update the board position and clocks after a move
     *
     * @param mli move details
     */
    void append(final OsMoveListItem mli) {
        updateClock(mli, board.isBlackMove());
        board.update(mli.move);
    }

    private void updateClock(OsMoveListItem mli, boolean blackMove) {
        if (blackMove) {
            blackClock = blackClock.update(mli.getElapsedTime());
        } else {
            whiteClock = whiteClock.update(mli.getElapsedTime());
        }
    }

    void Update(final COsMoveList ml, int nMoves) {
        int i;
        if (nMoves > ml.size())
            nMoves = ml.size();
        for (i = 0; i < nMoves; i++)
            append(ml.get(i));
    }

    /**
     * UpdateKomiSet() is called in a komi game to set the first move
     * choices of both players.
     * It updates the clock of the non-mover. The mover's clock
     * is updated by a call to Update().
     */
    void UpdateKomiSet(final OsMoveListItem[] mlis) {
        boolean fBlackOpponent = !board.isBlackMove();
        final int enemyIndex = fBlackOpponent ? 1 : 0;
        updateClock(mlis[enemyIndex], fBlackOpponent);
    }

    public void Calculate(final COsGame game, int nMoves) {
        final COsPosition posStart = game.GetPosStart();
        board.copy(posStart.board);
        copyClocks(posStart);

        if (nMoves != 0 && game.mt.fKomi) {
            Require.isTrue(!game.NeedsKomi(), "Needs komi as first move");

            UpdateKomiSet(game.mlisKomi);
        }
        Update(game.getMoveList(), nMoves);
    }

    void Clear() {
        board.clear();
        blackClock = OsClock.DEFAULT;
        whiteClock = OsClock.DEFAULT;
    }

    @Override public boolean equals(Object obj) {
        if (obj instanceof COsPosition) {
            COsPosition pos = (COsPosition) obj;
            return board.equals(pos.board) && blackClock.equals(pos.blackClock) && whiteClock.equals(pos.whiteClock);
        } else {
            return false;
        }
    }

    @Override public String toString() {
        return board.toString() + " : [" + blackClock + ", " + whiteClock + "]";
    }

    public void setBlackClock(@NotNull OsClock blackClock) {
        this.blackClock = blackClock;
    }

    public @NotNull OsClock getBlackClock() {
        return blackClock;
    }

    public void setWhiteClock(@NotNull OsClock whiteClock) {
        this.whiteClock = whiteClock;
    }

    public @NotNull OsClock getWhiteClock() {
        return whiteClock;
    }

    public @NotNull OsClock getCurrentClock() {
        return board.isBlackMove() ? getBlackClock() : getWhiteClock();
    }
}
