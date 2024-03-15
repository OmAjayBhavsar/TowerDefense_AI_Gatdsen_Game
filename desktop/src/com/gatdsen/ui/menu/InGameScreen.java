package com.gatdsen.ui.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.*;
import com.gatdsen.animation.Animator;
import com.gatdsen.animation.AnimatorCamera;
import com.gatdsen.manager.*;
import com.gatdsen.manager.run.Run;
import com.gatdsen.manager.run.RunConfiguration;
import com.gatdsen.simulation.GameState;
import com.gatdsen.simulation.action.Action;
import com.gatdsen.simulation.action.ActionLog;
import com.gatdsen.ui.ConfigScreen;
import com.gatdsen.ui.GADS;
import com.gatdsen.ui.assets.AssetContainer;
import com.gatdsen.ui.debugView.DebugView;

/**
 * Der Screen welcher ein aktives Spiel anzeigt.
 */
public class InGameScreen extends ConfigScreen implements AnimationLogProcessor {

    private final Manager manager;
    private Viewport gameViewport;
    private float worldWidth = 50 * 200;
    private float worldHeight = 30 * 200;

    private float renderingSpeed = 1;

    //sollte das HUD von GADS verwaltet werden?
    private Hud hud;
    private Animator animator;
    private final GADS gameManager;
    private Run run;

    private DebugView debugView;

    public InGameScreen(GADS instance) {

        gameManager = instance;
        gameViewport = new FitViewport(worldWidth, worldHeight + 700);

        hud = new Hud(this, gameManager);

        debugView = new DebugView(AssetContainer.MainMenuAssets.skin);

        setupInput();

        manager = Manager.getManager();
        animator = new Animator(gameViewport, hud.getUiMessenger());
    }

    @Override
    protected void setRunConfiguration(RunConfiguration runConfiguration) {
        //aktualisiere die Run-Konfiguration
        super.setRunConfiguration(runConfiguration);
        this.runConfiguration.gui = true;
        this.runConfiguration.animationLogProcessor = this;
        this.runConfiguration.inputProcessor = hud.getInputHandler();

        run = manager.startRun(this.runConfiguration);
        if (run == null) {
            throw new RuntimeException("Das Spiel kann nicht mit einer ungültigen Run-Konfiguration gestartet werden!");
        }
    }

    //wird aufgerufen, wenn der Bildschirm zum Hauptbildschirm von GADS wird
    @Override
    public void show() {
        hud.show();
        animator.show();
    }

    public void setRenderingSpeed(float speed) {
        //negative deltaTime ist nicht erlaubt
        if (speed >= 0) this.renderingSpeed = speed;
    }

    @Override
    public void render(float delta) {
        hud.tick(delta);
        animator.render(renderingSpeed * delta);
        hud.draw();
        debugView.draw();
    }

    @Override
    public void init(GameState state, String[] playerNames, String[][] skins) {
        //ToDo das Spiel startet, entferne Wartebildschirm usw.

        int worldWidth = (state.getBoardSizeX() * 2 + 10) * 200;
        int worldHeight = (state.getBoardSizeY() + 5) * 200;

        gameViewport.setWorldWidth(worldWidth);
        gameViewport.setWorldHeight(worldHeight);

        animator.init(state, playerNames, skins);

        int tileSize = animator.playerMaps[0].getTileSize();

        Vector2[] positionTileMaps = new Vector2[]{animator.playerMaps[0].getPos(), animator.playerMaps[1].getPos()};

        hud.setPlayerNames(playerNames);
        hud.setHudViewport(worldWidth, worldHeight);
        hud.init(state, positionTileMaps, tileSize, animator.playerMaps[0]);
    }

    /**
     * Leitet das ActionLog an den Animator zur Verarbeitung weiter
     *
     * @param log Queue aller {@link Action animationsbezogenen Aktionen}
     */
    public void animate(ActionLog log) {
        animator.animate(log);
        debugView.add(log);
    }


    @Override
    public void awaitNotification() {
        animator.awaitNotification();
    }

    @Override
    public void resize(int width, int height) {
        animator.resize(width, height);
        hud.resizeViewport(width, height);
        gameViewport.update(width, height);
        debugView.getViewport().update(width, height);

    }

    @Override
    public void pause() {
        animator.pause();
    }

    @Override
    public void resume() {
        animator.resume();
    }

    @Override
    public void hide() {
        animator.hide();
    }

    /**
     * Wird aufgerufen, wenn die Anwendung beendet wird oder derzeit, wenn Escape gedrückt wird, um zum Menü zurückzukehren. Nicht die beste, aber derzeit schnellste Methode.
     */
    @Override
    public void dispose() {
        hud.clear();
        manager.stop(run);
        gameManager.setScreen(GADS.ScreenState.MAINSCREEN, null);
    }

    public void shutdown() {
        hud.dispose();
        manager.stop(run);
        gameManager.setScreen(GADS.ScreenState.MAINSCREEN, null);
    }



    public void setupInput() {

        //animator als Actor?
        //simulation als Actor?
        Gdx.input.setInputProcessor(hud.getInputProcessor());

    }

    /**
     * Konvertiert Viewport-/Bildschirmkoordinaten in Welt-/Spielpositionen
     *
     * @param coordinates zu konvertieren.
     * @return Vektor mit Weltkoordinate
     */
    public Vector2 toWorldCoordinates(Vector2 coordinates) {
        Vector3 position = gameViewport.unproject(new Vector3(coordinates.x, coordinates.y, 0));
        return new Vector2(position.x, position.y);
    }

    //dieser Abschnitt behandelt die Eingabe
    public void processInputs(float[] ingameCameraDirection, float zoomPressed) {
        AnimatorCamera camera = animator.getCamera();
        camera.setDirections(ingameCameraDirection);
        camera.setZoomPressed(zoomPressed);
    }

    public void resetCamera() {
        animator.getCamera().resetCamera();
    }

    public void toggleCameraMove() {
        animator.getCamera().toggleCanMoveToVector();
    }

    public void toggleDebugView() {
        debugView.toggleDebugView();
        hud.toggleDebugOutlines();
    }

    public void moveCameraByOffset(Vector2 offset) {
        animator.getCamera().moveByOffset(offset);
    }

    /**
     * Ruft die Funktion des AnimatorCamera zum Zoomen auf.
     *
     * @param zoom Wert, der zum Zoomen hinzugefügt werden soll
     */
    public void zoomCamera(float zoom) {
        AnimatorCamera camera = animator.getCamera();
        camera.addZoomPercent(zoom);
    }

    public void skipTurnStart() {
        hud.skipTurnStart();
    }
}