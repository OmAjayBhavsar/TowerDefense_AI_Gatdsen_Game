package com.gatdsen.manager.map;

import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.gatdsen.simulation.GameMode;
import com.gatdsen.simulation.GameState;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

/**
 * Diese Klasse ist für das Laden von Karten zuständig.
 */
public final class MapRetriever {

    private static final String MAP_FILE_EXTENSION = ".json";
    private static final String INTERNAL_MAP_DIRECTORY = "/maps/";
    private static final String EXTERNAL_MAP_DIRECTORY = "maps/";

    static {
        File externalMapDirectory = new File(EXTERNAL_MAP_DIRECTORY);
        if (!externalMapDirectory.isDirectory()) {
            // TODO: Should this be logged?
            // System.err.println("Warning: No map directory found at " + externalMapDirectory.getAbsolutePath());
        }
    }

    private static final MapRetriever instance = new MapRetriever();

    public static MapRetriever getInstance() {
        return instance;
    }

    private Map[] maps;

    private MapRetriever() {
        Map[] internalMaps = getInternalMaps();
        Map[] externalMaps = getExternalMaps();

        maps = new Map[internalMaps.length + externalMaps.length];
        System.arraycopy(internalMaps, 0, maps, 0, internalMaps.length);
        System.arraycopy(externalMaps, 0, maps, internalMaps.length, externalMaps.length);

        if (maps.length == 0) {
            maps = new Map[]{new Map("No maps could be loaded", false, new GameState.MapTileType[0][0])};
        }
    }

    public Map getMapFromGamemode(GameMode gameMode) {
        String mapName = gameMode.getMap();
        for (Map map : maps) {
            if (map.getName().equals(mapName)) {
                if (!map.isHidden() || gameMode.isCampaignMode() || gameMode.isExamAdmissionMode()) {
                    return map;
                }
            }
        }
        throw new MapNotFoundException(mapName);
    }

    public String[] getDisplayableMapNames() {
        return Arrays.stream(maps).filter(map -> !map.isHidden()).map(Map::getName).toArray(String[]::new);
    }

    private Map[] getInternalMaps() {
        URI uri;
        try {
            uri = Objects.requireNonNull(getClass().getResource(INTERNAL_MAP_DIRECTORY)).toURI();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        Path mapDirectory;
        FileSystem fileSystem = null;
        try {
            mapDirectory = Paths.get(uri);
        } catch (FileSystemNotFoundException e) {
            try {
                fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            mapDirectory = fileSystem.getPath(INTERNAL_MAP_DIRECTORY);
        }
        Stream<Path> mapStream = null;
        try {
            mapStream = Files.list(mapDirectory);
            return mapStream
                    .filter(path -> path.getFileName().toString().endsWith(MAP_FILE_EXTENSION))
                    .map(path -> {
                        String mapName = path.getFileName().toString().replace(MAP_FILE_EXTENSION, "");
                        return createGameMap(
                                mapName,
                                isHiddenMap(mapName),
                                getClass().getResourceAsStream(INTERNAL_MAP_DIRECTORY + path.getFileName())
                        );
                    })
                    .toArray(Map[]::new);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (fileSystem != null) {
                try {
                    fileSystem.close();
                } catch (IOException ignored) {
                }
            }
            if (mapStream != null) {
                mapStream.close();
            }
        }
    }

    private static boolean isHiddenMap(String mapName) {
        switch (mapName) {
            case "Campaign1_1":
            case "Campaign1_2":
            case "Campaign2_1":
            case "Campaign2_2":
            case "Campaign3_1":
            case "ExamAdmission":
                return true;
        }
        return false;
    }

    private Map[] getExternalMaps() {
        File[] files = new File(EXTERNAL_MAP_DIRECTORY).listFiles();
        if (files == null) {
            return new Map[0];
        }
        return Arrays.stream(files)
                .filter(file -> file.getName().endsWith(MAP_FILE_EXTENSION))
                .map(file -> {
                    try {
                        return createGameMap(
                                file.getName().replace(MAP_FILE_EXTENSION, ""),
                                false,
                                new FileInputStream(file)
                        );
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toArray(Map[]::new);
    }

    private Map createGameMap(String mapName, boolean isHidden, InputStream fileStream) {
        JsonValue jsonData = readJsonValueFromInputStream(fileStream);

        int width = jsonData.get("width").asInt();
        int height = jsonData.get("height").asInt();

        GameState.MapTileType[][] tileTypes = new GameState.MapTileType[width][height];

        JsonValue tileData = jsonData.get("layers").get(0).get("data");

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int type = tileData.get(i + (height - j - 1) * width).asInt();
                tileTypes[i][j] = GameState.MapTileType.values()[type - 2];
            }
        }

        return new Map(mapName, isHidden, tileTypes);
    }

    private JsonValue readJsonValueFromInputStream(InputStream inputStream) {
        return new JsonReader().parse(inputStream);
    }
}
