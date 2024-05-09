package com.gatdsen.simulation.gamemode;

import com.gatdsen.simulation.GameMode;

public class ExamAdmissionMode extends GameMode {
    public ExamAdmissionMode() {
        super();
        map = "ExamAdmission";
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
