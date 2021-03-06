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

package com.welty.othello.database;

import com.orbanova.common.feed.Feeds;
import com.orbanova.common.misc.OperatingSystem;
import com.orbanova.common.misc.Vec;
import gnu.trove.map.hash.TDoubleIntHashMap;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 */
public class GgsDownloader {

    /**
     * The URL for the archive containing GGS games
     */
    private static final String archiveUrl = "https://skatgame.net/mburo/ggs/game-archive/Othello/";

    /**
     * Download a GGS file, select games from it, and write the selected games to c:/dev/oth1/games.ggf
     * (overwriting the existing file).
     */
    public static void main(String[] args) throws IOException, CompressorException {
        select("latest.256860");
    }

    /**
     * Select games and store them.
     * <p/>
     * Downloads the games from GGS if they are not already downloaded in the cache.
     *
     * @param fileNumber game archive number, from the selection available at the archive server.
     * @throws IOException
     * @throws CompressorException
     */
    public static void select(int fileNumber) throws IOException, CompressorException {
        select(fileNumber+".ggf");
    }

    /**
     * Select games and store them.
     * <p/>
     * Downloads the games from GGS if they are not already downloaded in the cache.
     *
     * @param fileCode game 'xxx.ggf' or 'latest.xxxxxx'
     * @throws IOException
     * @throws CompressorException
     */
    private static void select(String fileCode) throws IOException, CompressorException {
        final Path destPath = downloadGgs("Othello." + fileCode + ".bz2");
        final Path outputPath = Paths.get("/home/chris/games_" + fileCode + ".ggf");

        final List<OthelloMatch> matches = Feeds.ofLines(bz2Reader(destPath))
                .map(OthelloMatch.PARSER)
                .asList();

        final List<String> gameTexts = new ArrayList<>();

        final TDoubleIntHashMap countByResult = new TDoubleIntHashMap();

        int nGames = 0;
        int n8 = 0;

        for (OthelloMatch match : matches) {
            for (OthelloGame game : match.games) {
                nGames++;
                if (nGames%1024 == 0) {
                    System.out.println((nGames>>10) + "k games");
                }
                final String type = game.type();
                // only want to add standard start position to book.
                // no comments allowed because book might freak out if game is not completely played out.
                if ((type.equals("8") || type.equals("s8") || type.equals("8k")) && game.result().comment == null) {
                    n8++;
                    if (game.blackRating() > 1900 && game.whiteRating() > 1900) {
                        gameTexts.add(game.toString());
                        countByResult.adjustOrPutValue(game.score(), 1, 1);
                    }
                }
            }
        }
        final int nFilteredGames = Vec.sum(countByResult.values());
        System.out.println("selected " + nFilteredGames + " out of " + n8 + " standard games out of " + nGames + " games");
        System.out.format("%5s %5s%n", "score", "count");
        final double[] keys = countByResult.keys();
        Arrays.sort(keys);
        for (double key : keys) {
            System.out.format("%5.0f %5d%n", key, countByResult.get(key));
        }

        Files.write(outputPath, gameTexts, Charset.defaultCharset());
    }

    private static Path downloadGgs(String sourceFile) throws IOException {
        Path cacheDir = OperatingSystem.os.getCacheDir("com.welty.othello.database");
        final Path destPath = cacheDir.resolve(sourceFile);

        Files.createDirectories(cacheDir);

        // download the file, unless we've already got it
        if (!Files.exists(destPath)) {
            System.out.println("downloading to " + destPath);
            // fix a bug in Java, see http://stackoverflow.com/questions/7615645/ssl-handshake-alert-unrecognized-name-error-since-upgrade-to-java-1-7-0
            System.setProperty("jsse.enableSNIExtension", "false");
            final URL url = new URL(archiveUrl + sourceFile);

            Files.copy(url.openStream(), destPath);
        }
        return destPath;
    }

    /**
     * Open a .bz2 file for reading
     * <p/>
     * Contents of the bz2 file are decompressed, then converted from bytes to chars using this jvm's standard
     * charset.
     *
     * @param bz2File the file to open
     * @return a BufferedReader containing the decompressed contents of the bz2 file
     */
    private static BufferedReader bz2Reader(Path bz2File) throws CompressorException, IOException {
        final BufferedInputStream compressedBytes = new BufferedInputStream(Files.newInputStream(bz2File));
        final CompressorInputStream decompressedBytes = new CompressorStreamFactory().createCompressorInputStream(CompressorStreamFactory.BZIP2, compressedBytes);
        final BufferedReader br2 = new BufferedReader(new InputStreamReader(decompressedBytes));

        return br2;
    }
}
