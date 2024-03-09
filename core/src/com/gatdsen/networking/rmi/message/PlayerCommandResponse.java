package com.gatdsen.networking.rmi.message;

import com.gatdsen.manager.command.Command;

public final class PlayerCommandResponse implements Message {

    public final Command command;

    public PlayerCommandResponse(Command command) {
        this.command = command;
    }

    @Override
    public Type getType() {
        return Type.PlayerCommandResponse;
    }
}
