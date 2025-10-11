package lab5;

import java.io.*;

public class Beer {
    private String name, jelleg;
    private double alkoholfok;

    public Beer(String n, String j, double f) {
        this.name = n;
        this.jelleg = j;
        this.alkoholfok = f;
    }

    public String getName() {
        return this.name;
    }

    public String getJelleg() {
        return this.jelleg;
    }

    public double getErosseg() {
        return this.alkoholfok;
    }

    public String toString() {
        return name + " " + jelleg + " " + alkoholfok;
    }
}
