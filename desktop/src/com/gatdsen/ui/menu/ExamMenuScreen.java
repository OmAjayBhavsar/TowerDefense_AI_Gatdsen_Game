package com.gatdsen.ui.menu;

import com.gatdsen.ui.GADS;
import com.gatdsen.ui.menu.attributes.Attribute;
import com.gatdsen.ui.menu.attributes.PlayerAttribute;

public class ExamMenuScreen extends AttributeScreen{
    /**
     * Konstruktor für die Klasse AttributeScreen
     *
     * @param gameInstance Eine Instanz des GADS-Spiels
     */
    public ExamMenuScreen(GADS gameInstance) {
        super(gameInstance);
    }

    @Override
    protected Attribute[] getAttributes() {
        return new Attribute[] {new PlayerAttribute(0)};
    }

    @Override
    String getTitelString() {
        return "Klausurzulassung";
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
