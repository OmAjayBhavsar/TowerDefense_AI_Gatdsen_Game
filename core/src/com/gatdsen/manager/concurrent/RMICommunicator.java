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

public final class RMICommunicator {

    public final RMIRegistry registry;
    public final String localReferenceName;
    public final String remoteReferenceName;
    private final SimpleProcessCommunicator localCommunicatorObject;
    private ProcessCommunicator remoteCommunicatorStub;

    public RMICommunicator(RMIRegistry registry, String localReferenceName, String remoteReferenceName) {
        this.registry = registry;
        this.localReferenceName = localReferenceName;
        this.remoteReferenceName = remoteReferenceName;
        localCommunicatorObject = new SimpleProcessCommunicator();
        CompletableFuture<?> future = new CompletableFuture<>();
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
            future.complete(null);
        });
        try {
            registry.rebind(localReferenceName, UnicastRemoteObject.exportObject(localCommunicatorObject, 0));
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        try {
            future.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setMessageHandler(Message.Handler messageHandler) {
        localCommunicatorObject.setMessageHandler(messageHandler);
    }

    public void communicate(Message message) {
        communicate(remoteCommunicatorStub, message);
    }

    public void dispose() {
        communicate(remoteCommunicatorStub, new ProcessCommunicatorShutdownRequest());
        try {
            UnicastRemoteObject.unexportObject(localCommunicatorObject, true);
        } catch (NoSuchObjectException ignored) {
        }
        try {
            registry.unbind(localReferenceName);
        } catch (RemoteException | NotBoundException ignored) {
        }
    }

    public static void communicate(ProcessCommunicator targetCommunicator, Message message) {
        try {
            targetCommunicator.communicate(message);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
