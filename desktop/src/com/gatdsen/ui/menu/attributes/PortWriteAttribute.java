package com.gatdsen.ui.menu.attributes;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Align;
import com.gatdsen.manager.run.RunConfig;

public class PortWriteAttribute extends Attribute{
    @Override
    public Actor getContent(Skin skin) {
        Table portTable = new Table();
        Label textLabelPort = new Label("Port: ", skin);
        textLabelPort.setAlignment(Align.right);
        TextField portInputField = new TextField("", skin);
        portInputField.setAlignment(Align.center);
        portTable.columnDefaults(0).width(200);
        portTable.columnDefaults(1).width(200);
        portTable.add(textLabelPort).colspan(4).pad(10).right();
        portTable.add(portInputField).colspan(4).pad(10).width(200).row();
        return portTable;
    }

    @Override
    public RunConfig getConfig(RunConfig runConfig) {
        return null;
    }

    @Override
    public void setConfig(RunConfig runConfig) {

    }
}
