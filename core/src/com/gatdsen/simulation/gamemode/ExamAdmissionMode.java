package com.gatdsen.simulation.gamemode;

import com.gatdsen.manager.player.handler.PlayerClassReference;
import com.gatdsen.simulation.GameMode;

public class ExamAdmissionMode extends GameMode {
    public ExamAdmissionMode() {
        super();
        map = "ExamAdmission";
        enemyBot = PlayerClassReference.EXAM_ADMISSION_BOT;
    }

    @Override
    public void setMap(String map) {
        throw new UnsupportedOperationException("The map of the exam admission mode can not be changed!");
    }

    @Override
    public boolean isExamAdmissionMode() {
        return true;
    }
}
