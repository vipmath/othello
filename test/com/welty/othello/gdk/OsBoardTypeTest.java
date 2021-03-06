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

package com.welty.othello.gdk;

import com.welty.othello.c.CReader;
import junit.framework.TestCase;

/**
 * Created by IntelliJ IDEA.
 * User: HP_Administrator
 * Date: May 2, 2009
 * Time: 1:15:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class OsBoardTypeTest extends TestCase {
    public void testEquals() {
        final OsBoardType a = new OsBoardType("10");
        final OsBoardType b = new OsBoardType("10");
        final OsBoardType c = new OsBoardType("8");
        final OsBoardType d = new OsBoardType("8");

        assertEquals(a, a);
        assertEquals(a, b);
        assertFalse(a.equals(c));
        assertFalse(a.equals(d));
    }

    public void testIn() {
        testIn("8", 8, false, 64);
        testIn("6", 6, false, 36);
        testIn("88", 10, true, 88);
        testIn("8r", 8, false, 64); // ignore subsequent characters
        testIn("8 ", 8, false, 64);
    }

    private static void testIn(String s, int size, boolean octo, int nSquares) {
        final OsBoardType bt = new OsBoardType(new CReader(s));
        assertEquals(size, bt.n);
        assertEquals(octo, bt.octo);
        assertEquals(nSquares, bt.nPlayableSquares());
    }
}
