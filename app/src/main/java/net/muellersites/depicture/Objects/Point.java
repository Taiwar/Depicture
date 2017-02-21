package net.muellersites.depicture.Objects;

public class Point {
    public float x, y;
    public float dx, dy;

    public Point() {
    }

    public Point(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return x + ", " + y;
    }
}