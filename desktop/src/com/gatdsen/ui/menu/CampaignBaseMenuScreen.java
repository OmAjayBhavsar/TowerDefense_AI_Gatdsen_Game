package com.gatdsen.ui.menu;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.gatdsen.manager.map.MapRetriever;
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
        SelectBox<String> campaignSelectBox;
        CampaignMenuTable.setFillParent(false);
        CampaignMenuTable.center();

        Label textLabelCampaign = new Label("Kampagne: ", skin);
        textLabelCampaign.setAlignment(Align.right);
        campaignSelectBox = new SelectBox<>(skin);
        String[] sortedCampaignNames = {"CampaignMode1_1", "CampaignMode1_2", "CampaignMode2_1", "CampaignMode2_2", "CampaignMode3_1", "CampaignMode3_2", "CampaignMode4_1", "CampaignMode4_2", "CampaignMode5_1", "CampaignMode5_2", "CampaignMode6_2", "CampaignMode6_2"};
        Arrays.sort(sortedCampaignNames);
        campaignSelectBox.setItems(sortedCampaignNames);
        CampaignMenuTable.columnDefaults(0).width(200);
        CampaignMenuTable.columnDefaults(1).width(200);
        CampaignMenuTable.add(textLabelCampaign).colspan(4).pad(10).center();
        CampaignMenuTable.add(campaignSelectBox).colspan(4).pad(10).width(200).row();

        campaignSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

                switch (campaignSelectBox.getSelected()) {
                    case "CampaignMode1_1":
                        runConfig.gameMode = new CampaignMode1_1();
                        break;
                    case "CampaignMode1_2":
                        runConfig.gameMode = new CampaignMode1_2();
                        break;
                    case "CampaignMode2_1":
                        runConfig.gameMode = new CampaignMode2_1();
                        break;
                    case "CampaignMode2_2":
                        runConfig.gameMode = new CampaignMode2_2();
                        break;
                    case "CampaignMode3_1":
                        runConfig.gameMode = new CampaignMode3_1();
                        break;
                    case "CampaignMode3_2":
                        runConfig.gameMode = new CampaignMode3_2();
                        break;
                    case "CampaignMode4_1":
                        runConfig.gameMode = new CampaignMode4_1();
                        break;
                    case "CampaignMode4_2":
                        runConfig.gameMode = new CampaignMode4_2();
                        break;
                    case "CampaignMode5_1":
                        runConfig.gameMode = new CampaignMode5_1();
                        break;
                    case "CampaignMode5_2":
                        runConfig.gameMode = new CampaignMode5_2();
                        break;
                    case "CampaignMode6_1":
                        runConfig.gameMode = new CampaignMode6_1();
                        break;
                    case "CampaignMode6_2":
                        runConfig.gameMode = new CampaignMode6_2();
                        break;
                    default:
                        break;
                }
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
