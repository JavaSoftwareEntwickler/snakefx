package com.example.snakefx;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.Random;

public class Cibo {
    private int x, y;
    private final int SIZE = 10;
    private Rectangle rectangle;

    public Cibo(int maxWidth, int maxHeight) {
        Random rand = new Random();
        this.x = rand.nextInt(maxWidth / SIZE) * SIZE;
        this.y = rand.nextInt(maxHeight / SIZE) * SIZE;
        this.rectangle = new Rectangle(x, y, SIZE, SIZE);
        rectangle.setFill(Color.GREEN);
    }

    public void reset(int maxWidth, int maxHeight) {
        Random rand = new Random();
        this.x = rand.nextInt(maxWidth / SIZE) * SIZE;
        this.y = rand.nextInt(maxHeight / SIZE) * SIZE;
        this.rectangle.setX(x);
        this.rectangle.setY(y);
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
