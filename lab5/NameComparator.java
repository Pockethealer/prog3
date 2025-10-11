package lab5;

import java.util.Comparator;

public class NameComparator implements Comparator<Beer> {
    public int compare(Beer b1, Beer b2) {
        return b1.getName().compareTo(b2.getName());
    }
}
