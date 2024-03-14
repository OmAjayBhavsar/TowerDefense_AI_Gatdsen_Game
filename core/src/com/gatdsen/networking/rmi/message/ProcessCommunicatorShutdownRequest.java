package com.gatdsen.networking.rmi.message;

/**
 * Diese Nachricht wird vom Spielprozess an einen Spielerprozess gesendet, um diesen zur Terminierung aufzufordern.
 */
public class ProcessCommunicatorShutdownRequest implements Message {

    @Override
    public Type getType() {
        return Type.ProcessCommunicatorShutdownRequest;
    }
}