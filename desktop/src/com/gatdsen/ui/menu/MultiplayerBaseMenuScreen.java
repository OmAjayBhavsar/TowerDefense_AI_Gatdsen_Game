package com.gatdsen.ui.menu;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.gatdsen.ui.GADS;

public class MultiplayerBaseMenuScreen extends BaseMenuScreen{

    /**
     * Konstruktor für die Klasse BaseMenuScreen
     *
     * @param gameInstance Eine Instanz des GADS-Spiels
     */
    public MultiplayerBaseMenuScreen(GADS gameInstance) {
        super(gameInstance);
    }

    @Override
    String getTitelString() {
        return "Mehrspieler Modus wählen";
    }

    @Override
    Actor getContent(Skin skin) {
        Table mainMenuTable = new Table(skin);
        mainMenuTable.setFillParent(false); //sorgt dafür das die Tabelle nicht auf dem gesamten Screen angezeigt wird
        mainMenuTable.center();
        TextButton hostMultiplayerGameButton = new TextButton("Spiel hosten", skin);
        hostMultiplayerGameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameInstance.setScreen(GADS.ScreenState.MULTIPLAYERHOSTSCREEN, runConfig);
            }
        });
        mainMenuTable.add(hostMultiplayerGameButton).colspan(4).pad(10).width(200).row();
        TextButton joinMultiplayerGameButton = new TextButton("Spiel beitreten", skin);
        joinMultiplayerGameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameInstance.setScreen(GADS.ScreenState.MULTIPLAYERJOINSCREEN, runConfig);
            }
        });
        mainMenuTable.add(joinMultiplayerGameButton).colspan(4).pad(10).width(200).row();
        return mainMenuTable;
    }

    @Override
    GADS.ScreenState getNext() {
        return null;
    }

    @Override
    GADS.ScreenState getPrev() {
        return GADS.ScreenState.MAINSCREEN;
    }
}
