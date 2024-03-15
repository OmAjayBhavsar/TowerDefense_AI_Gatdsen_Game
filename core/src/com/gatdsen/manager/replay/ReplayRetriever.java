package com.gatdsen.manager.replay;

import com.gatdsen.manager.game.GameResults;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.ClosedByInterruptException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

/**
 * Diese Klasse ist für das Laden der internen Replays, sowie das Speichern und Laden von externen Replays zuständig.
 */
public final class ReplayRetriever {

    private static final String INTERNAL_REPLAY_DIRECTORY = "/replays/";
    private static final String EXTERNAL_REPLAY_DIRECTORY = "replays/";

    private static final ReplayRetriever instance = new ReplayRetriever();

    private final File externalReplayDirectory = new File(EXTERNAL_REPLAY_DIRECTORY);
    private Replay[] internalReplays = null;
    private int writtenFiles = 0;

    public static ReplayRetriever getInstance() {
        return instance;
    }

    /**
     * Gibt alle verfügbaren Replays zurück. Dies sind sowohl die internen als auch die externen Replays.
     * @return Ein Array mit allen verfügbaren Replays
     */
    public Replay[] getAvailableReplays() {
        List<Replay> replays = new ArrayList<>(Arrays.asList(getInternalReplays()));
        replays.addAll(getExternalReplays());
        return replays.toArray(new Replay[0]);
    }

    /**
     * Hilfsmethode, um die internen Replays zu laden. Nachdem die Replays einmal geladen wurden, werden sie gecached.
     * @return Ein Array aller vorhandenen internen Replays
     * @throws RuntimeException Wenn ein Fehler beim Laden eines Replays aufgetreten ist
     */
    private Replay[] getInternalReplays() throws RuntimeException {
        if (internalReplays != null) {
            return internalReplays;
        }
        URI uri;
        try {
            uri = Objects.requireNonNull(getClass().getResource(INTERNAL_REPLAY_DIRECTORY)).toURI();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        Path directory;
        FileSystem fileSystem = null;
        try {
            // Versuch das Verzeichnis direkt über das normale Dateisystem des Betriebssystems zu laden. Dies
            // funktioniert nur, wenn wir das Spiel vom Sourcecode ausführen.
            directory = Paths.get(uri);
        } catch (FileSystemNotFoundException e) {
            // Sollten wir das Spiel als JAR-Datei ausführen, müssen wir das Verzeichnis aus der JAR-Datei laden. Das
            // ist in Java sehr umständlich... zumindest habe ich keinen schöneren Weg gefunden.
            // Wir erstellen hier ein neues Dateisystem, das die JAR-Datei als Quelle hat und lesen dann das Verzeichnis
            // aus diesem Dateisystem.
            try {
                fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            directory = fileSystem.getPath(INTERNAL_REPLAY_DIRECTORY);
        }
        try (Stream<Path> mapStream = Files.list(directory)) {
            internalReplays = mapStream
                    .filter(path -> path.getFileName().toString().endsWith(Replay.FILE_EXTENSION))
                    .map(path -> {
                                try {
                                    return loadReplay(
                                            path.getFileName().toString().replace(Replay.FILE_EXTENSION, ""),
                                            INTERNAL_REPLAY_DIRECTORY,
                                            getClass().getResourceAsStream(INTERNAL_REPLAY_DIRECTORY + path.getFileName()));
                                } catch (ReplayException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                    )
                    .toArray(Replay[]::new);
            return internalReplays;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (fileSystem != null) {
                try {
                    fileSystem.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    /**
     * Hilfsmethode, um die externen Replays zu laden.
     * Anders als {@link #getInternalReplays()} wirft diese Methode keine Exception, wenn es beim Laden eines Replays
     * zu einem Fehler kommt. Stattdessen wird das betroffene Replay einfach ignoriert.
     * @return Eine Liste aller fehlerfrei ladbaren, externen Replays
     */
    private List<Replay> getExternalReplays() {
        List<Replay> replays = new ArrayList<>();
        for (File file : Objects.requireNonNull(externalReplayDirectory.listFiles(path -> path.getName().endsWith(Replay.FILE_EXTENSION)))) {
            Replay replay = null;
            try (FileInputStream fs = new FileInputStream(file)) {
                replay = loadReplay(
                        file.getName().replace(Replay.FILE_EXTENSION, ""),
                        EXTERNAL_REPLAY_DIRECTORY,
                        fs
                );
            } catch (ReplayException | IOException ignored) {
            }
            if (replay != null) {
                replays.add(replay);
            }
        }
        return replays;
    }

    /**
     * Speichert die übergebenen GameResults als Replay ab.
     * @param results Die GameResults, die als Replay abgespeichert werden sollen
     * @throws ReplayException Wenn das Replay nicht gespeichert werden konnte
     */
    public void saveAsReplay(GameResults results) throws ReplayException {
        String replayFileName = String.format(
                "%s_%d_(%d)",
                results.getConfig().gameMode.toString(),
                System.currentTimeMillis(),
                writtenFiles++
        );
        saveReplay(new Replay(replayFileName, EXTERNAL_REPLAY_DIRECTORY, results));
    }

    /**
     * Speichert das übergebene Replay ab.
     * @param replay Das Replay, das abgespeichert werden soll
     * @throws ReplayException Wenn das Replay nicht gespeichert werden konnte
     */
    public void saveReplay(Replay replay) throws ReplayException {
        if (externalReplayDirectory.exists() || externalReplayDirectory.mkdirs()) {
            String replayFilePath = replay.getFilePath();
            try (FileOutputStream fs = new FileOutputStream(replayFilePath)) {
                new ObjectOutputStream(fs).writeObject(replay.getGameResults());
            } catch (IOException e) {
                throw new ReplaySaveException(replay, "Unable to save replay at " + replayFilePath, e);
            }
        } else {
            throw new ReplaySaveException(replay, "Unable to create external replay directory at " + externalReplayDirectory);
        }
    }

    /**
     * Lädt das Replay auf Basis des übergebenen Parameters. Zunächst wird überprüft, ob der übergebene Parameter dem
     * Dateinamen eines internen Replays entspricht und falls ja, dieses zurückgegeben. Andernfalls wird versucht, das
     * Replay anhand des übergebenen Parameters zu laden. <br>
     * Der Parameter kann entweder der Dateiname (ohne Pfad und ohne Dateiendung) eines Replays im
     * /replays-Verzeichnisses des Spiels sein oder der direkte Dateipfad zu einem Replay.
     * @param fileNameOrFilePath Der Dateiname oder der direkte Dateipfad des Replays
     * @return Das geladene Replay
     * @throws ReplayException Wenn das Replay nicht geladen werden konnte
     */
    public Replay loadReplay(String fileNameOrFilePath) throws ReplayException {
        for (Replay internalReplay : getInternalReplays()) {
            if (internalReplay.getFileName().equals(fileNameOrFilePath)) {
                return internalReplay;
            }
        }
        String path = EXTERNAL_REPLAY_DIRECTORY;
        String fileName = fileNameOrFilePath;
        if (fileNameOrFilePath.endsWith(Replay.FILE_EXTENSION)) {
            File file = new File(fileNameOrFilePath);
            String parent = file.getParent();
            if (parent != null) {
                path = parent + "/";
            } else {
                path = "";
            }
            fileName = file.getName().replace(Replay.FILE_EXTENSION, "");
        }
        String filePath = Replay.getFilePath(path, fileName);
        Replay replay;
        try (FileInputStream fs = new FileInputStream(filePath)) {
            replay = loadReplay(fileName, path, fs);
        } catch (FileNotFoundException e) {
            throw new ReplayLoadException("There is no replay at " + filePath, e);
        } catch (IOException e) {
            throw new ReplayLoadException("Unable to read replay at " + filePath, e);
        }
        return replay;
    }

    /**
     * Hilfsmethode, die ein Replay mit dem übergebenen Dateinamen und Pfad, sowie einem InputStream, der das
     * {@link GameResults} Objekt enthält, lädt.
     * @param fileName Der Dateiname des Replays
     * @param path Der Pfad des Replays
     * @param inputStream Der InputStream, der das {@link GameResults} Objekt enthält
     * @return Das geladene Replay
     * @throws ReplayException Wenn das Replay nicht geladen werden konnte
     */
    private Replay loadReplay(String fileName, String path, InputStream inputStream) throws ReplayException {
        GameResults results;
        try (ObjectInputStream ois = new ObjectInputStream(inputStream)) {
            results = (GameResults) ois.readObject();
        } catch (IOException e) {
            throw new ReplayLoadException("Unable to read replay at " + path, e);
        } catch (ClassNotFoundException e) {
            throw new ReplayLoadException("Could not find the GameResults class that was serialized into the replay file at " + path, e);
        }
        return new Replay(fileName, path, results);
    }
}
