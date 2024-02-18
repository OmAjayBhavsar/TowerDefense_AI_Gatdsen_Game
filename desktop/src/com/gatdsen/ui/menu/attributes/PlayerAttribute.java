package com.gatdsen.ui.menu.attributes;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.gatdsen.manager.player.handler.PlayerHandlerFactory;
import com.gatdsen.manager.run.config.RunConfiguration;

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
        Label textLabelPlayer = new Label("Spieler " + (playerIndex + 1) + ":", skin);
        textLabelPlayer.setAlignment(Align.center);
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
     * Konfiguriert die RunConfiguration unter Berücksichtigung dieses Attributs und gibt die aktualisierte Konfiguration zurück.
     *
     * @param runConfiguration Die aktuelle RunConfiguration.
     * @return Die aktualisierte RunConfiguration nach der Konfiguration des Attributs.
     */
    @Override
    public RunConfiguration getConfig(RunConfiguration runConfiguration) {
        if (runConfiguration.playerFactories == null) {
            runConfiguration.playerFactories = new ArrayList<>();
        }
        while (runConfiguration.playerFactories.size() <= playerIndex) {
            runConfiguration.playerFactories.add(null);
        }
        runConfiguration.playerFactories.set(playerIndex, playerSelectBox.getSelected());
        return runConfiguration;
    }

    /**
     * Setzt die RunConfiguration basierend auf den Attributinformationen.
     *
     * @param runConfiguration Die RunConfiguration, die konfiguriert wird.
     */
    @Override
    public void setConfig(RunConfiguration runConfiguration) {
        if (runConfiguration.playerFactories == null || runConfiguration.playerFactories.size() <= playerIndex) {
            playerSelectBox.setSelected(null);
            return;
        }
        playerSelectBox.setSelected(runConfiguration.playerFactories.get(playerIndex));
    }
}