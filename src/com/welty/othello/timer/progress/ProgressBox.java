package com.welty.othello.timer.progress;

import javax.swing.*;

/**
 * <PRE>
 * User: Chris
 * Date: Jul 25, 2009
 * Time: 8:05:21 AM
 * </PRE>
 */
public class ProgressBox extends JPanel implements ProgressListener {
    private final MyProgressBar progressBar;
    private final ProgressTask task;
    private final JLabel resultLabel;

    public static ProgressBox create(String text, ProgressTask task, ColorSet colorSet) {
        final ProgressBox box = new ProgressBox(text, task, colorSet);
        task.addListener(box);
        return box;
    }

    /**
     * Construct using create() so this is added as a listener to task
     */
    private ProgressBox(String text, ProgressTask task, ColorSet colorSet) {
        this.task = task;
        progressBar = new MyMatteProgressBar(colorSet);
        final Box box = Box.createHorizontalBox();
        box.add(Utils.createLabel(text, 80));
        box.add(progressBar);
        resultLabel = Utils.createLabel("", 80);
        resultLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        box.add(resultLabel);
        add(box);
    }

    public void handleProgress() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                progressBar.setValue((int) (task.getProgress() * 100));
            }
        });
    }

    public void setResult(final double result) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                resultLabel.setText(String.format("%.1f Mn/s", result));
            }
        });
    }

}
