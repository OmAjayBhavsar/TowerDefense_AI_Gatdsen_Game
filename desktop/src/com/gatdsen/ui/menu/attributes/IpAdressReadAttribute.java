package com.gatdsen.ui.menu.attributes;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Align;
import com.gatdsen.manager.run.config.RunConfiguration;

public class IpAdressReadAttribute extends Attribute{
    @Override
    public Actor getContent(Skin skin) {
        Table IpAddressTable = new Table();
        Label textLabelIP = new Label("IP-Adresse: ", skin);
        textLabelIP.setAlignment(Align.right);
        Label textLabelIPAdress = new Label("192.168.2.2", skin);//toDo get ip address
        textLabelIP.setAlignment(Align.center);
        IpAddressTable.columnDefaults(0).width(100);
        IpAddressTable.columnDefaults(1).width(100);
        IpAddressTable.add(textLabelIP).colspan(4).pad(10).right();
        IpAddressTable.add(textLabelIPAdress).colspan(4).pad(10).width(80).row();
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
