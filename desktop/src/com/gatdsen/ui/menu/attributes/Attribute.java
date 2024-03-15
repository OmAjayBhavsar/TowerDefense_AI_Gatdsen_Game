package com.gatdsen.ui.menu.attributes;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.gatdsen.manager.run.RunConfig;

public abstract class Attribute {
    /**
     * Gibt den UI-Actor für das Attribut zurück
     *
     * @param skin Das Skin-Objekt für die Benutzeroberfläche
     * @return Ein Actor-Objekt, das das UI-Element für das Attribut repräsentiert
     */
    public abstract Actor getContent(Skin skin);

    /**
     * Konfiguriert die RunConfig unter Berücksichtigung dieses Attributs und gibt die aktualisierte Konfiguration zurück
     *
     * @param runConfig Die aktuelle RunConfig
     * @return Die aktualisierte RunConfig nach der Konfiguration des Attributs
     */
    public abstract RunConfig getConfig(RunConfig runConfig);

    /**
     * Setzt die RunConfig basierend auf den Attributinformationen
     *
     * @param runConfig Die RunConfig, die konfiguriert wird
     */
    public abstract void setConfig(RunConfig runConfig);
}
