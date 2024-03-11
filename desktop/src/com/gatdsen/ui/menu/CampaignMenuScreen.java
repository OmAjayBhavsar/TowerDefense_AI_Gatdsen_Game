package com.gatdsen.ui.menu;

import com.gatdsen.ui.GADS;
import com.gatdsen.ui.menu.attributes.Attribute;
import com.gatdsen.ui.menu.attributes.MapAttribute;
import com.gatdsen.ui.menu.attributes.PlayerAttribute;

public class CampaignMenuScreen extends AttributeScreen{
    /**
     * Konstruktor für die Klasse AttributeScreen
     *
     * @param gameInstance Eine Instanz des GADS-Spiels
     */
    public CampaignMenuScreen(GADS gameInstance) {
        super(gameInstance);
    }

    @Override
    protected Attribute[] getAttributes() {
        return new Attribute[] {new PlayerAttribute(0), new MapAttribute()};
    }

    @Override
    String getTitelString() {
        return "Kampagne";
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
