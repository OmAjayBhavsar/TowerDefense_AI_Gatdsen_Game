package com.gatdsen.ui.menu.attributes;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.gatdsen.manager.player.handler.PlayerHandlerFactory;
import com.gatdsen.manager.run.RunConfig;

import java.util.ArrayList;

public class PlayerAttribute extends Attribute {

    int playerIndex;
    SelectBox<PlayerHandlerFactory> playerSelectBox;
    PlayerHandlerFactory[] availablePlayers;

    /**
     * Konstruktor für die PlayerAttribute-Klasse.
     *
     * @param playerIndex Der Index des Spielers.
     */
    public PlayerAttribute(int playerIndex) {
        this.playerIndex = playerIndex;
        availablePlayers = PlayerHandlerFactory.getAvailablePlayerFactories();
    }

    /**
     * Gibt den UI-Actor für das Attribut zurück.
     *
     * @param skin Das Skin-Objekt für die Benutzeroberfläche.
     * @return Ein Actor-Objekt, das das UI-Element für das Attribut repräsentiert.
     */
    @Override
    public Actor getContent(Skin skin) {
        Label textLabelPlayer = new Label("Spieler " + (playerIndex + 1) + ": ", skin);
        textLabelPlayer.setAlignment(Align.right);
        playerSelectBox = new SelectBox<>(skin);
        playerSelectBox.setItems(availablePlayers);
        Table playerChooseTable = new Table();

        playerChooseTable.columnDefaults(0).width(100);
        playerChooseTable.columnDefaults(1).width(100);
        playerChooseTable.add(textLabelPlayer).colspan(4).pad(10);
        playerChooseTable.add(playerSelectBox).colspan(4).pad(10).width(80).row();

        return playerChooseTable;
    }

    /**
     * Konfiguriert die RunConfig unter Berücksichtigung dieses Attributs und gibt die aktualisierte Konfiguration zurück.
     *
     * @param runConfig Die aktuelle RunConfig.
     * @return Die aktualisierte RunConfig nach der Konfiguration des Attributs.
     */
    @Override
    public RunConfig getConfig(RunConfig runConfig) {
        if (runConfig.playerFactories == null) {
            runConfig.playerFactories = new ArrayList<>();
        }
        while (runConfig.playerFactories.size() <= playerIndex) {
            runConfig.playerFactories.add(null);
        }
        runConfig.playerFactories.set(playerIndex, playerSelectBox.getSelected());
        return runConfig;
    }

    /**
     * Setzt die RunConfig basierend auf den Attributinformationen.
     *
     * @param runConfig Die RunConfig, die konfiguriert wird.
     */
    @Override
    public void setConfig(RunConfig runConfig) {
        if (runConfig.playerFactories == null || runConfig.playerFactories.size() <= playerIndex) {
            playerSelectBox.setSelected(null);
            return;
        }
        playerSelectBox.setSelected(runConfig.playerFactories.get(playerIndex));
    }
}