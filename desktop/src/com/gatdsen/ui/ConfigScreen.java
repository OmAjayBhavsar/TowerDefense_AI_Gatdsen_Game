package com.gatdsen.ui;

import com.badlogic.gdx.Screen;
import com.gatdsen.manager.run.RunConfig;

abstract public class ConfigScreen implements Screen {
   protected RunConfig runConfig;

    /**
     * Gibt eine Kopie der aktuellen RunConfig zurück.
     * @return Eine Kopie der aktuellen RunConfig.
     */
    protected RunConfig getRunConfig() {
        return runConfig.copy();
    }

    /**
     * Setzt die RunConfig auf eine Kopie des übergebenen RunConfig-Objekts.
     * @param runConfig Die RunConfig, die als Basis für die Kopie verwendet wird.
     */
    protected void setRunConfig(RunConfig runConfig) {
        this.runConfig = runConfig.copy();
    }
}