package me.pintoadmin.termine;

import com.googlecode.lanterna.graphics.*;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.*;
import java.util.Random;
import java.util.function.*;

public class Minesweeper {
    private final Screen screen = Main.screen;
    private final TextGraphics g = Main.g;
    private final long seed;

    private final Supplier<Integer> W = () -> g.getSize().getColumns();
    private final Supplier<Integer> H = () -> g.getSize().getRows();

    int[] selection = {0, 0};

    public Minesweeper() {
        this(new Random().nextLong());
    }

    public Minesweeper(long seed) {
        this.seed = seed;

        drawGame();
    }

    private void drawGame() {
        int W = this.W.get(), H = this.H.get();
        screen.clear();
        Main.drawText(W / 2, 0, "Minesweeper");
    }

    public void handleInput(KeyStroke keyStroke) {}

    public long getSeed() {
        return seed;
    }
}
