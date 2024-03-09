package com.gatdsen.networking.rmi.message;

public class ProcessCommunicatorShutdownRequest implements Message {

    @Override
    public Type getType() {
        return Type.ProcessCommunicatorShutdownRequest;
    }
}