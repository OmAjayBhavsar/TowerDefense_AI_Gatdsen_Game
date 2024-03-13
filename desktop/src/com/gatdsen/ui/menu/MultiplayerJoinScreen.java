package com.gatdsen.ui.menu;

import com.gatdsen.ui.GADS;
import com.gatdsen.ui.menu.attributes.Attribute;
import com.gatdsen.ui.menu.attributes.IpAdressWriteAttribute;
import com.gatdsen.ui.menu.attributes.PlayerAttribute;
import com.gatdsen.ui.menu.attributes.PortWriteAttribute;

public class MultiplayerJoinScreen extends AttributeScreen{
    /**
     * Konstruktor für die Klasse AttributeScreen
     *
     * @param gameInstance Eine Instanz des GADS-Spiels
     */
    public MultiplayerJoinScreen(GADS gameInstance) {
        super(gameInstance);
    }

    @Override
    protected Attribute[] getAttributes() {
        return new Attribute[]{new PlayerAttribute(1), new IpAdressWriteAttribute(), new PortWriteAttribute()};
    }

    @Override
    String getTitelString() {
        return "Mehrspieler Spiel beitreten";
    }

    @Override
    GADS.ScreenState getNext() {
        return GADS.ScreenState.INGAMESCREEN;
    }

    @Override
    GADS.ScreenState getPrev() {
        return GADS.ScreenState.MAINSCREEN;
    }
}