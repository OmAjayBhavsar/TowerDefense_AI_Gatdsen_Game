package com.gatdsen.ui.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.gatdsen.animation.entity.TileMap;
import com.gatdsen.manager.run.RunConfig;
import com.gatdsen.manager.run.RunConfiguration;
import com.gatdsen.simulation.Enemy;
import com.gatdsen.simulation.GameState;
import com.gatdsen.simulation.PlayerState;
import com.gatdsen.simulation.Tower;
import com.gatdsen.ui.GADS;
import com.gatdsen.ui.assets.AssetContainer;
import com.gatdsen.ui.hud.*;

import java.util.ArrayList;

/**
 * Class for taking care of the User Interface.
 * Input Handling during the game.
 * Displaying health, inventory
 */
public class Hud implements Disposable {

    private Stage stage;
    private final InputHandler inputHandler;
    private final InputMultiplexer inputMultiplexer;
    private final TurnTimer turnTimer;
    public final Table layoutTable;
    private final Container<ImagePopup> turnPopupContainer;
    private final InGameScreen inGameScreen;
    private final TextureRegion turnChangeSprite;
    private final float turnChangeDuration;
    private final UiMessenger uiMessenger;
    private float renderingSpeed;
    private boolean debugVisible;
    private float[] scores;
    private String[] names;
    private final ScoreView scoreView;
    private final Skin skin = AssetContainer.MainMenuAssets.skin;
    Viewport hudViewport;
    private int player0Balance;
    private int player1Balance;
    protected GADS gameInstance;
    private GameState gameState;
    private int health;
    private int buttonWidth;
    private ProgressBar healthBarPlayer0 = new ProgressBar(0, health, 1, false, skin);
    private ProgressBar healthBarPlayer1 = new ProgressBar(0, health, 1, false, skin);
    private int roundCounter;
    private int healthPlayer0 = health;
    private int healthPlayer1 = health;
    public Group hudGroup = new Group();
    public TileMap tileMap;
    private SelectBox<Tower.TowerType> towerSelectBox;
    private SelectBox<String> towerSellUpgrade;
    private SelectBox fireModeSelectBox;
    private ArrayList<int[][]> towerMaps = new ArrayList<>();
    private Label player0BalanceLabel;
    private Label player1BalanceLabel;
    private Label currentRoundLabel;
    private Label healthPlayer0Label;
    private Label healthPlayer1Label;
    private VerticalGroup mainVerticalGroup;

    /**
     * Initialisiert das HUD-Objekt
     *
     * @param ingameScreen Die Instanz der InGameScreen-Klasse
     * @param gameInstance Die gameInstance für das Spiel
     */
    public Hud(InGameScreen ingameScreen, GADS gameInstance) {

        this.gameInstance = gameInstance;
        this.inGameScreen = ingameScreen;
        this.uiMessenger = new UiMessenger(this);
        turnChangeDuration = 2;
        turnChangeSprite = AssetContainer.IngameAssets.turnChange;
        inputHandler = setupInputHandler(ingameScreen, this);
        inputHandler.setUiMessenger(uiMessenger);
        turnTimer = new TurnTimer();
        turnPopupContainer = new Container<ImagePopup>();
        hudViewport = new FitViewport(200, 200);
        stage = new Stage(hudViewport);
        // Kombination von Eingaben von beiden Prozessoren (Spiel und UI)
        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(inputHandler);
        inputHandler.setUiMessenger(uiMessenger);// für die Simulation benötigt
        inputMultiplexer.addProcessor(stage);
        scoreView = new ScoreView(null);
        mainVerticalGroup = new VerticalGroup();
        mainVerticalGroup.setFillParent(true);
        stage.addActor(mainVerticalGroup);

    }

    public void setHudViewport(int worldWidth, int worldHeight) {
        stage.getViewport().setWorldSize((float) worldWidth / 10, (float) worldHeight / 10);
    }

    /**
     * Startet ein neues Spiel mit den gegebenen Parametern
     *
     * @param gameState             Der Zustand des neuen Spiels
     * @param arrayPositionTileMaps Die Positionen der TileMaps im Array
     * @param tileSize              Die Größe der Tiles
     * @param tileMap               Die TileMap des Spiels
     */

    public void init(GameState gameState, Vector2[] arrayPositionTileMaps, int tileSize, TileMap tileMap) {
        int playerTableWidth = 80;
        renderingSpeed = 1;
        player0Balance = 100;
        player1Balance = 100;
        health = 300;
        buttonWidth = 150;
        roundCounter = 0;
        TextButton restartGameButton;
        TextButton nextRoundButton;
        TextButton backToMainMenuButton;

        this.gameState = gameState;

        healthBarPlayer0.setValue(health);
        healthBarPlayer0.setAnimateDuration(0.25f);
        healthBarPlayer1.setValue(health);
        healthBarPlayer1.setAnimateDuration(0.25f);
        if (gameState != null) {
            roundCounter = gameState.getTurn();
        } else roundCounter = 0;

// Erstellen der Elemente
        player0BalanceLabel = new Label("$" + player0Balance, skin);
        player1BalanceLabel = new Label("$" + player1Balance, skin);
        currentRoundLabel = new Label("Runde: " + roundCounter, skin);
        healthPlayer0Label = new Label("" + healthPlayer0, skin);
        healthPlayer1Label = new Label("" + healthPlayer1, skin);
        nextRoundButton = new TextButton("Zug beenden", skin);
        backToMainMenuButton = new TextButton("Hauptmenü", skin);
        restartGameButton = new TextButton("Neustart", skin);
        SelectBox<String> playerSelectBox = new SelectBox<>(skin);
        playerSelectBox.setItems("Spieler 1", "Spieler 2");
        playerSelectBox.setSize(140, 20);

        SelectBox<String> enemySelectBox = new SelectBox<>(skin);
        enemySelectBox.setItems("Schild-Maus", "EMP-Maus", "Rüstungs-Maus"); // Beispielwerte, bitte anpassen
        enemySelectBox.setSize(140, 20);

        TextButton buyButton = new TextButton("Kaufen", skin);
        nextRoundButton.addListener(new ChangeListener() {
            /**
             * Wird aufgerufen, wenn der Button geklickt wird
             *
             * @param event Das ChangeEvent
             * @param actor Das Actor-Objekt, das das Änderungsereignis ausgelöst hat
             */
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                inputHandler.endTurn();
                updateUIElements();
            }
        });
        backToMainMenuButton.addListener(new ChangeListener() {
            /**
             * Wird aufgerufen, wenn der Button geklickt wird
             *
             * @param event Das ChangeEvent
             * @param actor Das Actor-Objekt, das das Änderungsereignis ausgelöst hat
             */
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                inGameScreen.dispose();
            }
        });
        buyButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String selectedPlayer = playerSelectBox.getSelected();
                String selectedEnemy = enemySelectBox.getSelected();
                int selectedPlayerInt;
                Enemy.Type enemyType;

                selectedPlayerInt = switch (selectedPlayer) {
                    case "Spieler 1" -> 0;
                    case "Spieler 2" -> 1;
                    default -> 0;
                };

                enemyType = switch (selectedEnemy) {
                    case "Schild-Maus" -> Enemy.Type.SHIELD_ENEMY;
                    case "EMP-Maus" -> Enemy.Type.EMP_ENEMY;
                    case "Rüstungs-Maus" -> Enemy.Type.ARMOR_ENEMY;
                    default -> Enemy.Type.SHIELD_ENEMY;
                };
                inputHandler.playerBuyedEnemy(selectedPlayerInt, enemyType);
            }
        });

        Table playerTable = new Table();
        playerTable.defaults().pad(10);
        playerTable.add(new Label("Spieler 1", skin)).width(playerTableWidth);
        playerTable.add(player0BalanceLabel).width(playerTableWidth);
        playerTable.add(healthBarPlayer0).width(playerTableWidth);
        playerTable.add(currentRoundLabel).width(playerTableWidth);
        playerTable.add(healthBarPlayer1).width(playerTableWidth);
        playerTable.add(player1BalanceLabel).width(playerTableWidth);
        playerTable.add(new Label("Spieler 2", skin)).width(playerTableWidth);

        Table mainTable = new Table();
        mainTable.defaults().pad(10);
        mainTable.add(new Label("", skin)).width(buttonWidth).row(); // Invisible label for spacing
        mainTable.add(turnTimer).width(buttonWidth).row();
        mainTable.add(new Label("", skin)).width(buttonWidth).row(); // Invisible label for spacing
        mainTable.add(nextRoundButton).colspan(3).width(buttonWidth).row();
        mainTable.add(new Label("Shop zum Spawnen", skin)).colspan(3).row();
        mainTable.add(new Label("von Gegnern", skin)).colspan(3).row();
        mainTable.add(playerSelectBox).colspan(3).width(buttonWidth).row();
        mainTable.add(enemySelectBox).colspan(3).width(buttonWidth).row();
        mainTable.add(buyButton).colspan(3).width(buttonWidth).row();
        mainTable.add(new Label("", skin)).colspan(3).width(buttonWidth).row(); // Invisible label for spacing
        mainTable.add(backToMainMenuButton).colspan(3).width(buttonWidth).row();

        // Hinzufügen der Tabellen zur VerticalGroup
        mainVerticalGroup.addActor(playerTable);
        mainVerticalGroup.addActor(mainTable);

        stage.addActor(mainVerticalGroup);
        stage.addActor(hudGroup);

        int numberOfTeams = gameState.getPlayerCount();
        TextButton[] teamButtons;
        teamButtons = new TextButton[numberOfTeams];

        for (int i = 0; i < numberOfTeams; i++) {
            int[][] towerMap = new int[gameState.getBoardSizeX()][gameState.getBoardSizeY()];
            towerMaps.add(towerMap);
            teamButtons[i] = tileMapButton(i, tileMap);
            teamButtons[i].setSize((gameState.getBoardSizeX() * tileSize) / 10.0f, (gameState.getBoardSizeY() * tileSize) / 10.0f);
            hudGroup.addActor(teamButtons[i]);
            teamButtons[i].setPosition((arrayPositionTileMaps[i].x) / 10.0f, (arrayPositionTileMaps[i].y) / 10.0f);
            teamButtons[i].setColor(Color.CLEAR);
            initPlayerHealth(i);
            initBankBalance(i);
        }
        if (turnPopupContainer.hasChildren()) {
            turnPopupContainer.removeActorAt(0, false);
        }
        setupScoreboard(gameState);

        // float mainGroupX = (hudViewport.getWorldWidth() - mainVerticalGroup.getWidth()) / 2;
        //  float mainGroupY = hudViewport.getWorldHeight() - mainVerticalGroup.getHeight();

        //    mainVerticalGroup.setPosition(mainGroupX, mainGroupY);
        stage.addActor(mainVerticalGroup);
    }

    /**
     * Erstellt einen InputHandler und gibt ihn zurück
     *
     * @param ingameScreen Die Instanz der InGameScreen-Klasse
     * @param h            Das Hud-Objekt
     * @return Ein neues InputHandler-Objekt
     */
    private InputHandler setupInputHandler(InGameScreen ingameScreen, Hud h) {
        return new InputHandler(ingameScreen, h);
    }

    /**
     * Setzt das Scoreboard für das Spiel auf
     *
     * @param game Die GameState-Instanz für das Spiel
     */
    public void setupScoreboard(GameState game) {

        //ToDo read player count and assign individual colors
        ScoreBoard scores = new ScoreBoard(new Color[]{Color.WHITE, Color.WHITE}, names, game);

        this.scores = game.getHealth();

        scoreView.addScoreboard(scores);
    }

    /**
     * Setzt die Namen der Spieler
     *
     * @param names Ein Array mit den Namen der Spieler
     */
    public void setPlayerNames(String[] names) {
        this.names = names;
    }


    /**
     * Aktualisiert die UI-Elemente des HUD mit den aktuellen Daten.
     */
    private void updateUIElements() {
        if (player0BalanceLabel != null) {
            player0BalanceLabel.setText("$" + player0Balance);
        }

        if (player1BalanceLabel != null) {
            player1BalanceLabel.setText("$" + player1Balance);
        }

        healthPlayer0Label.setText("" + healthPlayer0);
        healthPlayer1Label.setText("" + healthPlayer0);

        // ToDo weitere UI-Elemente aktualisieren...
    }

    /**
     * setzt und aktualisiert den Rundenzähler
     */
    public void setRoundCounter() {
        currentRoundLabel.setText("Runde: " + roundCounter++);
    }

    /**
     * Gibt den InputHandler zurück
     *
     * @return Der InputHandler für das HUD
     */
    public InputHandler getInputHandler() {
        return inputHandler;
    }

    /**
     * Gibt den InputProcessor zurück
     *
     * @return Der InputProcessor für das HUD
     */
    public InputProcessor getInputProcessor() {
        return inputMultiplexer;
    }

    /**
     * Zeichnet das HUD und die ScoreView
     */
    public void draw() {
        stage.getViewport().apply(true);
        stage.draw();
        if (scoreView != null) {
            scoreView.draw();
        }
    }

    /**
     * Aktualisiert den InputHandler und die Stage basierend auf dem Zeitdelta
     *
     * @param delta Das Zeitdelta seit dem letzten Frame
     */
    protected void tick(float delta) {
        inputHandler.tick(delta);
        stage.act(delta);
    }

    /**
     * Erstellt ein Popup für den Spielzugwechsel mit der angegebenen Umrandungsfarbe
     *
     * @param outlinecolor Die Farbe für die Umrandung
     */
    public void createTurnChangePopup(Color outlinecolor) {
        drawImagePopup(new ImagePopup(turnChangeSprite, turnChangeDuration / renderingSpeed, turnChangeSprite.getRegionWidth() * 8, turnChangeSprite.getRegionHeight() * 8, outlinecolor), false);
    }

    /**
     * Zeichnet das gegebene Bildpopup und positioniert es entsprechend den Parametern
     *
     * @param image  Das zu zeichnende Bildpopup
     * @param center Bestimmt, ob das Popup zentriert oder oben platziert wird
     */
    public void drawImagePopup(ImagePopup image, boolean center) {
        if (turnPopupContainer.hasChildren()) {
            turnPopupContainer.removeActorAt(0, false);
        }
        turnPopupContainer.setActor(image);
        if (center) {
            turnPopupContainer.center();
        } else {
            turnPopupContainer.top();
        }
        image.setScaling(Scaling.fit);
        turnPopupContainer.fill();
        turnPopupContainer.maxSize(image.getWidthForContainer(), image.getHeightForContainer());
    }

    /**
     * Ändert die Größe des Viewports basierend auf der angegebenen Breite und Höhe
     *
     * @param width  Die neue Breite des Viewports
     * @param height Die neue Höhe des Viewports
     */
    public void resizeViewport(int width, int height) {
        stage.getViewport().update(width, height, true);
        if (scoreView != null) {
            scoreView.getViewport().update(width, height, true);
        }
    }

    /**
     * Gibt den UiMessenger zurück
     *
     * @return Der UiMessenger für die Kommunikation
     */
    public UiMessenger getUiMessenger() {
        return uiMessenger;
    }

    /**
     * Setzt die Rendering-Geschwindigkeit für das HUD
     *
     * @param speed Die neue Rendering-Geschwindigkeit
     */
    public void setRenderingSpeed(float speed) {
        inGameScreen.setRenderingSpeed(speed);
        inputHandler.turnChangeSpeedup(speed);
        this.renderingSpeed = speed;
    }

    /**
     * Setzt die verbleibende Zeit für den aktuellen Spielzug
     *
     * @param time Die verbleibende Zeit in Sekunden
     */
    public void setTurntimeRemaining(int time) {
        turnTimer.setCurrentTime(time);
    }

    /**
     * Startet den Spielzug-Timer mit der angegebenen Dauer in Sekunden
     *
     * @param seconds Die Dauer des Spielzug-Timers in Sekunden
     */
    public void startTurnTimer(int seconds) {
        setRoundCounter();
        turnTimer.startTimer(seconds);
    }

    /**
     * Stoppt den laufenden Spielzug-Timer
     */
    public void stopTurnTimer() {
        turnTimer.stopTimer();
    }

    /**
     * dispose für das Hud
     */
    @Override
    public void dispose() {
        stage.dispose();
    }

    public void clear() {
        stage.clear();
        mainVerticalGroup.clear();
        hudGroup.clear(); // Entferne alle Elemente aus der Hud-Gruppe
        for (Actor actor : hudGroup.getChildren()) {
            if (actor instanceof TextButton) {
                actor.remove(); // Entferne jeden TextButton aus der Hud-Gruppe
            }
        }
    }

    /**
     * Schaltet die Sichtbarkeit der Debug-Linien ein oder aus
     */
    public void toggleDebugOutlines() {
        this.debugVisible = !debugVisible;

        this.mainVerticalGroup.setDebug(debugVisible);
    }

    /**
     * Schaltet die Anzeige der Punktzahlen ein oder aus, sofern vorhanden
     */
    public void toggleScores() {
        if (scoreView != null) {
            scoreView.toggleEnabled();
        }
    }

    /**
     * Passt die Punktzahl für das angegebene Team im HUD an
     *
     * @param team  Das Team, dessen Punktzahl angepasst wird
     * @param score Die neue Punktzahl für das Team
     */
    public void adjustScores(int team, float score) {
        this.scores[team] = score;

        if (scoreView != null) {
            scoreView.adjustScores(scores);
        }
    }

    /**
     * Zeigt das Ergebnis des Spiels an, einschließlich eines Hintergrundbilds und Popup-Fensters
     *
     * @param won    Gibt an, ob das Team gewonnen hat
     * @param team   Das betroffene Team
     * @param isDraw Gibt an, ob das Spiel unentschieden endete
     */
    public void gameEnded(boolean won, int team, boolean isDraw) {

        //create a pixel with a set color that will be used as Background
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        //set the color to black
        pixmap.setColor(0, 0, 0, 0.5f);
        pixmap.fill();
        pixmap.dispose();

        //determine sprite
        if (isDraw) {
            gameInstance.setScreen(GADS.ScreenState.DRAWSCREEN, new RunConfig());
        } else if (won && team == 0) {
            gameInstance.setScreen(GADS.ScreenState.VICTORYSCREEN, new RunConfig());
        } else {
            gameInstance.setScreen(GADS.ScreenState.LOSSSCREEN, new RunConfig());
        }
    }

    /**
     * Entfernt das Popup am Start eines neuen Spielzugs
     */
    public void skipTurnStart() {
        if (turnPopupContainer.getActor() != null)
            turnPopupContainer.getActor().remove();
    }

    /**
     * Erstellt und gibt einen TextButton für die TileMap eines Teams zurück
     *
     * @param team    Das Team, zu dem der Button gehört
     * @param tileMap Die TileMap des Spiels
     * @return Der erstellte TextButton
     */
    private TextButton tileMapButton(int team, TileMap tileMap) {
        TextButton tileMapButton = new TextButton("", skin);

        tileMapButton.addListener(new ClickListener() {
            /**
             * Wird aufgerufen, wenn der Button geklickt wird
             *
             * @param event Das InputEvent
             * @param x Die x-Position der Berührung
             * @param y Die y-Position der Berührung
             * @param pointer Der Zeiger
             * @param button Die gedrückte Taste
             * @return true, wenn das Event konsumiert wird; false, wenn es weitergeleitet wird
             */
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                int posX = (int) ((x / tileMap.getTileSize()) * 10);
                int posY = (int) ((y / tileMap.getTileSize()) * 10);
                if (button == Input.Buttons.RIGHT && tileMap.getTile(posX, posY) == 0 && towerMaps.get(team)[posX][posY] == 1) {
                    closeSelectBox();
                    towerSellUpgrade = new SelectBox<>(skin);
                    towerSellUpgrade.setItems("Upgrade", "Verkaufen");
                    towerSellUpgrade.setSize(140, 20);
                    towerSellUpgrade.setPosition(tileMapButton.getX() + x, tileMapButton.getY() + y);
                    hudGroup.addActor(towerSellUpgrade);
                    towerSellUpgrade.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent changeEvent, Actor actor) {
                            String selectedItem = towerSellUpgrade.getSelected();
                            switch (selectedItem) {
                                case ("Upgrade"):
                                    inputHandler.playerFieldRightClicked(team, posX, posY, true, false);
                                case ("Verkaufen"):
                                    inputHandler.playerFieldRightClicked(team, posX, posY, false, true);
                                default:
                                    break;
                            }
                            if (towerSellUpgrade != null) {
                                towerSellUpgrade.remove();
                            }
                        }
                    });
                    return true;
                } else if (button == Input.Buttons.LEFT && tileMap.getTile(posX, posY) == 0 && towerMaps.get(team)[posX][posY] == 1) {
                    Skin skin = AssetContainer.MainMenuAssets.skin;
                    closeSelectBox();
                    fireModeSelectBox = new SelectBox<>(skin);
                    Tower.TargetOption[] targetOption = Tower.TargetOption.values();
                    fireModeSelectBox.setItems((Object[]) targetOption);
                    fireModeSelectBox.setSize(140, 20);
                    fireModeSelectBox.setPosition(tileMapButton.getX() + x, tileMapButton.getY() + y);
                    hudGroup.addActor(fireModeSelectBox);
                    fireModeSelectBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {
                            Tower.TargetOption targetOption;
                            targetOption = (Tower.TargetOption) fireModeSelectBox.getSelected();
                            inputHandler.playerFieldLeftClicked(team, posX, posY, null, targetOption);
                            if (fireModeSelectBox != null) {
                                fireModeSelectBox.remove();
                            }
                        }
                    });
                    return true;
                } else if (button == Input.Buttons.LEFT && tileMap.getTile(posX, posY) == 0) {
                    closeSelectBox();
                    Skin skin = AssetContainer.MainMenuAssets.skin;
                    towerSelectBox = new SelectBox<>(skin);
                    Tower.TowerType[] towerTypes = Tower.TowerType.values();
                    towerSelectBox.setItems(towerTypes);
                    towerSelectBox.setSize(140, 20);
                    towerSelectBox.setPosition(tileMapButton.getX() + x, tileMapButton.getY() + y);
                    hudGroup.addActor(towerSelectBox);
                    towerSelectBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {
                            Tower.TowerType towerType;
                            towerType = towerSelectBox.getSelected();
                            inputHandler.playerFieldLeftClicked(team, posX, posY, towerType, null);
                            if (team >= 0 && team < towerMaps.size()) {
                                towerMaps.get(team)[posX][posY] = 1;
                            }
                            if (towerSelectBox != null) {
                                towerSelectBox.remove();
                            }
                        }
                    });
                    return true;
                }
                return false;
            }
        });
        this.tileMap = tileMap;
        return tileMapButton;
    }

    /**
     * Setzt den InputProcessor auf die Stage, um das HUD anzuzeigen.
     */
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    private void closeSelectBox() {
        if (towerSelectBox != null) {
            towerSelectBox.remove();
        }
        if (towerSellUpgrade != null) {
            towerSellUpgrade.remove();
        }
        if (fireModeSelectBox != null) {
            fireModeSelectBox.remove();
        }
    }

    /**
     * Initialisiert das Bankguthaben des angegebenen Spielers und setzt die Variable "balance" in "initBankbalance" gleich dem Wert aus dem "PlayerState" oben.
     *
     * @param playerID Die ID des Spielers, dessen Bankguthaben initialisiert werden soll.
     */
    public void initBankBalance(int playerID) {
        // Zugriff auf den Spielerzustand des GameState
        PlayerState[] playerStates = gameState.getPlayerStates();

        // Überprüfung der Spieler-ID und Aktualisierung des entsprechenden Bankguthabens
        if (playerID == 0) {
            player0Balance = playerStates[0].getMoney();
        } else if (playerID == 1) {
            player1Balance = playerStates[1].getMoney();
        }
        updateUIElements();
    }


    /**
     * Setzt das Bankguthaben für den angegebenen Spieler
     *
     * @param playerID Die ID des Spielers (aktuell nur 0 oder 1)
     * @param balance  Das neue Bankguthaben
     */
    public void setBankBalance(int playerID, int balance) {
        if (playerID == 0) {
            player0Balance = balance;

        } else if (playerID == 1) {
            player1Balance = balance;
        }
        updateUIElements();
    }

    /**
     * Initialisiert Leben des Spielers und aktualisiert die entsprechende Lebensleiste sowie visuelle Elemente
     * @param playerID Die ID des Spielers, dessen Leben initialisiert werden soll
     */
    public void initPlayerHealth(int playerID) {
        float[] playerHealths = gameState.getHealth();
        if (playerID == 0) {
            healthBarPlayer0.setRange(0, (int) playerHealths[0]);
            healthBarPlayer0.setValue((int) playerHealths[0]);
            healthBarPlayer0.updateVisualValue();
            healthPlayer0 = (int) playerHealths[0];
        } else if (playerID == 1) {
            healthBarPlayer1.setRange(0, (int) playerHealths[1]);
            healthBarPlayer1.setValue((int) playerHealths[1]);
            healthBarPlayer1.updateVisualValue();
            healthPlayer1 = (int) playerHealths[1];
        }
        updateUIElements();
    }

    /**
     * Aktualisiert den Gesundheitswert eines Spielers.
     * @param playerID Die ID des Spielers.
     * @param health   Der neue Gesundheitswert.
     */
    public void setPlayerHealth(int playerID, int health) {
        if (playerID == 0) {
            healthBarPlayer0.setValue(health);
            healthBarPlayer0.updateVisualValue();
            healthPlayer0 = health;
        } else if (playerID == 1) {
            healthBarPlayer1.setValue(health);
            healthBarPlayer1.updateVisualValue();
            healthPlayer1 = health;
        }
        updateUIElements();
    }
}