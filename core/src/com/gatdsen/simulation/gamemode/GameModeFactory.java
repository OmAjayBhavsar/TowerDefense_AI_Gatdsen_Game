package com.gatdsen.simulation.gamemode;

import com.gatdsen.simulation.GameMode;
import com.gatdsen.simulation.gamemode.campaign.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

public final class GameModeFactory {

    private static GameModeFactory instance = null;

    public static GameModeFactory getInstance() {
        if (instance == null) {
            instance = new GameModeFactory();
        }
        return instance;
    }

    private final Map<String, Supplier<? extends GameMode>> gameModeConstructors = new HashMap<>();

    private GameModeFactory() {
        List<Supplier<? extends GameMode>> gameModes = List.of(
            ReplayMode::new,
            NormalMode::new,
            ChristmasMode::new,
            CampaignMode1_1::new, CampaignMode1_2::new,
            CampaignMode2_1::new, CampaignMode2_2::new,
            CampaignMode3_1::new, CampaignMode3_2::new,
            CampaignMode4_1::new, CampaignMode4_2::new,
            CampaignMode5_1::new, CampaignMode5_2::new,
            CampaignMode6_1::new, CampaignMode6_2::new,
            ExamAdmissionMode::new,
            TournamentPhase1Mode::new,
            TournamentPhase2Mode::new
        );
        for (Supplier<? extends GameMode> gameMode : gameModes) {
            for (String identifier : gameMode.get().getIdentifiers()) {
                gameModeConstructors.put(formatIdentifier(identifier), gameMode);
            }
        }
    }

    private static String formatIdentifier(String identifier) {
        return identifier.toLowerCase().replace(" ", "").replace("_", "");
    }

    public GameMode[] getAvailableGameModes() {
        Set<Supplier<? extends GameMode>> uniqueGameModes = Set.copyOf(gameModeConstructors.values());
        return uniqueGameModes.stream()
            .map(Supplier::get)
            .filter(GameMode::isAvailable)
            .toArray(GameMode[]::new);
    }

    public GameMode[] getAvailableCampaigns() {
        return Stream.of(getAvailableGameModes())
            .filter(gameMode -> gameMode.getType() == GameMode.Type.CAMPAIGN)
            .toArray(GameMode[]::new);
    }

    public GameMode getGameMode(String identifier) {
        Supplier<? extends GameMode> constructor = gameModeConstructors.get(formatIdentifier(identifier));
        if (constructor == null) {
            return null;
        }
        GameMode gameMode = constructor.get();
        if (!gameMode.isAvailable()) {
            return null;
        }
        return gameMode;
    }
}
