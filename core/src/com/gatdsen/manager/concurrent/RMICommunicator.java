package com.gatdsen.manager.concurrent;

import com.gatdsen.networking.rmi.SimpleProcessCommunicator;
import com.gatdsen.networking.rmi.ProcessCommunicator;
import com.gatdsen.networking.rmi.message.Message;
import com.gatdsen.networking.rmi.message.ProcessCommunicatorShutdownRequest;
import com.gatdsen.networking.rmi.message.UnexpectedMessageException;

import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Diese Klasse repräsentiert einen RMI-Kommunikator, der für die Kommunikation mit einem anderen Prozess verwendet
 * werden kann.
 */
public final class RMICommunicator extends Resource {

    /** Das RMI-Registry, der für die Kommunikation verwendet wird, indem an dieses die Remote Objekte gebunden werden */
    private final RMIRegistry registry;
    /** Der Name, unter dem der Kommunikator dieses Prozesses in der Registry registriert wird */
    private final String localReferenceName;
    /** Der Kommunikator dieses Prozesses */
    private final SimpleProcessCommunicator localCommunicatorObject;
    /** Der Stub des Kommunikators des anderen Prozesses */
    private ProcessCommunicator remoteCommunicatorStub;

    /** Ein {@link CompletableFuture}, das abgeschlossen wird, wenn der Kommunikator dieses Prozesses eingerichtet wurde */
    private final CompletableFuture<?> setupFuture = new CompletableFuture<>();

    public RMICommunicator(RMIRegistry registry, String localReferenceName, String remoteReferenceName) {
        this.registry = registry;
        this.localReferenceName = localReferenceName;
        localCommunicatorObject = new SimpleProcessCommunicator();
        localCommunicatorObject.setMessageHandler(message -> {
            if (message.getType() != Message.Type.ProcessCommunicatorSetupResponse) {
                throw new UnexpectedMessageException(message, Message.Type.ProcessCommunicatorSetupResponse);
            }
            try {
                remoteCommunicatorStub = (ProcessCommunicator) registry.lookup(remoteReferenceName);
            } catch (NotBoundException | RemoteException e) {
                throw new RuntimeException(e);
            }
            localCommunicatorObject.setMessageHandler(null);
            setupFuture.complete(null);
        });
        try {
            registry.rebind(localReferenceName, UnicastRemoteObject.exportObject(localCommunicatorObject, 0));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Setzt den Callback, der aufgerufen wird, wenn eine Nachricht empfangen wird. Falls der Kommunikator noch nicht
     * eingerichtet wurde, blockiert diese Methode so lange, bis der Kommunikator fertig eingerichtet ist und der
     * Callback gesetzt werden kann.
     * Siehe auch {@link SimpleProcessCommunicator#setMessageHandler(Message.Handler)}
     * @param messageHandler Der Callback
     */
    public void setMessageHandler(Message.Handler messageHandler) {
        try {
            blockUntilSetup();
        } catch (InterruptedException e) {
            return;
        }
        localCommunicatorObject.setMessageHandler(messageHandler);
    }

    /**
     * Sendet eine Nachricht an den Kommunikationspartner. Falls der Kommunikator noch nicht eingerichtet wurde,
     * blockiert diese Methode so lange, bis der Kommunikator fertig eingerichtet ist und die Nachricht gesendet werden
     * kann.
     * @param message Die zu sendende Nachricht
     */
    public void communicate(Message message) {
        try {
            blockUntilSetup();
        } catch (InterruptedException e) {
            return;
        }
        communicate(remoteCommunicatorStub, message);
    }

    /**
     * Falls der Kommunikator noch nicht eingerichtet wurde, blockiert diese Methode den aktuellen Thread so lange, bis
     * der Kommunikator fertig eingerichtet ist.
     * @throws InterruptedException Falls der Thread unterbrochen wird, während er wartet / blockiert
     */
    private void blockUntilSetup() throws InterruptedException {
        if (!setupFuture.isDone()) {
            try {
                setupFuture.get();
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Beendet diesen Kommunikator, indem dem Kommunikator das Signal zum Beenden gegeben wird und das eigene
     * Remote-Objekt aus der Registry entfernt wird.
     */
    public void dispose() {
        if (isDisposed() || registry.isDisposed()) {
            return;
        }
        communicate(new ProcessCommunicatorShutdownRequest());
        try {
            UnicastRemoteObject.unexportObject(localCommunicatorObject, true);
        } catch (NoSuchObjectException ignored) {
        }
        try {
            registry.unbind(localReferenceName);
        } catch (RemoteException | NotBoundException ignored) {
        }
        setDisposed();
    }

    /**
     * Hilfsmethode, um eine Nachricht an einen Kommunikator zu senden.
     * @param targetCommunicator Der Kommunikator, an den die Nachricht gesendet werden soll
     * @param message Die zu sendende Nachricht
     */
    public static void communicate(ProcessCommunicator targetCommunicator, Message message) {
        try {
            targetCommunicator.communicate(message);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
