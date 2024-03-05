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
import com.gatdsen.manager.run.config.RunConfiguration;
import com.gatdsen.simulation.GameState;
import com.gatdsen.simulation.PlayerState;
import com.gatdsen.simulation.Tower;
import com.gatdsen.ui.GADS;
import com.gatdsen.ui.assets.AssetContainer;
import com.gatdsen.ui.hud.*;

/**
 * Class for taking care of the User Interface.
 * Input Handling during the game.
 * Displaying health, inventory
 */
public class Hud implements Disposable{

    private static Stage stage;
    private final InputHandler inputHandler;
    private final InputMultiplexer inputMultiplexer;
    private final TurnTimer turnTimer;
    public final Table layoutTable;
    private final Container<ImagePopup> turnPopupContainer;
    private final InGameScreen inGameScreen;
    private final TextureRegion turnChangeSprite;
    private final float turnChangeDuration;
    private final UiMessenger uiMessenger;
    private float renderingSpeed = 1;
    private boolean debugVisible;
    private float[] scores;
    private String[] names;
    private final ScoreView scoreView;
    private TextButton nextRoundButton;
    private final Skin skin = AssetContainer.MainMenuAssets.skin;
    Viewport hudViewport;
    private int player0Balance = 100;
    private int player1Balance = 100;
    protected GADS gameInstance;
    private GameState gameState;
    private int health = 300;
    private ProgressBar healthBarPlayer0 = new ProgressBar(0, health, 1, false, skin);
    private ProgressBar healthBarPlayer1 = new ProgressBar(0, health, 1, false, skin);
    private int roundCounter = 1;
    private int healthPlayer0 = health;
    private int healthPlayer1 = health;
    public Group hudGroup = new Group();
    public TileMap tileMap;
    private SelectBox<Tower.TowerType> towerSelectBox;
    private Tower.TowerType towerType;

    /**
     * Initialisiert das HUD-Objekt
     *
     * @param ingameScreen Die Instanz der InGameScreen-Klasse
     * @param gameInstance Die gameInstance für das Spiel
     */
    public Hud(InGameScreen ingameScreen, GADS gameInstance) {

        this.gameInstance = gameInstance;
        this.inGameScreen = ingameScreen;
        hudViewport = new FitViewport(600, 400);
        this.uiMessenger = new UiMessenger(this);
        turnChangeDuration = 2;
        turnChangeSprite = AssetContainer.IngameAssets.turnChange;
        stage = new Stage(hudViewport);
        layoutTable = setupLayoutTable();
        inputHandler = setupInputHandler(ingameScreen, this);
        inputHandler.setUiMessenger(uiMessenger);
        turnTimer = new TurnTimer();
        turnPopupContainer = new Container<ImagePopup>();
        layoutHudElements();
        // Kombination von Eingaben von beiden Prozessoren (Spiel und UI)
        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(inputHandler); // für die Simulation benötigt
        inputMultiplexer.addProcessor(stage); // für die UI-Buttons benötigt
        stage.addActor(layoutTable);
        scoreView = new ScoreView(null);

        healthBarPlayer0.setValue(health);
        healthBarPlayer0.setAnimateDuration(0.25f);
        healthBarPlayer1.setValue(health);
        healthBarPlayer1.setAnimateDuration(0.25f);
        if (gameState != null){
            roundCounter = gameState.getTurn();
        }
        else roundCounter = 1;
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
     * Konfiguriert und gibt eine Tabelle für das Layout zurück
     *
     * @return Eine neu konfigurierte Table-Instanz
     */
    private Table setupLayoutTable() {
        Table table = new Table(AssetContainer.MainMenuAssets.skin);

        table.setFillParent(true);
        table.columnDefaults(0).width(100);
        table.columnDefaults(1).width(100);
        table.columnDefaults(2).width(100);
        table.columnDefaults(3).width(100);
        table.columnDefaults(4).width(100);
        table.columnDefaults(5).width(100);
        table.columnDefaults(6).width(100);
        table.center().top();
        return table;
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
     * Konfiguriert die HUD-Elemente und deren Anordnung
     */
    public void layoutHudElements() {
        float padding = 10;

        Label player0BalanceLabel = new Label("$" + player0Balance, skin);
        player0BalanceLabel.setAlignment(Align.center);
        Label player1BalanceLabel = new Label("$" + player1Balance, skin);
        player1BalanceLabel.setAlignment(Align.center);
        Label currentPlayer0 = new Label("Spieler 0", skin);
        currentPlayer0.setAlignment(Align.center);
        Label currentPlayer1 = new Label("Spieler 1", skin);
        currentPlayer1.setAlignment(Align.center);
        Label currentRoundLabel = new Label("Runde: " + roundCounter, skin);
        currentRoundLabel.setAlignment(Align.center);
        Label healthPlayer0Label = new Label("" + healthPlayer0 , skin);
        healthPlayer0Label.setAlignment(Align.center);
        Label healthPlayer1Label = new Label("" + healthPlayer1, skin);
        healthPlayer1Label.setAlignment(Align.center);

        Label invisibleLabel = new Label("", skin);
        nextRoundButton = new TextButton("Zug beenden", skin);
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
                layoutTable.clear();
                layoutHudElements();
            }
        });
        layoutTable.add(currentPlayer0).expandX();
        layoutTable.add(player0BalanceLabel).pad(padding).expandX();
        layoutTable.add(healthBarPlayer0).pad(padding).expandX();
        layoutTable.add(currentRoundLabel).align(Align.center).pad(padding).expandX();
        layoutTable.add(healthBarPlayer1).pad(padding).expandX();
        layoutTable.add(player1BalanceLabel).pad(padding).expandX();
        layoutTable.add(currentPlayer1).pad(padding).expandX().row();
        layoutTable.add(invisibleLabel).pad(padding).expandX();
        layoutTable.add(invisibleLabel).pad(padding).expandX();
        layoutTable.add(healthPlayer0Label).pad(padding).expandX();
        layoutTable.add(invisibleLabel).pad(padding).expandX();
        layoutTable.add(healthPlayer1Label).pad(padding).expandX().row();
        layoutTable.add(invisibleLabel);
        layoutTable.add(invisibleLabel);
        layoutTable.add(invisibleLabel);
        layoutTable.add(turnTimer).row();
        layoutTable.add(invisibleLabel);
        layoutTable.add(invisibleLabel);
        layoutTable.add(invisibleLabel);
        layoutTable.add(nextRoundButton).pad(padding).expandX().row();
        layoutTable.add(invisibleLabel);
    }

    /*
    /**
     * Erstellt einen FastForwardButton und gibt ihn zurück
     *
     * @param uiMessenger Der UiMessenger für die Kommunikation
     * @param speedUp     Die Geschwindigkeitssteigerung für die Schnellvorlauf-Funktion
     * @return Ein neues FastForwardButton-Objekt

    private FastForwardButton setupFastForwardButton(UiMessenger uiMessenger, float speedUp) {

        FastForwardButton button = new FastForwardButton(new TextureRegionDrawable(AssetContainer.IngameAssets.fastForwardButton),
                new TextureRegionDrawable(AssetContainer.IngameAssets.fastForwardButtonPressed),
                new TextureRegionDrawable(AssetContainer.IngameAssets.fastForwardButtonChecked),
                uiMessenger, speedUp);
        return button;
    }
    */

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
        roundCounter++;
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

    /**
     * Schaltet die Sichtbarkeit der Debug-Linien ein oder aus
     */
    public void toggleDebugOutlines() {
        this.debugVisible = !debugVisible;

        this.layoutTable.setDebug(debugVisible);
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
     * Passt die Punktzahlen im HUD basierend auf dem gegebenen Array an
     *
     * @param scores Ein Array mit den neuen Punktzahlen

    public void adjustScores(float[] scores) {
    this.scores = scores;

    if (scoreView != null) {
    scoreView.adjustScores(scores);
    }
    }
     */

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

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        //set the color to black
        pixmap.setColor(0, 0, 0, 0.5f);
        pixmap.fill();
        layoutTable.clear();
        layoutTable.setBackground(new TextureRegionDrawable(new Texture(pixmap)));
        pixmap.dispose();

        ImagePopup display;

        //determine sprite
        if (isDraw) {
            gameInstance.setScreen(GADS.ScreenState.DRAWSCREEN, new RunConfiguration());
        } else if (won && team == 0) {
            gameInstance.setScreen(GADS.ScreenState.VICTORYSCREEN, new RunConfiguration());
        } else {
            gameInstance.setScreen(GADS.ScreenState.LOSSSCREEN, new RunConfiguration());
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
     * Startet ein neues Spiel mit den gegebenen Parametern
     *
     * @param gameState             Der Zustand des neuen Spiels
     * @param arrayPositionTileMaps Die Positionen der TileMaps im Array
     * @param tileSize              Die Größe der Tiles
     * @param tileMap               Die TileMap des Spiels
     */
    public void newGame(GameState gameState, Vector2[] arrayPositionTileMaps, int tileSize, TileMap tileMap) {

        this.gameState = gameState;
        stage.addActor(hudGroup);

        hudViewport.setWorldWidth((float) ((gameState.getBoardSizeX() * 2 + 10) * 200) /10);
        hudViewport.setWorldHeight((float) ((gameState.getBoardSizeY() + 5) * 200) /10);

        int numberOfTeams = gameState.getPlayerCount();
        TextButton[] teamButtons;
        teamButtons = new TextButton[numberOfTeams];

        for (int i = 0; i < numberOfTeams; i++) {
            teamButtons[i] = tileMapButton(i, tileMap);
            teamButtons[i].setSize((gameState.getBoardSizeX() * tileSize) / 10.0f, (gameState.getBoardSizeY() * tileSize) / 10.0f);
            hudGroup.addActor(teamButtons[i]);
            teamButtons[i].setPosition((arrayPositionTileMaps[i].x) / 10.0f, (arrayPositionTileMaps[i].y) / 10.0f);
            teamButtons[i].setColor(Color.CLEAR);
            initPlayerHealth(i);
            initBankBalance(i);
        }
        layoutTable.setBackground((Drawable) null);
        if (turnPopupContainer.hasChildren()) {
            turnPopupContainer.removeActorAt(0, false);
        }
        setupScoreboard(gameState);
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
                if (button == Input.Buttons.RIGHT && tileMap.getTile(posX, posY) == 0) {
                    inputHandler.playerFieldRightClicked(team, posX, posY);
                    return true;
                } else if (button == Input.Buttons.LEFT && tileMap.getTile(posX, posY) == 0) {
                    Skin skin = AssetContainer.MainMenuAssets.skin;

                    if (towerSelectBox != null) {
                        towerSelectBox.remove();
                    }
                    towerSelectBox = new SelectBox<>(skin);


                    Tower.TowerType[] towerTypes = Tower.TowerType.values();
                    towerSelectBox.setItems(towerTypes);

                    towerSelectBox.setSize(140, 20);

                    towerSelectBox.setPosition(tileMapButton.getX() + x, tileMapButton.getY() +y);

                    hudGroup.addActor(towerSelectBox);

                    towerSelectBox.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {
                            towerType = towerSelectBox.getSelected();
                            inputHandler.playerFieldLeftClicked(team, posX, posY, towerType);
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

        // Layout leeren und HUD-Elemente aktualisieren
        layoutTable.clear();
        layoutHudElements();
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
        layoutTable.clear();
        layoutHudElements();
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
        layoutTable.clear();
        layoutHudElements();
    }

    /**
     * Setzt die Lebensanzeige des angegebenen Spielers und aktualisiert die entsprechende Lebensleiste sowie visuelle Elemente
     * @param playerID Die ID des Spielers, dessen Leben gesetzt werden soll.
     * @param health   Der neue Lebenswert für den Spieler.
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
        layoutTable.clear();
        layoutHudElements();
    }
}