package com.gatdsen.ui.menu;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.gatdsen.simulation.GameState;
import com.gatdsen.simulation.gamemode.ChristmasMode;
import com.gatdsen.simulation.gamemode.ExamAdmissionMode;
import com.gatdsen.simulation.gamemode.NormalMode;
import com.gatdsen.simulation.gamemode.campaign.CampaignMode1_1;
import com.gatdsen.ui.GADS;

public class MainScreen extends BaseMenuScreen {

    /**
     * Konstruktor für die Klasse MainScreen
     *
     * @param gameInstance Die Instanz des Hauptspiels (GADS)
     */
    public MainScreen(GADS gameInstance) {
        super(gameInstance);
    }

    /**
     * Gibt den Inhalt der Benutzeroberfläche als Tabelle für den Hauptbildschirm zurück
     *
     * @param skin Das Skin-Objekt für die Benutzeroberfläche
     * @return Die Tabelle, die den Inhalt für den Hauptbildschirm repräsentiert
     */
    @Override
    Table getContent(Skin skin) {
        Table mainMenuTable = new Table(skin);
        mainMenuTable.setFillParent(false); //sorgt dafür das die Tabelle nicht auf dem gesamten Screen angezeigt wird
        mainMenuTable.center();
        TextButton normalGameModeButton = new TextButton("Spielmodus Normal", skin);
        normalGameModeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                runConfig.gameMode = new NormalMode();
                gameInstance.setScreen(GADS.ScreenState.NORMALMODESCREEN, runConfig);
            }
        });
        mainMenuTable.add(normalGameModeButton).colspan(4).pad(10).width(200).row();
        TextButton christmasTaskButton = new TextButton("Weihnachtsaufgabe", skin);
        christmasTaskButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                //setRunConfig(RunConfig.fromGameMode(GameState.GameMode.Christmas_Task), runConfig));
                runConfig.gameMode = new ChristmasMode();
                gameInstance.setScreen(GADS.ScreenState.CHRISTMASTASKSCREEN, runConfig);
            }
        });
        mainMenuTable.add(christmasTaskButton).colspan(4).pad(10).width(200).row();
        TextButton examButton = new TextButton("Klausurzulassung", skin);
        examButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                runConfig.gameMode = new ExamAdmissionMode();
                gameInstance.setScreen(GADS.ScreenState.EXAMMENUSCREEN, runConfig);
            }
        });
        mainMenuTable.add(examButton).colspan(4).pad(10).width(200).row();
        TextButton campaignButton = new TextButton("Kampagne", skin);
        campaignButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                runConfig.gameMode = new CampaignMode1_1(); // TODO: richtige Kampagne auswählen gez. Dani
                gameInstance.setScreen(GADS.ScreenState.CAMPAIGNSCREEN, runConfig);
            }
        });
        mainMenuTable.add(campaignButton).colspan(4).pad(10).width(200).row();
        TextButton multiplayerButton = new TextButton("Mehrspieler", skin);
        multiplayerButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameInstance.setScreen(GADS.ScreenState.MULTIPLAYERBASESCREEN, runConfig);
            }
        });
        mainMenuTable.add(multiplayerButton).colspan(4).pad(10).width(200).row();
        return mainMenuTable;
    }

    /**
     * Gibt den nächsten Bildschirmzustand für den Hauptbildschirm zurück
     *
     * @return Der nächste Bildschirmzustand (ScreenState)
     */
    @Override
    GADS.ScreenState getNext() {
        return null;
    }

    /**
     * Gibt den vorherigen Bildschirmzustand für den Hauptbildschirm zurück
     *
     * @return Der vorherige Bildschirmzustand (ScreenState)
     */
    @Override
    GADS.ScreenState getPrev() {
        return null;
    }

    /**
     * Gibt den Titelstring für die Überschrift zurück
     *
     * @return Der Titel
     */
    @Override
    String getTitelString() {
        return "";
    }
}
