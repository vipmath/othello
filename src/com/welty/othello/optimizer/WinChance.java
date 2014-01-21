package com.welty.othello.optimizer;

import java.io.IOException;
import java.util.ArrayList;

/**
 * <PRE>
 * User: chris
 * Date: 3/16/11
 * Time: 3:24 PM
 * </PRE>
 */
public class WinChance {
    /**
     * Print out winning percentage vs static evaluation
     *
     * @throws IOException  if unable to read file
     */
    public static void main(String[] args) throws IOException {
        final int nEmpty = 12;
        final ArrayList<Position> positions = Fitter.loadPositions(nEmpty);

        final HashFunction wins = new HashFunction();
        final HashFunction losses = new HashFunction();

        for (Position position : positions) {
            final int s = position.getStaticInt();
            if (position.searchValue > 0) {
                wins.add(s, 1);
            } else {
                losses.add(s, 1);
            }
        }

        final HashFunction fractionW = new HashFunction();
        final HashFunction fractionL = new HashFunction();

        for (Integer key : wins.combinedKeys(losses)) {
            final double w = wins.get(key);
            final double L = losses.get(key);
            fractionW.put(key, 100*(w+1)/(w+1+L));
            fractionL.put(key, 100*w/(w+1+L));
        }

        wins.dump();
        losses.dump();
        fractionL.dump();
        System.out.println("Win Percentage:");
        fractionW.dump();

        fractionW.dumpAsArray("winPercentage", 65);
    }
}
