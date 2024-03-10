package com.gatdsen.networking.rmi.message;

/**
 * Diese Exception kann von {@link Message.Handler} geworfen werden, wenn eine
 * Nachricht empfangen wurde, die nicht erwartet wurde.
 */
public final class UnexpectedMessageException extends RuntimeException {

    /**
     * @param message Die unerwartete Nachricht
     * @param expectedTypes Die Nachrichtentypen, die erwartet wurden
     */
    public UnexpectedMessageException(Message message, Message.Type... expectedTypes) {
        super(
                "Got message of type \"" + message.getType().name() +
                "\", but only expected one of: " + String.join(", ", getMessageTypeNames(expectedTypes))
        );
    }

    /**
     * @param message Die unerwartete Nachricht
     */
    public UnexpectedMessageException(Message message) {
        super(
                "Got message of type \"" + message.getType().name() + "\", but none was expected."
        );
    }

    /**
     * Gib die Namen der gegebenen Nachrichtentypen als String-Array zurück.
     * @param types Die Nachrichtentypen
     * @return Die Namen der Nachrichtentypen
     */
    private static String[] getMessageTypeNames(Message.Type[] types) {
        String[] typeNames = new String[types.length];
        for (int i = 0; i < types.length; i++) {
            typeNames[i] = types[i].name();
        }
        return typeNames;
    }
}
