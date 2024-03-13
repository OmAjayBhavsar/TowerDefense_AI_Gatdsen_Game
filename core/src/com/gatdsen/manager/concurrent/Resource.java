package com.gatdsen.manager.concurrent;

import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Diese Klasse dient als Basisklasse für Ressourcen wie Threads oder Prozessen, die von verschiedenen Teilen des
 * Programms benötigt werden.
 */
abstract class Resource {

    /** Ein {@link CompletableFuture}, der erfüllt wird, sobald diese Ressource komplett beendet wurde */
    private final CompletableFuture<?> disposeFuture = new CompletableFuture<>();

    /**
     * @return Gibt true zurück, wenn die Ressource beendet wurde, ansonsten false
     */
    public boolean isDisposed() {
        return disposeFuture.isDone();
    }

    /**
     * Diese Methode wartet / blockiert den aktuellen Thread bis die Ressource beendet wurde.
     * @throws InterruptedException Wenn der Thread während des Wartens unterbrochen (interrupted) wird
     */
    public final void waitForDispose() throws InterruptedException {
        try {
            disposeFuture.get();
        } catch (ExecutionException | CancellationException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Hilfsmethode, die die Beendigung der Ressource signalisiert, indem sie den {@link CompletableFuture} erfüllt.
     */
    protected final void setDisposed() {
        disposeFuture.complete(null);
    }

    /**
     * Diese Methode startet das Beenden der Ressource.
     * Es gibt keine Garantie, dass die Ressource nach Aufruf der Methode sofort beendet sein muss.
     */
    public void dispose() {
    }

    /**
     * Diese Methode erzwingt das Beenden der Ressource.
     */
    public void forceDispose() {
    }
}
