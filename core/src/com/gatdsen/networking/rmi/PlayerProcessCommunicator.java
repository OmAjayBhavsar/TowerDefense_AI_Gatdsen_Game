package com.gatdsen.networking.rmi;

import com.gatdsen.manager.PlayerExecutor;
import com.gatdsen.manager.concurrent.RMICommunicator;
import com.gatdsen.manager.concurrent.RMIRegistry;
import com.gatdsen.manager.player.data.penalty.Penalty;
import com.gatdsen.networking.rmi.message.*;

import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class PlayerProcessCommunicator implements ProcessCommunicator {

    private final RMIRegistry registry;
    private final String localReferenceName;
    private ProcessCommunicator remoteCommunicatorStub = null;

    private PlayerExecutor playerExecutor;

    public PlayerProcessCommunicator(RMIRegistry registry, String localReferenceName) {
        this.registry = registry;
        this.localReferenceName = localReferenceName;
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
                playerExecutor = new PlayerExecutor(gameCreateRequest.isDebug, gameCreateRequest.playerId, gameCreateRequest.playerClass);
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
                Penalty penalty = playerExecutor.init(playerInitRequest.state, playerInitRequest.seed);
                RMICommunicator.communicate(remoteCommunicatorStub, new PlayerInitResponse(penalty));
                break;
            case PlayerExecuteTurnRequest:
                PlayerExecuteTurnRequest playerExecuteTurnRequest = (PlayerExecuteTurnRequest) message;
                playerExecutor.executeTurn(
                        playerExecuteTurnRequest.state,
                        command -> RMICommunicator.communicate(remoteCommunicatorStub, new PlayerCommandResponse(command))
                );
                break;
            case ProcessCommunicatorShutdownRequest:
                //dispose();
                break;
        }
    }

    private void dispose() {
        try {
            UnicastRemoteObject.unexportObject(this, true);
        } catch (NoSuchObjectException e) {
            throw new RuntimeException(e);
        }
        try {
            registry.unbind(localReferenceName);
        } catch (RemoteException | NotBoundException e) {
            throw new RuntimeException(e);
        }
    }
}
