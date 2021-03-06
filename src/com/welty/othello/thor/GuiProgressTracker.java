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

package com.welty.othello.thor;

import com.orbanova.common.misc.Engineering;
import com.orbanova.common.misc.Require;

import javax.swing.*;
import java.awt.*;

/**
 * Dialog where progress information is displayed.
 * <p/>
 * This must be constructed on the EventDispatchThread. The operation being tracked must call increment also on the EDT.
 * If the work is also done on the EDT, this means the EDT will block.
 * <p/>
 * Progress is displayed as a number (in Engineering format) followed by a suffix.
 * For example, "12,345k games loaded". In this case, " games loaded" is the suffix,
 * and "12,345k" is the Engineering representation of the number of games.
 */
public class GuiProgressTracker implements IndeterminateProgressTracker {
    private static final int UPDATE_MILLIS = 500;
    private long progress = 0;
    private final JLabel label;
    private final JDialog dialog;
    private final String suffix;
    private long nextUpdate = System.currentTimeMillis() + UPDATE_MILLIS;

    /**
     * Construct a Monitor.
     * <p/>
     * Must be constructed on the EDT. See {@link GuiProgressTracker} for complete usage details.
     *
     * @param suffix suffix for progress display
     */
    public GuiProgressTracker(final String suffix) {
        this.suffix = suffix;
        Require.isTrue(SwingUtilities.isEventDispatchThread(), "Must be constructed on EDT");
        dialog = new JDialog(null, "Loading Games", Dialog.ModalityType.MODELESS);
        dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        label = new JLabel("0 " + suffix);
        label.setPreferredSize(new Dimension(300, 100));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        dialog.add(label);
        dialog.pack();
        dialog.setVisible(true);
    }

    @Override public void increment() {
        progress++;
        if (System.currentTimeMillis() > nextUpdate) {
            update();
        }
    }

    @Override public void update() {
        nextUpdate = System.currentTimeMillis() + UPDATE_MILLIS;
        dialog.validate();
        label.setText(Engineering.compactFormat(progress) + suffix);
        label.paintImmediately(0, 0, 1000, 1000);
    }

    @Override public void close() {
        dialog.setVisible(false);
        dialog.dispose();
    }
}
