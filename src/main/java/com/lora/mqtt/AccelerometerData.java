package com.lora.mqtt;

public class AccelerometerData {
    private final int x;
    private final int y;
    private final int z;

    public AccelerometerData(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getZ() { return z; }

    @Override
    public String toString() {
        return String.format("X: %d, Y: %d, Z: %d", x, y, z);
    }
}