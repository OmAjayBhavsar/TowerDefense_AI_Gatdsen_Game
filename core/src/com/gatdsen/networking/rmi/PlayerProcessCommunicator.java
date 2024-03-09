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
    private final ProcessCommunicator remoteCommunicatorStub;

    private PlayerExecutor playerExecutor;

    public PlayerProcessCommunicator(RMIRegistry registry, String localReferenceName, ProcessCommunicator remoteCommunicatorStub) {
        this.registry = registry;
        this.localReferenceName = localReferenceName;
        this.remoteCommunicatorStub = remoteCommunicatorStub;
        RMICommunicator.communicate(remoteCommunicatorStub, new ProcessCommunicatorSetupResponse());
        Runtime.getRuntime().addShutdownHook(new Thread(this::dispose));
    }

    @Override
    public void communicate(Message message) {
        switch (message.getType()) {
            case GameCreateRequest:
                GameCreateRequest gameCreateRequest = (GameCreateRequest) message;
                playerExecutor = new PlayerExecutor(gameCreateRequest.isDebug, gameCreateRequest.playerIndex, gameCreateRequest.playerClass);
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
