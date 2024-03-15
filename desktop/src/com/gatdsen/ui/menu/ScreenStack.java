package com.gatdsen.ui.menu;

import com.badlogic.gdx.Screen;

import java.util.Stack;

public class ScreenStack {
    private final Stack<Screen> screenStack;

    public ScreenStack() {
        screenStack = new Stack<>();
    }

    public void pushScreen(Screen screen) {
        if (!screenStack.isEmpty()) {
            screenStack.peek().pause();
        }
        screenStack.push(screen);
        screen.show();
    }

    public void popScreen() {
        if (!screenStack.isEmpty()) {
            screenStack.pop().hide();
        }

        if (!screenStack.isEmpty()) {
            screenStack.peek().resume();
        }
    }

    public void setScreen(Screen screen) {
        popAllScreens();
        pushScreen(screen);
    }

    private void popAllScreens() {
        while (!screenStack.isEmpty()) {
            screenStack.pop().hide();
        }
    }
}
