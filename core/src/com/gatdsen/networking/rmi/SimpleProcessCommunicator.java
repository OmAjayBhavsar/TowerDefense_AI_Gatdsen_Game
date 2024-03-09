package com.gatdsen.networking.rmi;

import com.gatdsen.networking.rmi.message.Message;
import com.gatdsen.networking.rmi.message.UnexpectedMessageException;

/**
 * Dies ist eine simple Implementierung des {@link ProcessCommunicator}-Interfaces, welches es ermöglicht, Nachrichten
 * von einem Prozess an einen anderen zu senden.
 * Statt einer festen Implementierung der Logik, wie bestimmte Nachrichten verarbeitet werden, bietet diese Klasse die
 * Möglichkeit einen Callback zu setzen, der aufgerufen wird, wenn eine Nachricht empfangen wird.
 */
public class SimpleProcessCommunicator implements ProcessCommunicator {

    private Message.MessageHandler messageHandler = null;

    /**
     * Setzt den Callback, der aufgerufen wird, wenn eine Nachricht empfangen wird.
     * @param messageHandler Der Callback
     */
    public void setMessageHandler(Message.MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    @Override
    public void communicate(Message message) {
        // Falls kein Callback gesetzt ist, wird eine Exception geworfen, damit eine Nachricht nicht unwissentlich
        // ignoriert wird.
        if (messageHandler == null) {
            throw new UnexpectedMessageException(message);
        }
        messageHandler.handle(message);
    }
}
