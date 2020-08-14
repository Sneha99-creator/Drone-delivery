package com.mitchell.drone;

public class Coordinates {
    private Integer nsCoord;
    private Integer ewCoord;

    public Coordinates(Integer nsCoord, Integer ewCoord) {
        this.nsCoord = nsCoord;
        this.ewCoord = ewCoord;
    }

    public Integer getNsCoord() {
        return nsCoord;
    }

    public Integer getEwCoord() {
        return ewCoord;
    }

    @Override
    public String toString() {
        return "Coordinates{" +
                "nsCoord=" + nsCoord +
                ", ewCoord=" + ewCoord +
                '}';
    }
}
