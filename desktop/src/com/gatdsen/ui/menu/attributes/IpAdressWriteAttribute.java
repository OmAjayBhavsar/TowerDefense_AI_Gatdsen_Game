package com.gatdsen.ui.menu.attributes;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.gatdsen.manager.run.config.RunConfiguration;

public class IpAdressWriteAttribute extends Attribute{
    @Override
    public Actor getContent(Skin skin) {
        Table IpAddressTable = new Table();
        Label textLabelIP = new Label("IP-Adresse: ", skin);
        textLabelIP.setAlignment(Align.right);
        TextField ipAddressInputField = new TextField("", skin);
        ipAddressInputField.setAlignment(Align.center);
        IpAddressTable.columnDefaults(0).width(100);
        IpAddressTable.columnDefaults(1).width(100);
        IpAddressTable.add(textLabelIP).colspan(4).pad(10).right();
        IpAddressTable.add(ipAddressInputField).colspan(4).pad(10).width(80).row();
        return IpAddressTable;
    }

    @Override
    public RunConfiguration getConfig(RunConfiguration runConfiguration) {
        return null;
    }

    @Override
    public void setConfig(RunConfiguration runConfiguration) {

    }
}
