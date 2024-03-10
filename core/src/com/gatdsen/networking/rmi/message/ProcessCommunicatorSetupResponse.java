package com.gatdsen.networking.rmi.message;

/**
 * Diese Nachricht wird vom Spielerprozess an den Spielprozess gesendet, wenn dieser erfolgreich eingerichtet wurde
 * und zur Kommunikation bereit ist.
 */
public final class ProcessCommunicatorSetupResponse implements Message {

    @Override
    public Type getType() {
        return Type.ProcessCommunicatorSetupResponse;
    }
}