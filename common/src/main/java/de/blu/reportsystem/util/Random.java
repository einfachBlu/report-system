package de.blu.reportsystem.util;

public final class Random {

    public static int randomRange(int min, int max){
        return (int) (min + Math.round(Math.random() * (max - min)));
    }
}
