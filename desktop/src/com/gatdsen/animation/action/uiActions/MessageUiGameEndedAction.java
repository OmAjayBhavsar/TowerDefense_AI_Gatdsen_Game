package com.gatdsen.animation.action.uiActions;

import com.badlogic.gdx.graphics.Color;
import com.gatdsen.ui.hud.UiMessenger;


/**
 * Animator Action for notifying the Ui that the Game is Over
 */
public class MessageUiGameEndedAction extends MessageUiAction{
	boolean draw;
	int team;

	public MessageUiGameEndedAction(float start, UiMessenger uiMessenger, int team) {
		super(start, uiMessenger);
		this.team = team;

	}
	public MessageUiGameEndedAction(float start,UiMessenger uiMessenger,boolean isDraw){
		super(start, uiMessenger);
		draw = isDraw;

	}

	@Override
	protected void runAction(float oldTime, float current) {
		uiMessenger.gameEnded(team,draw);
		endAction(oldTime);
	}
}
