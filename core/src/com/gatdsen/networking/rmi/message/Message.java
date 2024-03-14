package com.gatdsen.networking.rmi.message;

import java.io.Serializable;

/**
 * Dieses Interface repräsentiert eine Nachricht, die zwischen Parteien wie bspw. zwei verschiedenen Prozessen zur
 * Kommunikation ausgetauscht werden können.
 */
public interface Message extends Serializable {

    /**
     * Dieser Enum repräsentiert den Typ einer Nachricht.
     */
    enum Type {
        ProcessCommunicatorSetupResponse,
        ProcessCommunicatorShutdownRequest,

        GameCreateRequest,
        GameCreateResponse,

        PlayerInitRequest,
        PlayerInitResponse,

        PlayerExecuteTurnRequest,
        PlayerCommandResponse,
        PlayerExecuteTurnResponse
    }

    /**
     * Dieses Interface kann zur Repräsentation eines Callbacks genutzt werden, der aufgerufen wird, wenn eine Nachricht
     * empfangen wird und behandelt werden muss.
     */
    interface Handler {
        void handle(Message message);
    }

    /**
     * Gibt den Typ der Nachricht zurück.
     * @return Der Typ der Nachricht
     */
    Type getType();
}
