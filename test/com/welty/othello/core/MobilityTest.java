/*
 * Copyright (c) 2014 Chris Welty.
 *
 * This is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License, version 3,
 * as published by the Free Software Foundation.
 *
 * This file is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * For the license, see <http://www.gnu.org/licenses/gpl.html>.
 */

package com.welty.othello.core;

import junit.framework.TestCase;

/**
 * Created by IntelliJ IDEA.
 * User: HP_Administrator
 * Date: May 2, 2009
 * Time: 9:02:13 AM
 * To change this template use File | Settings | File Templates.
 */
public class MobilityTest extends TestCase {
    public void testMoves() {
        CBitBoard board = new CBitBoard("---------------------------O*------*O---------------------------", true);
        final long mover = board.getMover();
        final long empty = board.getEmpty();
        assertEquals(0x0000102004080000L, Mobility.calcMoves(mover, empty));
        assertEquals(0x0000080420100000L, Mobility.calcMoves(board.getEnemy(), empty));

        // test to ensure we don't wrap around
        board = new CBitBoard("-------- O*------ ------*O -------O *------- -------* O------- --------", true);
        assertEquals(0, Mobility.calcMoves(board.getMover(), board.getEmpty()));

        // test diagonals
        board = new CBitBoard("*------* -O----O- -------- -------- -------- -------- -O----O- *------*", true);
        assertEquals(0x0000240000240000L, Mobility.calcMoves(board.getMover(), board.getEmpty()));

        // Test the maximum number of enemy discs (6)
        board = new CBitBoard("*OOOOOO- -------- -------- -------- -------- -------- -------- --------", true);
        assertEquals(0x80L, Mobility.calcMoves(board.getMover(), board.getEmpty()));
    }
}
