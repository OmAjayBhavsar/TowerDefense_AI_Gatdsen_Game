package com.gatdsen.manager.replay;

import com.gatdsen.manager.game.GameResults;

/**
 * Diese Klasse repräsentiert ein Replay eines Spiels.
 */
public final class Replay {

    /** Die Dateiendung für Replay-Dateien mit vorangestelltem Punkt */
    public static final String FILE_EXTENSION = ".replay";

    /** Der Name der Replay-Datei, ohne Angabe des Dateipfades oder Dateiendung */
    private final String fileName;
    /** Der Pfad zum Ordner, indem sich die Replay-Datei befindet */
    private final String path;
    /** Die Ergebnisse des Spiels, die im Replay festgehalten wurden */
    private final GameResults gameResults;

    /**
     * @param fileName Der Name der Replay-Datei, ohne Angabe des Dateipfades oder Dateiendung
     * @param path Der Pfad zum Ordner, indem sich die Replay-Datei befindet
     * @param gameResults Die Ergebnisse des Spiels, die im Replay festgehalten wurden
     */
    Replay(String fileName, String path, GameResults gameResults) {
        this.fileName = fileName;
        this.path = path;
        this.gameResults = gameResults;
    }

    /**
     * @return Der Name der Replay-Datei, ohne Angabe des Dateipfades oder Dateiendung
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @return Der Pfad zum Ordner, indem sich die Replay-Datei befindet
     */
    public String getPath() {
        return path;
    }

    /**
     * @return Der vollständige Dateipfad der Replay-Datei, also {@link #getPath()} + {@link #getFileName()} +
     *         {@link #FILE_EXTENSION}
     */
    public String getFilePath() {
        return getFilePath(path, fileName);
    }

    /**
     * Hilfsmethode, die den vollständigen Dateipfad einer Replay-Datei aus dem gegebenen Pfad und Dateinamen erstellt.
     * @param path Der Pfad zum Ordner, indem sich die Replay-Datei befindet
     * @param fileName Der Name der Replay-Datei, ohne Angabe des Dateipfades oder Dateiendung
     * @return Der vollständige Dateipfad der Replay-Datei, also {@link #getPath()} + {@link #getFileName()} +
     *         {@link #FILE_EXTENSION}
     */
    public static String getFilePath(String path, String fileName) {
        return path + fileName + FILE_EXTENSION;
    }

    /**
     * @return Die Ergebnisse des Spiels, die im Replay festgehalten wurden
     */
    public GameResults getGameResults() {
        return gameResults;
    }

    @Override
    public String toString() {
        return fileName;
    }
}
