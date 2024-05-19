package com.gatdsen.ui.menu.attributes;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.gatdsen.manager.run.RunConfig;
import com.gatdsen.simulation.GameMode;
import com.gatdsen.simulation.gamemode.GameModeFactory;

import java.util.Arrays;

public final class CampaignAttribute extends Attribute {

    private final GameMode[] campaigns;
    private SelectBox<GameMode> selectBox;

    public CampaignAttribute() {
        campaigns = GameModeFactory.getInstance().getAvailableCampaigns();
        Arrays.sort(
                campaigns,
                // Sortierung der Kampagnen in umgekehrter lexikografischer Reihenfolge, damit die neueren Kampagnen
                // immer zuerst kommen
                (GameMode campaign1, GameMode campaign2) ->
                        campaign2.getDisplayName().compareTo(campaign1.getDisplayName())
        );
    }

    @Override
    public Actor getContent(Skin skin) {
        Label textLabel = new Label("Kampagne: ", skin);
        textLabel.setAlignment(Align.right);

        selectBox = new SelectBox<>(skin);
        selectBox.setItems(campaigns);

        Table table = new Table();
        table.columnDefaults(0).width(200);
        table.columnDefaults(1).width(200);
        table.add(textLabel).colspan(4).pad(10);
        table.add(selectBox).colspan(4).pad(10).width(200).row();
        return table;
    }

    /**
     * Konfiguriert die RunConfig unter Berücksichtigung dieses Attributs und gibt die aktualisierte Konfiguration zurück.
     *
     * @param runConfig Die aktuelle RunConfig.
     * @return Die aktualisierte RunConfig nach der Konfiguration des Attributs.
     */
    @Override
    public RunConfig getConfig(RunConfig runConfig) {
        runConfig.gameMode = selectBox.getSelected();
        return runConfig;
    }

    /**
     * Setzt die RunConfig basierend auf den Attributinformationen.
     *
     * @param runConfig Die RunConfig, die konfiguriert wird.
     */
    @Override
    public void setConfig(RunConfig runConfig) {
        GameMode selectedCampaign = null;
        if (campaigns.length > 0) {
            // Dieser Code funktioniert nur gut, wenn die Liste in umgekehrter lexikografischer Reihenfolge sortiert
            // wurde und es pro Woche nur zwei neue Kampagnenlevel gibt
            // Hier wird nämlich das zweite Element im Array ausgewählt, bei einem Array der Struktur
            // ["Campaign 2.2", "Campaign 2.1", "Campaign 1.2", "Campaign 1.1"] also "Campaign 2.1", da dies das erste
            // Kampagnenlevel der aktuellen Woche ist
            selectedCampaign = campaigns[Math.min(1, campaigns.length - 1)];
        }
        selectBox.setSelected(selectedCampaign);
    }
}
