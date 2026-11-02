package me.pintoadmin.termine;

import static com.googlecode.lanterna.TextColor.ANSI.DEFAULT;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFrame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class Main {
    public static Terminal terminal;
    public static Screen screen;
    public static TextGraphics g;

    private Minesweeper currentGame;

    void main() {
        try {
            terminal = new DefaultTerminalFactory().createTerminal();
            screen = new TerminalScreen(terminal);
            screen.startScreen();

            g = screen.newTextGraphics();
            g.setBackgroundColor(DEFAULT);
            g.setForegroundColor(TextColor.ANSI.WHITE);

            screen.setCursorPosition(null);

            screen.clear();
            g.fill(' ');

            new Startup();
            Thread.sleep(2000);

            currentGame = new Minesweeper();

            screen.refresh(Screen.RefreshType.COMPLETE);

            Thread resizeHandler =
                    new Thread(
                            () -> {
                                while (true) {
                                    TerminalSize newSize = screen.doResizeIfNecessary();
                                    if (newSize != null) {
                                        // Resize actions here
                                        try {
                                            screen.refresh(Screen.RefreshType.DELTA);
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                                }
                            });
            resizeHandler.start();

            Thread keyHandlers =
                    new Thread(
                            () -> {
                                while (true) {
                                    try {
                                        KeyStroke keystroke = screen.pollInput();
                                        if (keystroke == null) continue;
                                        if (keystroke.getKeyType() == KeyType.Escape) {
                                            screen.close();
                                            Thread.currentThread().interrupt();
                                            resizeHandler.interrupt();
                                            System.exit(0);
                                            break;
                                        } else {
                                            try {
                                                currentGame.handleInput(keystroke);
                                            } catch (Exception e) {
                                                if (e.getMessage() == null)
                                                    throw new RuntimeException(e);
                                                if (e.getMessage().equals("reset")) {
                                                    screen.clear();
                                                    currentGame =
                                                            new Minesweeper(currentGame.getSeed());
                                                    screen.refresh(Screen.RefreshType.COMPLETE);
                                                } else if (e.getMessage().equals("newseed")) {
                                                    screen.clear();
                                                    currentGame = new Minesweeper();
                                                    screen.refresh(Screen.RefreshType.COMPLETE);
                                                } else throw new RuntimeException(e);
                                            }
                                        }
                                    } catch (IOException e) {
                                        System.err.println(
                                                "Could not read terminal input: " + e.getMessage());
                                        e.printStackTrace();
                                    }
                                }
                            });
            keyHandlers.start();

            if (terminal instanceof SwingTerminalFrame swingTerminalFrame) {
                swingTerminalFrame.addWindowListener(
                        new WindowAdapter() {
                            @Override
                            public void windowClosing(WindowEvent e) {
                                try {
                                    keyHandlers.interrupt();
                                    resizeHandler.interrupt();
                                } catch (RuntimeException _) {
                                }
                                System.exit(0);
                            }
                        });
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
