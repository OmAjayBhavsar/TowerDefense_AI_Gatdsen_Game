package com.gatdsen.manager.map;

public final class MapNotFoundException extends RuntimeException {

    public MapNotFoundException(String mapName) {
        super("The map \"" + mapName + "\" could not be found.");
    }
}
