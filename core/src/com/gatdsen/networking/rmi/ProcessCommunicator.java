package com.gatdsen.networking.rmi;

import com.gatdsen.networking.rmi.message.Message;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Die Schnittstelle für Javas Remote Method Invocation (RMI).
 * Klassen, die dieses Interface implementieren, können mit Java RMI dafür verwendet werden, um es einem Prozess
 * durch Exportieren dieses Remote-Objekts zu ermöglichen, Kommunikation von anderen Prozessen zu empfangen, die dieses
 * Remote-Objekt abrufen.
 */
public interface ProcessCommunicator extends Remote {

    /**
     * Kommuniziert die übergebene Nachricht vom aufrufenden Prozess an den Prozess, zu dem dieses Remote-Objekt
     * gehört.
     * Der aufrufende Prozess sendet die Nachricht über den RMI-Stub an den Prozess, der dieses Remote-Objekt
     * exportiert hat. Wie diese Nachricht dann weiterverarbeitet wird, ist von der Implementierung dieses
     * Interfaces bzw. der genauen Implementierung dieser Methode abhängig und geschieht innerhalb des ursprünglichen
     * Prozesses.
     * @param message Die zu kommunizierende Nachricht
     * @throws RemoteException Wird geworfen, wenn ein Fehler bei der Kommunikation auftritt
     */
    void communicate(Message message) throws RemoteException;
}