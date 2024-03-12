package com.gatdsen.manager.player.data;

import com.gatdsen.manager.player.Bot;
import com.gatdsen.manager.player.Player;

import java.io.Serializable;

public class PlayerInformation implements Serializable {

    private final PlayerType type;
    private final String name;

    public PlayerInformation(PlayerType type, String name) {
        this.type = type;
        this.name = name;
    }

    public PlayerType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public static PlayerInformation fromPlayer(Player player) {
        PlayerType type = PlayerType.fromPlayer(player);
        if (player instanceof Bot) {
            Bot bot = (Bot) player;
            return new BotInformation(type, bot.getName(), bot.getStudentName(), bot.getMatrikel());
        }
        return new PlayerInformation(type, player.getName());
    }
}
