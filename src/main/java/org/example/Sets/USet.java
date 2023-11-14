package org.example.Sets;

public interface USet<T> extends Iterable<T> {
    int size();
    boolean add(T x);
    T remove(T x);
    T find(T x);
    void clear();
}
