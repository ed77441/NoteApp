package com.mynote.common;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.*;
import java.util.stream.Collectors;

public class KeyHandler implements KeyEventDispatcher {

    private Map<String, Runnable> funcList  =  new HashMap<>();
    private Map<Integer, Integer> keyStates = new HashMap<>();
    private int counter = 0;

    @Override
    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        if (updateKeyState(keyEvent)) {
            String newKeySequence = keyStates.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByValue())
                    .map(entry -> KeyEvent.getKeyText(entry.getKey()))
                    .collect(Collectors.joining(" "));

            Optional<Runnable> target = funcList.entrySet()
                    .stream()
                    .filter(entry -> newKeySequence.equals(entry.getKey()))
                    .map(Map.Entry::getValue)
                    .findFirst();

            target.ifPresent(Runnable::run);
        }
        return false;
    }

    private boolean updateKeyState(KeyEvent keyEvent) {
        int keyCode = keyEvent.getKeyCode();
        switch (keyEvent.getID()) {
            case KeyEvent.KEY_PRESSED:
                if (!keyStates.containsKey(keyCode)) {
                    keyStates.put(keyCode, counter++);
                    return true;
                }
                break;
            case KeyEvent.KEY_RELEASED:
                keyStates.remove(keyCode);
                if (keyStates.isEmpty()) {
                    counter = 0;
                }
                break;
            default: break;
        }
        return false;
    }

    public void bind(String keySequence, Runnable runner) {
        keySequence = String.join(" " , keySequence.split("\\s+"));
        funcList.put(keySequence, runner);
    }
}
