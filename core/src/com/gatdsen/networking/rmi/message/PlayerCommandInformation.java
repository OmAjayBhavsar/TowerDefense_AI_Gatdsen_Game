package com.gatdsen.networking.rmi.message;

import com.gatdsen.manager.command.Command;

public class PlayerCommandInformation implements Message {

    public final Command command;

    public PlayerCommandInformation(Command command) {
        this.command = command;
    }
}
