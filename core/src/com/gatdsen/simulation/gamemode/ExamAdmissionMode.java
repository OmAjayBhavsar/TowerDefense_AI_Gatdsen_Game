package com.gatdsen.simulation.gamemode;

public class ExamAdmissionMode extends PlayableGameMode {

    public ExamAdmissionMode() {
        super();
        map = "ExamAdmission";
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
