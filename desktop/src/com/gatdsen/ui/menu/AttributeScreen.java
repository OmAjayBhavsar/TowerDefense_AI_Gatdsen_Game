package com.gatdsen.ui.menu;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.gatdsen.manager.run.RunConfig;
import com.gatdsen.ui.GADS;
import com.gatdsen.ui.menu.attributes.Attribute;

public abstract class AttributeScreen extends BaseMenuScreen {

    Attribute [] attributes;

    /**
     * Konstruktor für die Klasse AttributeScreen
     *
     * @param gameInstance Eine Instanz des GADS-Spiels
     */
    public AttributeScreen(GADS gameInstance) {
        super(gameInstance);

    }

    /**
     * Gibt den Inhalt der Benutzeroberfläche als Actor für den Attributsbildschirm zurück
     *
     * @param skin Das Skin-Objekt für die Benutzeroberfläche
     * @return Ein Actor-Objekt, das den Inhalt für den Attributsbildschirm repräsentiert
     */
    @Override
    Actor getContent(Skin skin) {
        Table attributeTable = new Table();
        attributes = (getAttributes());

        for (Attribute attribute : attributes) {
            attributeTable.add(attribute.getContent(skin)).center().row();
        }
        return attributeTable;
    }

    /**
     * Gibt die RunConfig unter Berücksichtigung der Attribute zurück
     *
     * @return Eine RunConfig mit den konfigurierten Attributen
     */
    @Override
    protected RunConfig getRunConfig() {
        RunConfig runConfig = super.getRunConfig();
        for (Attribute attribute : attributes) {
            attribute.getConfig(runConfig);
        }
        return runConfig;
    }

    /**
     * Setzt die RunConfig unter Berücksichtigung der Attribute
     *
     * @param runConfig Die RunConfig, die als Basis für die Kopie verwendet wird
     */
    @Override
    protected void setRunConfig(RunConfig runConfig) {
        super.setRunConfig(runConfig);
        for (Attribute attribute : attributes) {
            attribute.setConfig(runConfig);
        }
    }

    /**
     * Gibt ein Array von Attributen zurück, die für diesen Bildschirm konfiguriert werden sollen
     *
     * @return Ein Array von Attributen
     */
    protected abstract Attribute[] getAttributes();
}
