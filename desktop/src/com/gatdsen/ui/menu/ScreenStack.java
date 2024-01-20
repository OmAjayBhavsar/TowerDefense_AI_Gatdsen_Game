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
            // Pause the current top screen if it exists
            screenStack.peek().pause();
        }
        screenStack.push(screen);
        screen.show();
    }

    public void popScreen() {
        if (!screenStack.isEmpty()) {
            // Dispose the current top screen before popping it
            screenStack.pop().dispose();
        }

        if (!screenStack.isEmpty()) {
            // Resume the new top screen if it exists
            screenStack.peek().resume();
        }
    }

    public void setScreen(Screen screen) {
        popAllScreens();
        pushScreen(screen);
    }

    private void popAllScreens() {
        while (!screenStack.isEmpty()) {
            screenStack.pop().dispose();
        }
    }
}
