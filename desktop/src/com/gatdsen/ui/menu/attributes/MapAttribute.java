package com.gatdsen.ui.menu.attributes;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.gatdsen.manager.run.RunConfig;
import com.gatdsen.manager.map.MapRetriever;

import java.util.Arrays;

public class MapAttribute extends Attribute {

    SelectBox<String> mapSelectBox;

    /**
     * Gibt den UI-Actor für das Attribut zurück.
     *
     * @param skin Das Skin-Objekt für die Benutzeroberfläche.
     * @return Ein Actor-Objekt, das das UI-Element für das Attribut repräsentiert.
     */
    @Override
    public Actor getContent(Skin skin) {
        Table mapTable = new Table();
        Label textLabelMap = new Label("Karte: ", skin);
        textLabelMap.setAlignment(Align.right);
        mapSelectBox = new SelectBox<>(skin);
        String[] sortedMapNames = MapRetriever.getInstance().getMapNames();
        Arrays.sort(sortedMapNames);
        mapSelectBox.setItems(sortedMapNames);
        mapTable.columnDefaults(0).width(200);
        mapTable.columnDefaults(1).width(200);
        mapTable.add(textLabelMap).colspan(4).pad(10).center();
        mapTable.add(mapSelectBox).colspan(4).pad(10).width(200).row();
        return mapTable;
    }

    /**
     * Konfiguriert die RunConfig unter Berücksichtigung dieses Attributs und gibt die aktualisierte Konfiguration zurück.
     *
     * @param runConfig Die aktuelle RunConfig.
     * @return Die aktualisierte RunConfig nach der Konfiguration des Attributs.
     */
    @Override
    public RunConfig getConfig(RunConfig runConfig) {
        runConfig.mapName = mapSelectBox.getSelected();
        return runConfig;
    }

    /**
     * Setzt die RunConfig basierend auf den Attributinformationen.
     *
     * @param runConfig Die RunConfig, die konfiguriert wird.
     */
    @Override
    public void setConfig(RunConfig runConfig) {
        mapSelectBox.setSelected(runConfig.mapName);
    }
}