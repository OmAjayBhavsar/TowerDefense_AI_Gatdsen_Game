package com.gatdsen.simulation.action;

/**
 * Spezialisierte Klasse von {@link Action} die anzeigt, dass ein Fehler aufgetreten ist
 */
public class ErrorAction extends Action{
    private final String errorMessage;

    /**
     * Speichert das Ereignis, dass ein Fehler aufgetreten ist
     *
     * @param errorMessage Fehlermeldung
     */
    public ErrorAction(String errorMessage) {
        super(0f);
        this.errorMessage = errorMessage;
    }

    /**
     * @return Fehlermeldung
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * @return String-Representation der ErrorAction
     */
    @Override
    public String toString() {
        return "ErrorAction{" +
                "errorMessage='" + errorMessage + '\'' +
                "} " + super.toString();
    }
}
