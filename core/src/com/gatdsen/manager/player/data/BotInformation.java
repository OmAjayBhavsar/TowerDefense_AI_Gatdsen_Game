package com.gatdsen.manager.player.data;

public class BotInformation extends PlayerInformation {

    public final String studentName;
    public final int matrikel;

    public BotInformation(PlayerType type, String name, String studentName, int matrikel) {
        super(type, name);
        this.studentName = studentName;
        this.matrikel = matrikel;
    }
}
