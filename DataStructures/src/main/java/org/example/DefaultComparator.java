package org.example;

import java.util.Comparator;

public class DefaultComparator<T> implements Comparator<T> {
    @Override
    public int compare(T a, T b) {
        return ((Comparable<T>)a).compareTo(b);
    }
}
