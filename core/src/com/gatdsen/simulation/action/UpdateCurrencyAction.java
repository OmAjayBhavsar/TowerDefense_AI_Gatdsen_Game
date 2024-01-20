package com.gatdsen.simulation.action;

/**
 * Spezialisierte Klasse von {@link TeamAction} die anzeigt, dass das Geld eines Teams aktualisiert wurde
 */
public class UpdateCurrencyAction extends TeamAction {
    private final int newCurrency;
    private final int newSpawnCurrency;

    /**
     * Speichert das Ereignis, dass das Geld eines Teams aktualisiert wurde
     *
     * @param delay       nicht-negativer zeitbasierter Offset zu seinem Elternteil in Sekunden
     * @param newCurrency neuer Geldstand
     * @param team        index des Teams
     */
    public UpdateCurrencyAction(float delay, int newCurrency, int newSpawnCurrency, int team) {
        super(delay, team);
        this.newCurrency = newCurrency;
        this.newSpawnCurrency = newSpawnCurrency;
    }

    /**
     * @return neuer Geldstand
     */
    public int getNewCurrency() {
        return newCurrency;
    }

    public int getNewSpawnCurrency() {
        return newSpawnCurrency;
    }

    @Override
    public String toString() {
        return "UpdateCurrencyAction{" +
                "newCurrency=" + newCurrency +
                ", newSpawnCurrency=" + newSpawnCurrency +
                '}';
    }
}
