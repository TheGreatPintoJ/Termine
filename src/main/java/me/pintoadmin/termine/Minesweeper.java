package me.pintoadmin.termine;

import com.googlecode.lanterna.input.KeyStroke;
import java.util.Random;

public class Minesweeper {
    private final long seed;

    public Minesweeper() {
        this(new Random().nextLong());
    }

    public Minesweeper(long seed) {
        this.seed = seed;
    }

    public void handleInput(KeyStroke keyStroke) {}

    public long getSeed() {
        return seed;
    }
}
