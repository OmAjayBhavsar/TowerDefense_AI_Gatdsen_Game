package com.gatdsen.networking.rmi;

import com.gatdsen.manager.PlayerExecutor;
import com.gatdsen.manager.concurrent.RMICommunicator;
import com.gatdsen.manager.concurrent.RMIRegistry;
import com.gatdsen.manager.concurrent.ResourcePool;
import com.gatdsen.manager.player.data.penalty.Penalty;
import com.gatdsen.networking.rmi.message.*;

import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PlayerProcessCommunicator implements ProcessCommunicator {

    private final RMIRegistry registry;
    private final String localReferenceName;
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private ProcessCommunicator remoteCommunicatorStub = null;

    private PlayerExecutor playerExecutor = null;

    private boolean isDisposed = false;

    public PlayerProcessCommunicator(RMIRegistry registry, String localReferenceName) {
        this.registry = registry;
        this.localReferenceName = localReferenceName;
        ProcessHandle parentProcess = ProcessHandle.current().parent().orElse(null);
        executor.scheduleAtFixedRate(
            () -> {
                if (parentProcess != null && parentProcess.isAlive()) {
                    return;
                }
                dispose();
            },
                0,
                500,
                TimeUnit.MILLISECONDS
        );
        Runtime.getRuntime().addShutdownHook(new Thread(this::dispose));
    }

    public void setRemoteCommunicatorStub(ProcessCommunicator remoteCommunicatorStub) {
        if (this.remoteCommunicatorStub != null) {
            throw new IllegalStateException("RemoteCommunicatorStub already set");
        }
        this.remoteCommunicatorStub = remoteCommunicatorStub;
        RMICommunicator.communicate(remoteCommunicatorStub, new ProcessCommunicatorSetupResponse());
    }

    @Override
    public void communicate(Message message) {
        if (remoteCommunicatorStub == null) {
            throw new UnexpectedMessageException(message);
        }
        switch (message.getType()) {
            case GameCreateRequest:
                GameCreateRequest gameCreateRequest = (GameCreateRequest) message;
                if (playerExecutor != null) {
                    playerExecutor.dispose(true);
                }
                playerExecutor = new PlayerExecutor(
                        gameCreateRequest.isDebug,
                        gameCreateRequest.playerId,
                        gameCreateRequest.playerClassReference.getPlayerClass()
                );
                RMICommunicator.communicate(
                        remoteCommunicatorStub,
                        new GameCreateResponse(
                                playerExecutor.getPlayerInformation(),
                                playerExecutor.getPlayerClassAnalyzer().getSeedModifier()
                        )
                );
                break;
            case PlayerInitRequest:
                PlayerInitRequest playerInitRequest = (PlayerInitRequest) message;
                RMICommunicator.communicate(
                        remoteCommunicatorStub,
                        new PlayerInitResponse(playerExecutor.init(playerInitRequest.state, playerInitRequest.seed))
                );
                break;
            case PlayerExecuteTurnRequest:
                PlayerExecuteTurnRequest playerExecuteTurnRequest = (PlayerExecuteTurnRequest) message;
                playerExecutor.executeTurn(
                        playerExecuteTurnRequest.state,
                        commands -> RMICommunicator.communicate(remoteCommunicatorStub, new PlayerCommandResponse(commands))
                ).thenAccept(
                        penalty -> RMICommunicator.communicate(remoteCommunicatorStub, new PlayerExecuteTurnResponse(penalty))
                );
                break;
            case ProcessCommunicatorShutdownRequest:
                dispose();
                break;
        }
    }

    public void dispose() {
        synchronized (this) {
            if (isDisposed) {
                return;
            }
            isDisposed = true;
        }
        executor.shutdownNow();
        try {
            UnicastRemoteObject.unexportObject(this, true);
        } catch (NoSuchObjectException ignored) {
        }
        try {
            registry.unbind(localReferenceName);
        } catch (RemoteException | NotBoundException ignored) {
        }
        playerExecutor.dispose(false);
        ResourcePool.getInstance().dispose();
    }
}
