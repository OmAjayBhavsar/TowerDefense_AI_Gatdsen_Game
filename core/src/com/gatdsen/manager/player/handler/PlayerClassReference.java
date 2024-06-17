package com.gatdsen.manager.player.handler;

import com.gatdsen.manager.player.*;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Diese Klasse repräsentiert eine Referenz auf eine Spielerklasse.
 * <p>
 * Sie wird verwendet, um Spielerklassen besser serialisierbar zu machen. Einfache {@link Class} Objekte implementieren
 * zwar das {@link Serializable} Interface, jedoch muss beim Deserialisieren der Klasse diese Klasse auch im anderen
 * Prozess geladen sein.
 * Dies ist bei {@link Bot} Klassen nur der Fall, wenn es unsere internen Klassen sind. Externe Klassen aus dem "bots"
 * Verzeichnis, müssen extra geladen werden und können daher nicht ohne weiteres zwischen Prozessen ausgetauscht werden.
 */
public final class PlayerClassReference implements Serializable {

    /** Die Referenz auf den {@link HumanPlayer} */
    public static final PlayerClassReference HUMAN_PLAYER = new PlayerClassReference("HumanPlayer", HumanPlayer.class);
    /** Die Referenz auf den {@link IdleBot} */
    public static final PlayerClassReference IDLE_BOT = new PlayerClassReference("IdleBot", IdleBot.class);

    public static final PlayerClassReference CAMPAIGN_22_BOT = new PlayerClassReference("Campaign_22_Bot", Campaign2_2Bot.class);
    /** Die Referenz auf den {@link IdleBot} */
    public static final PlayerClassReference EXAM_ADMISSION_BOT = new PlayerClassReference("ExamAdmissionBot", ExamAdmissionBot.class);
    /** Alle Referenzen auf die intern existierenden Spielerklassen */
    public static final Map<String, PlayerClassReference> INTERNAL_PLAYER_CLASS_REFERENCES = Map.of(HUMAN_PLAYER.fileName, HUMAN_PLAYER, IDLE_BOT.fileName, IDLE_BOT, CAMPAIGN_22_BOT.fileName, CAMPAIGN_22_BOT,EXAM_ADMISSION_BOT.fileName, EXAM_ADMISSION_BOT);

    /** Der Dateiname der Klasse - ohne Angabe des Dateipfades oder ".class" Endung */
    private final String fileName;
    /**
     * Die Spielerklasse, die repräsentiert wird
     * Die Variable ist transient, damit der Wert beim Serialisieren nicht mitgespeichert wird.
     */
    private transient Class<? extends Player> playerClass;

    /**
     * Erstellt eine neue Referenz auf eine Spielerklasse.
     * @param fileName Der Dateiname der Klasse - ohne Angabe des Dateipfades oder ".class" Endung
     * @param playerClass Die Spielerklasse, die repräsentiert wird von dieser Instanz
     */
    public PlayerClassReference(String fileName, Class<? extends Player> playerClass) {
        this.fileName = fileName;
        this.playerClass = playerClass;
    }

    /**
     * Gibt die Instanz der von dieser Klasse repräsentierten Spielerklasse zurück.
     * @return Die repräsentierte Spielerklasse
     */
    public Class<? extends Player> getPlayerClass() {
        // Nach dem Serialisieren und Deserialisieren wird die Variable playerClass nicht mehr gesetzt sein, sondern den
        // Standardwert null haben, vermutlich da wir uns nun in einem anderen Prozess befinden.
        // In diesem Fall muss die Klasse ggf. neu geladen werden.
        if (playerClass == null) {
            playerClass = getPlayerClassReference(fileName).getPlayerClass();
        }
        return playerClass;
    }

    /**
     * @return Gibt true zurück, falls die repräsentierte Klasse eine interne Spielerklasse ist, ansonsten false
     */
    public boolean referencesInternalPlayerClass() {
        return INTERNAL_PLAYER_CLASS_REFERENCES.containsKey(fileName);
    }

    /**
     * Gibt ein Array mit allen verfügbaren Spielerklassen zurück. Dies sind die internen Spielerklassen und alle
     * Klassen, die sich im "bots" Verzeichnis befinden, die ".class" Endung haben und eine Unterklasse von {@link Bot}
     * sind.
     * @return Ein Array mit allen verfügbaren Spielerklassen
     */
    public static PlayerClassReference[] getAvailablePlayerClassReferences() {
        List<PlayerClassReference> references = new ArrayList<>();
        references.add(HUMAN_PLAYER);
        references.add(IDLE_BOT);
        File botDir = new File("bots");
        if (botDir.exists()) {
            try (URLClassLoader loader = getURLClassLoader()) {
                // Dieser REGEX-Ausdruck matcht mit allen Dateien vom Format {ClassName}.class, solange {ClassName}
                // kein "$" enthält.
                // Damit filtern wir alle inneren Klassen heraus, da diese nicht direkt als Spielerklasse verwendet
                // werden können.
                File[] botFiles = botDir.listFiles(file -> file.getName().matches("^[^$]*\\.class$"));
                for (File botFile : Objects.requireNonNull(botFiles)) {
                    String classFileName = botFile.getName().replace(".class", "");
                    Class<? extends Player> playerClass = loadBotPlayerClass(loader, botDir, classFileName);
                    references.add(new PlayerClassReference(classFileName, playerClass));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return references.toArray(new PlayerClassReference[0]);
    }

    /**
     * Gibt die Referenz auf die Spielerklasse mit dem gegebenen Dateinamen zurück.
     * @param fileName Der Dateiname der Klasse - ohne Angabe des Dateipfades oder ".class" Endung
     * @return Die Referenz auf die Spielerklasse
     */
    public static PlayerClassReference getPlayerClassReference(String fileName) {
        PlayerClassReference internalPlayer = INTERNAL_PLAYER_CLASS_REFERENCES.get(fileName);
        if (internalPlayer != null) {
            return internalPlayer;
        }
        File botFile = new File("bots/" + fileName + ".class");
        if (!botFile.exists()) {
            throw new RuntimeException("Couldn't find bots." + fileName + ".class");
        }
        try (URLClassLoader loader = getURLClassLoader()) {
            return new PlayerClassReference(fileName, loadBotPlayerClass(loader, botFile.getParentFile(), fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Erstellt einen URLClassLoader, der auf das aktuelle Verzeichnis zeigt.
     * @return Der erstellte URLClassLoader
     */
    private static URLClassLoader getURLClassLoader() {
        try {
            return new URLClassLoader(new URL[]{new File(".").toURI().toURL()});
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Versucht die Bot-Klasse mit dem gegebenen Dateinamen mithilfe des gegebenen ClassLoaders zu laden.
     * Die Bot.class Datei muss sich dabei im "bots" Package befinden relativ zum Verzeichnis, auf das der ClassLoader
     * zeigt.
     * Das Verzeichnis wird dabei verwendet, um auch innere Klassen (die den Namen "fileName${innerClassName}.class"
     * haben) zu laden.
     * @param loader Der ClassLoader, der die Klasse laden soll
     * @param directory Das Verzeichnis, in dem die Bot.class Datei liegt
     * @param fileName Der Name der Bot.class Datei - ohne ".class" Endung!
     * @return Die geladene Klasse, die eine Unterklasse von {@link Bot} ist
     * @throws IllegalArgumentException Die Exception wird geworfen, falls die Klasse nicht gefunden werden konnte, die
     * Klasse von einer zu aktuellen Java-Version kompiliert wurde oder die Klasse keine Unterklasse von {@link Bot}
     * ist.
     */
    private static Class<? extends Bot> loadBotPlayerClass(ClassLoader loader, File directory, String fileName) throws IllegalArgumentException {
        Class<?> loadedClass = loadClass(loader, fileName);
        if (loadedClass == null || !Bot.class.isAssignableFrom(loadedClass)) {
            throw new IllegalArgumentException("Couldn't find bots." + fileName + ".class");
        }
        // Dieser REGEX-Ausdruck matcht mit allen Dateien, die mit fileName beginnen, dann ein "$" enthalten, gefolgt von
        // beliebigen Zeichen und enden mit ".class".
        File[] innerClassFiles = directory.listFiles(file -> file.getName().matches(fileName + "\\$.*\\.class"));
        for (File innerClassFile : Objects.requireNonNull(innerClassFiles)) {
            loadClass(loader, innerClassFile.getName().replace(".class", ""));
        }
        @SuppressWarnings("unchecked")
        Class<? extends Bot> botClass = (Class<? extends Bot>) loadedClass;
        return botClass;
    }

    /**
     * Hilfsmethode, um eine Klasse mit dem gegebenen Dateinamen mithilfe des gegebenen ClassLoaders zu laden und die
     * von {@link ClassLoader#loadClass(String)} geworfenen Exceptions zu kapseln.
     * Die Klasse muss sich dabei im "bots" Package befinden relativ zum Verzeichnis, auf das der ClassLoader zeigt.
     * @param loader Der ClassLoader, der die Klasse laden soll
     * @param fileName Der Name der Klasse - ohne ".class" Endung!
     * @return Die Referenz auf die geladene Klasse
     * @throws IllegalArgumentException Die Exception wird geworfen, falls die Klasse nicht gefunden werden konnte oder
     * die Klasse von einer zu aktuellen Java-Version kompiliert wurde.
     */
    private static Class<?> loadClass(ClassLoader loader, String fileName) throws IllegalArgumentException {
        try {
            return loader.loadClass("bots." + fileName);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Could not find class for " + fileName + ".class", e);
        } catch (UnsupportedClassVersionError e) {
            throw new IllegalArgumentException("Class " + fileName + ".class is compiled for a newer Java version than the current one.", e);
        }
    }
}
