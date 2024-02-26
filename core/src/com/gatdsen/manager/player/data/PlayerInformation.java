package com.gatdsen.manager.player.data;

import com.gatdsen.manager.player.Bot;
import com.gatdsen.manager.player.Player;

import java.io.Serializable;

public class PlayerInformation implements Serializable {

    public final Player.PlayerType type;
    public final String name;

    public PlayerInformation(Player.PlayerType type, String name) {
        this.type = type;
        this.name = name;
    }

    public static PlayerInformation fromPlayer(Player player) {
        if (player instanceof Bot) {
            Bot bot = (Bot) player;
            return new BotInformation(bot.getType(), bot.getName(), bot.getStudentName(), bot.getMatrikel());
        }
        return new PlayerInformation(player.getType(), player.getName());
    }
}
