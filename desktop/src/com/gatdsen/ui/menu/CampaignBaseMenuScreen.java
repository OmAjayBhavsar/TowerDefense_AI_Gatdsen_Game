package com.gatdsen.ui.menu;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.gatdsen.manager.map.MapRetriever;
import com.gatdsen.simulation.GameMode;
import com.gatdsen.simulation.gamemode.GameModeFactory;
import com.gatdsen.simulation.gamemode.campaign.*;
import com.gatdsen.ui.GADS;
import com.gatdsen.ui.menu.attributes.Attribute;
import com.gatdsen.ui.menu.attributes.MapAttribute;
import com.gatdsen.ui.menu.attributes.PlayerAttribute;

import java.util.Arrays;

public class CampaignBaseMenuScreen extends BaseMenuScreen {
    /**
     * Konstruktor für die Klasse AttributeScreen
     *
     * @param gameInstance Eine Instanz des GADS-Spiels
     */
    public CampaignBaseMenuScreen(GADS gameInstance) {
        super(gameInstance);
    }

    @Override
    String getTitelString() {
        return "Kampagne";
    }

    @Override
    Actor getContent(Skin skin) {
        Table CampaignMenuTable = new Table(skin);
        SelectBox<GameMode> campaignSelectBox;
        CampaignMenuTable.setFillParent(false);
        CampaignMenuTable.center();

        Label textLabelCampaign = new Label("Kampagne: ", skin);
        textLabelCampaign.setAlignment(Align.right);
        campaignSelectBox = new SelectBox<>(skin);
        campaignSelectBox.setItems(GameModeFactory.getInstance().getAvailableCampaigns());
        CampaignMenuTable.columnDefaults(0).width(200);
        CampaignMenuTable.columnDefaults(1).width(200);
        CampaignMenuTable.add(textLabelCampaign).colspan(4).pad(10).center();
        CampaignMenuTable.add(campaignSelectBox).colspan(4).pad(10).width(200).row();

        campaignSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                runConfig.gameMode = campaignSelectBox.getSelected();
            }
        });
        return CampaignMenuTable;
    }

    @Override
    GADS.ScreenState getNext() {
        return GADS.ScreenState.INGAMESCREEN;
    }

    @Override
    GADS.ScreenState getPrev() {
        return GADS.ScreenState.MAINSCREEN;
    }
}
