package com.gatdsen.networking.rmi.message;

public final class ProcessCommunicatorSetupResponse implements Message {

    @Override
    public Type getType() {
        return Type.ProcessCommunicatorSetupResponse;
    }
}