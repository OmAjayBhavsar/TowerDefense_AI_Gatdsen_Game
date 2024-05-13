package com.gatdsen.simulation.gamemode;

import com.gatdsen.manager.player.handler.LocalPlayerHandlerFactory;

public class ExamAdmissionMode extends PlayableGameMode {

    public ExamAdmissionMode() {
        super();
        setMap("ExamAdmission", "ExamAdmission2", "ExamAdmission3", "ExamAdmission4");
        setPlayerFactory(1, LocalPlayerHandlerFactory.EXAM_ADMISSION_BOT);
    }

    @Override
    public String getDisplayName() {
        return "Exam Admission";
    }

    @Override
    public Type getType() {
        return Type.EXAM_ADMISSION;
    }

    @Override
    public String[] getIdentifiers() {
        return new String[]{
                String.valueOf(getType().ordinal()),
                "exam", "exam_admission", "admission"
        };
    }
}
