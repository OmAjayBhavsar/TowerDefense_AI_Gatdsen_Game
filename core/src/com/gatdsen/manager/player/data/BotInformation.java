package com.gatdsen.manager.player.data;

public class BotInformation extends PlayerInformation {

    private final String studentName;
    private final int matrikel;

    public BotInformation(PlayerType type, String name, String studentName, int matrikel) {
        super(type, name);
        this.studentName = studentName;
        this.matrikel = matrikel;
    }

    public String getStudentName() {
        return studentName;
    }

    public int getMatrikel() {
        return matrikel;
    }
}
