package com.gatdsen.ui.menu.attributes;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.gatdsen.manager.Manager;
import com.gatdsen.manager.run.config.RunConfiguration;
import com.gatdsen.manager.player.Player;

import java.util.ArrayList;

public class PlayerAttribute extends Attribute {

    int playerIndex;
    SelectBox<Manager.NamedPlayerClass> playerSelectBox;
    Manager.NamedPlayerClass[] availablePlayers;

    /**
     * Konstruktor für die PlayerAttribute-Klasse.
     *
     * @param playerIndex Der Index des Spielers.
     */
    public PlayerAttribute(int playerIndex) {
        this.playerIndex = playerIndex;
        availablePlayers = Manager.getPossiblePlayers();
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
        if (runConfiguration.players == null) {
            runConfiguration.players = new ArrayList<>();
        }
        while (runConfiguration.players.size() <= playerIndex) {
            runConfiguration.players.add(null);
        }
        runConfiguration.players.set(playerIndex, playerSelectBox.getSelected().getClassRef());
        return runConfiguration;
    }

    /**
     * Setzt die RunConfiguration basierend auf den Attributinformationen.
     *
     * @param runConfiguration Die RunConfiguration, die konfiguriert wird.
     */
    @Override
    public void setConfig(RunConfiguration runConfiguration) {
        if (runConfiguration.players == null || runConfiguration.players.size() <= playerIndex) {
            playerSelectBox.setSelected(null);
            return;
        }
        Class<? extends Player> targetClass = runConfiguration.players.get(playerIndex);
        Manager.NamedPlayerClass result = null;
        for (Manager.NamedPlayerClass availablePlayer : availablePlayers) {
            if (availablePlayer.getClassRef() == targetClass) {
                result = availablePlayer;
                break;
            }
        }
        playerSelectBox.setSelected(result);
    }
}