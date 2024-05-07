package com.gatdsen.animation.action.uiActions;

import com.gatdsen.ui.hud.UiMessenger;

/**
 * Animator Action for notifying the UI that a player has been disqualified
 */
public class MessageUiDisqualifyAction extends MessageUiAction {
    int team;
    String message;

    public MessageUiDisqualifyAction(float start, UiMessenger uiMessenger, int team, String message) {
        super(start, uiMessenger);
        this.team = team;
        this.message = message;
    }

    protected void runAction(float oldTime, float newTime) {
        uiMessenger.playerDisqualified(team, message);
        endAction(oldTime);
    }
}
