package com.lucianms.utils;

/**
 * @author izarooni
 */
public class Location {

    private int x;
    private int y;

    public Location() {
        this(0, 0);
    }

    public Location(Location loc) {
        this(loc.x, loc.y);
    }

    public Location(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return String.format("Location{x=%d, y=%d}", x, y);
    }

    public double distance(Location location) {
        double dx = location.x - this.x;
        double dy = location.y - this.y;
        return ((dx * dx) + (dy * dy));
    }

    public Location getLocation() {
        return new Location(x, y);
    }

    public Location add(int x, int y) {
        this.x += x;
        this.y += y;
        return this;
    }

    public Location subtract(int x, int y) {
        this.x -= x;
        this.y -= y;
        return this;
    }

    public Location set(int x, int y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public Location set(Location loc) {
        this.x = loc.x;
        this.y = loc.y;
        return this;
    }

    public int getX() {
        return x;
    }

    public Location setX(int x) {
        this.x = x;
        return this;
    }

    public int getY() {
        return y;
    }

    public Location setY(int y) {
        this.y = y;
        return this;
    }

    public Location addX(int x) {
        this.x += x;
        return this;
    }

    public Location addY(int y) {
        this.y += y;
        return this;
    }
}
