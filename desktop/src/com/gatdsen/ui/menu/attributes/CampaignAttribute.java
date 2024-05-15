package com.gatdsen.ui.menu.attributes;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.gatdsen.manager.player.handler.PlayerHandlerFactory;
import com.gatdsen.manager.run.RunConfig;
import com.gatdsen.simulation.GameMode;
import com.gatdsen.simulation.gamemode.GameModeFactory;
import com.gatdsen.simulation.gamemode.PlayableGameMode;

public final class CampaignAttribute extends Attribute {

    private SelectBox<GameMode> selectBox;

    @Override
    public Actor getContent(Skin skin) {
        Label textLabel = new Label("Kampagne: ", skin);
        textLabel.setAlignment(Align.right);

        selectBox = new SelectBox<>(skin);
        selectBox.setItems(GameModeFactory.getInstance().getAvailableCampaigns());

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
        GameMode campaign = null;
        GameMode[] campaigns = GameModeFactory.getInstance().getAvailableCampaigns();
        if (campaigns.length > 0) {
            campaign = campaigns[0];
        }
        selectBox.setSelected(campaign);
    }
}
